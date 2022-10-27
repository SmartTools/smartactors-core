package info.smart_tools.smartactors.scheduler.actor.impl.refresher;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.actor.impl.EntryStorage;
import info.smart_tools.smartactors.scheduler.actor.impl.exceptions.CancelledLocalEntryRequestException;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.IRemoteEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerEntryFilterException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service that synchronizes entries between remote and local storage (downloads entries from remote storage periodically).
 */
public class EntryStorageRefresher implements ISchedulerStorageRefresher {
    private final EntryStorage entryStorage;
    private final IRemoteEntryStorage remoteEntryStorage;

    private final IQueue<ITask> taskQueue;
    private ITimerTask timerTask;
    private ITimer timer;
    private ITime time;

    private int maxPageSize;
    private int minPageSize;
    private int maxLocalEntries;
    private long refreshRepeatInterval;
    private long refreshAwakeInterval;
    private long refreshSuspendInterval;
    private long refreshStart;
    private long refreshStop;

    private final IFieldName maxPageSizeFN;
    private final IFieldName minPageSizeFN;
    private final IFieldName maxLocalEntriesFN;
    private final IFieldName refreshRepeatIntervalFN;
    private final IFieldName refreshAwakeIntervalFN;
    private final IFieldName refreshSuspendIntervalFN;

    private IObject lastDownloadedState;

    private final IFieldName entryIdFN;

    private final ITask downloadTask = activeRefresherTask(this::doDownloadIteration);
    private final ITask localRefreshTask = activeRefresherTask(this::doRefreshLocalStorage);

    private final Lock stateLock = new ReentrantLock();
    private boolean isStarted = false;

    /**
     * The constructor.
     *
     * <p>
     * Interval values should satisfy the following inequality:
     * {@code 0 < refreshRepeatInterval <= refreshAwakeInterval < refreshSuspendInterval}
     * </p>
     *
     * @param entryStorage              the entry storage
     * @param remoteEntryStorage        the remote entry storage
     * @param refreshRepeatInterval     interval between refresher executions in milliseconds
     * @param refreshAwakeInterval      time (in milliseconds after current execution start) to awake entries scheduled until
     * @param refreshSuspendInterval    time (in milliseconds after current execution start) to suspend entries scheduled after
     * @param maxPageSize               maximal size of pages downloaded from remote storage
     * @param minPageSize               minimal size of pages downloaded from remote storage
     * @param maxLocalEntries           maximal amount of entries stored in local storage
     * @throws ResolutionException if error occurs resolving any dependency
     * @throws InvalidArgumentException if {@code entryStorage} or {@code remoteEntryStorage} are {@code null}
     * @throws InvalidArgumentException if interval values do not satisfy requirements described above
     * @throws InvalidArgumentException if {@code maxPageSize} is not a positive value
     */
    public EntryStorageRefresher(final EntryStorage entryStorage,
                                 final IRemoteEntryStorage remoteEntryStorage,
                                 final long refreshRepeatInterval,
                                 final long refreshAwakeInterval,
                                 final long refreshSuspendInterval,
                                 final int maxPageSize,
                                 final int minPageSize,
                                 final int maxLocalEntries)
            throws ResolutionException, InvalidArgumentException {
        if (null == entryStorage) {
            throw new InvalidArgumentException("Entry storage should not be null.");
        }

        if (null == remoteEntryStorage) {
            throw new InvalidArgumentException("Remote entry storage should not be null.");
        }

        verifyParameters(refreshRepeatInterval, refreshAwakeInterval, refreshSuspendInterval, maxPageSize, minPageSize, maxLocalEntries);

        this.entryStorage = entryStorage;
        this.remoteEntryStorage = remoteEntryStorage;

        this.refreshRepeatInterval = refreshRepeatInterval;
        this.refreshAwakeInterval = refreshAwakeInterval;
        this.refreshSuspendInterval = refreshSuspendInterval;

        this.maxPageSize = maxPageSize;
        this.minPageSize = minPageSize;
        this.maxLocalEntries = maxLocalEntries;

        this.taskQueue = IOC.resolve(Keys.getKeyByName("task_queue"));

        this.entryIdFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "entryId");

