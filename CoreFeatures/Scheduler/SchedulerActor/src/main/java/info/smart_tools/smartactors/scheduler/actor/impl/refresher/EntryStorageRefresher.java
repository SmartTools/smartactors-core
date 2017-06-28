package info.smart_tools.smartactors.scheduler.actor.impl.refresher;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartupException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.scheduler.actor.impl.EntryStorage;
import info.smart_tools.smartactors.scheduler.actor.impl.exceptions.CancelledLocalEntryRequestException;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.IRemoteEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.IDelayedSynchronousService;
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
public class EntryStorageRefresher implements IDelayedSynchronousService {
    private final EntryStorage entryStorage;
    private final IRemoteEntryStorage remoteEntryStorage;

    private final IQueue<ITask> taskQueue;
    private ITimerTask timerTask;
    private ITimer timer;
    private ITime time;

    private int pageSize;
    private long refreshRepeatInterval;
    private long refreshAwakeInterval;
    private long refreshSuspendInterval;
    private long refreshStart;
    private long refreshStop;

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
     * @param pageSize                  size of pages downloaded from remote storage
     * @throws ResolutionException if error occurs resolving any dependency
     * @throws InvalidArgumentException if {@code entryStorage} or {@code remoteEntryStorage} are {@code null}
     * @throws InvalidArgumentException if interval values do not satisfy requirements described above
     * @throws InvalidArgumentException if {@code pageSize} is not a positive value
     */
    public EntryStorageRefresher(final EntryStorage entryStorage,
                                 final IRemoteEntryStorage remoteEntryStorage,
                                 final long refreshRepeatInterval,
                                 final long refreshAwakeInterval,
                                 final long refreshSuspendInterval,
                                 final int pageSize)
            throws ResolutionException, InvalidArgumentException {
        if (null == entryStorage) {
            throw new InvalidArgumentException("Entry storage should not be null.");
        }

        if (null == remoteEntryStorage) {
            throw new InvalidArgumentException("Remote entry storage should not be null.");
        }

        if (!(
                (0 < refreshRepeatInterval) &&
                (refreshRepeatInterval <= refreshAwakeInterval) &&
                (refreshAwakeInterval < refreshSuspendInterval)
        )) {
            throw new InvalidArgumentException(String.format(
                    "Invalid interval values: RRI = %d, RAI = %d, RSI = %d; should be: 0 < RRI <= RAI < RSI.",
                    refreshRepeatInterval, refreshAwakeInterval, refreshSuspendInterval));
        }

        if (pageSize <= 0) {
            throw new InvalidArgumentException("Invalid page size.");
        }

        this.entryStorage = entryStorage;
        this.remoteEntryStorage = remoteEntryStorage;

        this.refreshRepeatInterval = refreshRepeatInterval;
        this.refreshAwakeInterval = refreshAwakeInterval;
        this.refreshSuspendInterval = refreshSuspendInterval;

        this.pageSize = pageSize;

        this.taskQueue = IOC.resolve(Keys.getOrAdd("task_queue"));

        this.entryIdFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "entryId");

        timer = IOC.resolve(Keys.getOrAdd("timer"));
        time = IOC.resolve(Keys.getOrAdd("time"));
    }

    @Override
    public void start() throws ServiceStartupException, IllegalServiceStateException {
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
    public void startAfter(final long startTime) throws ServiceStartupException, IllegalServiceStateException {
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
            throw new ServiceStartupException(e);
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
                List<IObject> entries = remoteEntryStorage.downloadEntries(
                        Math.min(refreshStart + refreshRepeatInterval, refreshStop),
                        lastDownloadedState,
                        pageSize);

                for (IObject entryState : entries) {
                    String id = (String) entryState.getValue(entryIdFN);

                    try {
                        ISchedulerEntry localEntry = entryStorage.getLocalEntry(id);

                        if (null == localEntry) {
                            if (entryStorage.getFilter().testRestore(entryState)) {
                                ISchedulerEntry newEntry = IOC.resolve(Keys.getOrAdd("restore scheduler entry"), entryState, entryStorage);
                                remoteEntryStorage.weakSaveEntry(newEntry);
                            }
                        } else {
                            entryStorage.notifyActive(localEntry);
                        }
                    } catch (CancelledLocalEntryRequestException e) {
                        continue;
                    }

                    lastDownloadedState = entryState;
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
            if (!isStarted) {
                return;
            }

            try {
                task.execute();
            } finally {
                stateLock.unlock();
            }
        };
    }
}
