package info.smart_tools.smartactors.scheduler_plugins.scheduler_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;
import info.smart_tools.smartactors.scheduler_plugins.scheduling_strategies_plugin.PluginSchedulingStrategies;
import info.smart_tools.smartactors.task.blocking_queue.BlockingQueue;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask_dispatcher.ITaskDispatcher;
import info.smart_tools.smartactors.task.interfaces.ithread_pool.IThreadPool;
import info.smart_tools.smartactors.task.task_dispatcher.TaskDispatcher;
import info.smart_tools.smartactors.task.thread_pool.ThreadPool;
import org.junit.Ignore;
import org.junit.Test;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@Ignore("Need rewrite tests w/o loading other plugins")
public class SchedulerIntegrationTest extends IOCInitializer {
    private static long SCHEDULE_MAX = 1000000;
    private static long SCHEDULE_INITIAL = 10000;
    private static int NEW_ENTRIES_PER_ENTRY = 1;

    private static long DELAY_BASE = 2000;
    private static long DELAY_MOD = 5;
    private static long DELAY_K = 5000;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

//    load(ConnectionOptionsPlugin.class);
//    load(PostgresConnectionPoolPlugin.class);
//    load(PostgresDBTasksPlugin.class);
//    load(PluginTimer.class);
//    load(PluginScheduler.class);
//    load(PluginSchedulingStrategies.class);


    private class CountSchedulerAction implements ISchedulerAction {
        @Override
        public void init(ISchedulerEntry entry, IObject args) throws SchedulerActionInitializationException {
            try {
                entry.getState().setValue(markerFieldName, args.getValue(markerFieldName));
            } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
                throw new SchedulerActionInitializationException("Error initializing test action.", e);
            }
        }