        timer = IOC.resolve(Keys.getKeyByName("timer"));
        time = IOC.resolve(Keys.getKeyByName("time"));

        maxPageSizeFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "maxPageSize");
        minPageSizeFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "minPageSize");
        maxLocalEntriesFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "maxLocalEntries");
        refreshAwakeIntervalFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "refreshAwakeInterval");
        refreshRepeatIntervalFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "refreshRepeatInterval");
        refreshSuspendIntervalFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "refreshSuspendInterval");
    }

    private void verifyParameters(final long refreshRepeatIntervalParam,
                                  final long refreshAwakeIntervalParm,
                                  final long refreshSuspendIntervalParam,
                                  final int maxPageSizeParam,
                                  final int minPageSizeParam,
                                  final int maxLocalEntriesParam)
            throws InvalidArgumentException {
        if (!(
                (0 < refreshRepeatIntervalParam) &&
                        (refreshRepeatIntervalParam <= refreshAwakeIntervalParm) &&
                        (refreshAwakeIntervalParm < refreshSuspendIntervalParam)
        )) {
            throw new InvalidArgumentException(String.format(
                    "Invalid interval values: RRI = %d, RAI = %d, RSI = %d; should be: 0 < RRI <= RAI < RSI.",
                    refreshRepeatIntervalParam, refreshAwakeIntervalParm, refreshSuspendIntervalParam));
        }

        if (minPageSizeParam <= 0 || maxPageSizeParam < minPageSizeParam) {
            throw new InvalidArgumentException(String.format("Invalid page sizes: %d, %d.", minPageSizeParam, maxPageSizeParam));
        }

        if (maxLocalEntriesParam <= 0) {
            throw new InvalidArgumentException(String.format("Invalid maximal amount of local entries: %d.", maxLocalEntriesParam));
        }
    }

    private Number readNum(final IObject object, final IFieldName fieldName, final Number def)
            throws ReadValueException, InvalidArgumentException {
        Number v = (Number) object.getValue(fieldName);

        if (null == v) {
            return def;
        }

        return v;
    }

    @Override
    public void configure(final IObject conf)
            throws ReadValueException, InvalidArgumentException {
        stateLock.lock();

        try {
            long rri = readNum(conf, refreshRepeatIntervalFN, refreshRepeatInterval).longValue();
            long rai = readNum(conf, refreshAwakeIntervalFN, refreshAwakeInterval).longValue();
            long rsi = readNum(conf, refreshSuspendIntervalFN, refreshSuspendInterval).longValue();
            int minPS = readNum(conf, minPageSizeFN, minPageSize).intValue();
            int maxPS = readNum(conf, maxPageSizeFN, maxPageSize).intValue();
            int maxLE = readNum(conf, maxLocalEntriesFN, maxLocalEntries).intValue();

            verifyParameters(rri, rai, rsi, maxPS, minPS, maxLE);

            this.refreshAwakeInterval = rai;
            this.refreshRepeatInterval = rri;
            this.refreshSuspendInterval = rsi;
            this.minPageSize = minPS;
            this.maxPageSize = maxPS;
            this.maxLocalEntries = maxLE;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void start() throws ServiceStartException, IllegalServiceStateException {
        startAfter(time.currentTimeMillis());
    }

    @Override
    public void stop() throws ServiceStopException, IllegalServiceStateException {
        stateLock.lock();
        try {
            if (!this.isStarted) {
                throw new IllegalServiceStateException("The refresher is not started.");
            }

            timerTask.cancel();

            this.isStarted = false;
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Stop the refresher after some moment of time.
     *
     * The difference to calling {@link #stop()} at that moment is that refresher will not download any entries scheduled after that moment.
     *
     * @param stopTime    time to stop after
     * @throws ServiceStopException when error occurs stopping the refresher
     * @throws IllegalServiceStateException if the refresher is not running
     */
    @Override
    public void stopAfter(final long stopTime) throws ServiceStopException, IllegalServiceStateException {
        stateLock.lock();
        try {
            if (!this.isStarted) {
                throw new IllegalServiceStateException("The refresher is not started.");
            }

            this.refreshStop = stopTime;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void startAfter(final long startTime) throws ServiceStartException, IllegalServiceStateException {
        stateLock.lock();
        try {
            if (this.isStarted) {
                throw new IllegalServiceStateException("The refresher is already started.");
            }

            this.refreshStart = startTime;
            this.refreshStop = Long.MAX_VALUE;

            this.timerTask = timer.schedule(activeRefresherTask(this::startRefresh), refreshStart);

            this.isStarted = true;
        } catch (TaskScheduleException e) {
            throw new ServiceStartException(e);
        } finally {
            stateLock.unlock();
        }
    }

    private void startRefresh() throws TaskExecutionException {
        try {
            lastDownloadedState = null;
            taskQueue.put(downloadTask);
        } catch (InterruptedException e) {
            throw new TaskExecutionException("Unexpected interrupt while refreshing scheduler entry storage.", e);
        }
    }

    private void doDownloadIteration() throws TaskExecutionException {
        try {
            boolean cont = true;
            try {
                int nAllowed = maxLocalEntries - entryStorage.countLocalEntries();

                if (nAllowed <= 0) {
                    cont = false;
                    return;
                }

                List<IObject> entries = remoteEntryStorage.downloadEntries(
                        Math.min(refreshStart + refreshRepeatInterval, refreshStop),
                        lastDownloadedState,
                        Math.min(maxPageSize, Math.max(minPageSize, nAllowed)));

                for (IObject entryState : entries) {
                    String id = (String) entryState.getValue(entryIdFN);

                    try {
                        ISchedulerEntry localEntry = entryStorage.getLocalEntry(id);

                        if (null == localEntry) {
                            if (entryStorage.getFilter().testRestore(entryState)) {
                                ISchedulerEntry newEntry = IOC.resolve(Keys.getKeyByName("restore scheduler entry"), entryState, entryStorage);
                                remoteEntryStorage.weakSaveEntry(newEntry);
                            }
                        } else {
                            entryStorage.notifyActive(localEntry);
                        }
                    } catch (CancelledLocalEntryRequestException e) {
                        continue;
                    }

                    lastDownloadedState = entryState;

                    if (--nAllowed <= 0) {
                        cont = false;
                        return;
                    }
                }

                cont = !entries.isEmpty();
            } finally {
                taskQueue.put(cont ? downloadTask : localRefreshTask);
            }
        } catch (Exception e) {
            throw new TaskExecutionException(e);
        }
    }

    private void doRefreshLocalStorage() throws TaskExecutionException {
        try {
            try {
                entryStorage.refresh(refreshStart + refreshAwakeInterval, refreshStart + refreshSuspendInterval);
            } finally {
                refreshStart = refreshStart + refreshRepeatInterval;
                timerTask.reschedule(refreshStart);

                if (refreshStart > refreshStop) {
                    stop();
                }
            }
        } catch (EntryStorageAccessException | EntryScheduleException | TaskScheduleException | ServiceStopException
                | SchedulerEntryFilterException e) {
            throw new TaskExecutionException(e);
        } catch (IllegalServiceStateException ignore) {
            // Refresher is already stopped manually, before timeout;
            // this is impossible due to state lock being acquired in #activeRefresherTask
        }
    }

    private ITask activeRefresherTask(final ITask task) {
        return () -> {
            stateLock.lock();
            try {
                if (!isStarted) {
                    return;
                }

                task.execute();
            } finally {
                stateLock.unlock();
            }
        };
    }
}