        @Override
        public void execute(ISchedulerEntry entry) throws SchedulerActionExecutionException {
            try {
                notifyExecuted(entry.getState().getValue(markerFieldName));

                for (int i = 0; i < NEW_ENTRIES_PER_ENTRY; i++) {
                    scheduleOne();
                }
            } catch (Exception e) {
                throw new SchedulerActionExecutionException("Error executing test action", e);
            }
        }
    }

    private AtomicLong nExecuted;
    private AtomicLong nScheduled;
    private boolean isCompleted;
    private final Object conpletionLock = new Object();
    private ConcurrentHashMap<Object, Long> sMap;
    private IFieldName markerFieldName;
    private ISchedulerService service;
    private ISchedulerEntryStorage storage;
    private CopyOnWriteArrayList<Object> duplicateMarkers;

    private void notifyExecuted(final Object marker) {
        long ne = nExecuted.incrementAndGet();
        long ns = nScheduled.get();

        Long sTime = sMap.remove(marker);

        if (null == sTime) {
            nExecuted.decrementAndGet();
            System.err.println(MessageFormat.format("Invalid or duplicate marker: {0}", marker));
            duplicateMarkers.add(marker);
            return;
        }

        if (ne % 20 == 0) {
            System.out.println(MessageFormat.format("Task {0}, ({1}/{2}) ok, delta={3}",
                    marker,
                    ne, ns,
                    System.currentTimeMillis() - sTime));

            if (ne % 9 == 0) {
                long nsa = ns - ne;
                try {
                    long nl = storage.listLocalEntries().size();

                    System.out.println(MessageFormat.format("Stored {0} of ~{1} (~{2}%) entries, {3} duplicate.",
                            nl, nsa,
                            100f * (((float) nl) / ((float) nsa)),
                            duplicateMarkers.size()
                            ));
                } catch (EntryStorageAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (ne == ns) {
            synchronized (conpletionLock) {
                isCompleted = true;
                conpletionLock.notifyAll();
            }
        }
    }

    private void scheduleOne() throws Exception {
        long idx = nScheduled.incrementAndGet();

        if (idx > SCHEDULE_MAX) {
            nScheduled.decrementAndGet();
            return;
        }

        long delay = DELAY_BASE + (idx % DELAY_MOD) * DELAY_K;
        Object marker = UUID.randomUUID().toString();
        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC).plus(delay, ChronoUnit.MILLIS);

        IObject entryArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                String.format("{" +
                        "   'strategy':'do once scheduling strategy'," +
                        "   'action': 'count scheduler action'," +
                        "   '_marker':'%s'," +
                        "   'save':true," +
                        "   'neverTooLate':true," +
                        "   'time':'%s'" +
                        "}", marker, time.toString())
                        .replace('\'','"'));

        IOC.resolve(Keys.getKeyByName("new scheduler entry"), entryArgs, storage);

        sMap.put(marker, time.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Override
    protected void registerMocks() throws Exception {
        IQueue<ITask> taskQueue = new BlockingQueue<>(new ArrayBlockingQueue<>(100000));
        IThreadPool threadPool = new ThreadPool(4);
        ITaskDispatcher taskDispatcher = new TaskDispatcher(taskQueue, threadPool, 500, 2);

        taskDispatcher.start();

        IOC.register(Keys.getKeyByName("task_queue"), new SingletonStrategy(taskQueue));

        nExecuted = new AtomicLong(0);
        nScheduled = new AtomicLong(0);
        isCompleted = false;
        sMap = new ConcurrentHashMap<>();

        IOC.register(Keys.getKeyByName("count scheduler action"), new SingletonStrategy(new CountSchedulerAction()));

        markerFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "_marker");
        duplicateMarkers = new CopyOnWriteArrayList<>();
    }

//    @Test
//    @Ignore("very slow")
//    public void testSchedulerIntegration()
//            throws Exception {
//        Object connectionOptions = new ConnectionOptions() {
//            @Override
//            public String getUrl() throws ReadValueException {
//                return "jdbc:postgresql://localhost:5433/postgres";
//            }
//
//            @Override
//            public String getUsername() throws ReadValueException {
//                return "test_user";
//            }
//
//            @Override
//            public String getPassword() throws ReadValueException {
//                return "password";
//            }
//
//            @Override
//            public Integer getMaxConnections() throws ReadValueException {
//                return 16;
//            }
//
//            @Override
//            public void setUrl(String url) throws ChangeValueException {}
//
//            @Override
//            public void setUsername(String username) throws ChangeValueException {}
//
//            @Override
//            public void setPassword(String password) throws ChangeValueException {}
//
//            @Override
//            public void setMaxConnections(Integer maxConnections) throws ChangeValueException {}
//        };
//
//        Object connectionPool = IOC.resolve(Keys.getKeyByName("PostgresConnectionPool"), connectionOptions);
//
//        service = IOC.resolve(
//                Keys.getKeyByName("new scheduler service"),
//                connectionPool,
//                "schedulerCollection"
//        );
//
//        storage = service.getEntryStorage();
//
//        service.start();
//
//        for (int i = 0; i < SCHEDULE_INITIAL; i++) {
//            scheduleOne();
//        }
//
//        synchronized (conpletionLock) {
//            while (!isCompleted) {
//                conpletionLock.wait(100);
//            }
//        }
//
//        long endTime = System.currentTimeMillis();
//
//        for (ISchedulerEntry entry : storage.listLocalEntries()) {
//            System.out.println(MessageFormat.format("(1) Zombie entry {0} scheduled in {1} ms after end.",
//                    entry.getState().getValue(markerFieldName),
//                    entry.getLastTime() - endTime));
//        }
//
//        for (int i = 0; i < 30 && storage.listLocalEntries().size() != 0; i++) {
//            Thread.sleep(1000);
//        }
//
//        for (ISchedulerEntry entry : storage.listLocalEntries()) {
//            System.out.println(MessageFormat.format("(2) Zombie entry {0} scheduled in {1} ms after end.",
//                    entry.getState().getValue(markerFieldName),
//                    entry.getLastTime() - endTime));
//        }
//
//        assertEquals(0, sMap.size());
//
//        List<ISchedulerEntry> entries = storage.listLocalEntries();
//        assertEquals(0, entries.size());
//
//        System.err.println(duplicateMarkers);
//        assertEquals(0, duplicateMarkers.size());
//    }
}
