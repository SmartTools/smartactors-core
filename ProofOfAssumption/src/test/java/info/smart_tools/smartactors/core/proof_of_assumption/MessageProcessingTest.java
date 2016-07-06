package info.smart_tools.smartactors.core.proof_of_assumption;

import info.smart_tools.smartactors.core.blocking_queue.BlockingQueue;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.imessage.IMessage;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask_dispatcher.ITaskDispatcher;
import info.smart_tools.smartactors.core.ithread_pool.IThreadPool;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.core.message_processing_sequence.MessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processor.MessageProcessor;
import info.smart_tools.smartactors.core.receiver_chain.ImmutableReceiverChain;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import info.smart_tools.smartactors.core.task_dispatcher.TaskDispatcher;
import info.smart_tools.smartactors.core.thread_pool.ThreadPool;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.Mockito.mock;

/**
 * Performance test for {@link MessageProcessor} and related components.
 */
public class MessageProcessingTest {
    private final int PAYLOAD_MESSAGES = 1000000;
    private final int MEASURE_MESSAGES = 2;
    private final int WINDOW_SIZE = 2000;
    private final int POOL_SIZE = 8;
    private final int QUICK_POOL_SIZE = 4;
    private final int PUT_TASKS = 2;
    private final int PUT_INTERVAL = 10;

    private IQueue<ITask> taskQueue;
    private ITaskDispatcher dispatcher;

    private IReceiverChain standardChain;

    @Before
    public void setUp()
            throws Exception {
        taskQueue = new BlockingQueue<>(new ArrayBlockingQueue<>(PAYLOAD_MESSAGES + MEASURE_MESSAGES + WINDOW_SIZE));
        IThreadPool threadPool = new ThreadPool(POOL_SIZE+PUT_TASKS);
        dispatcher = new TaskDispatcher(taskQueue, threadPool, 1000L, QUICK_POOL_SIZE+PUT_TASKS);

        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );

        ScopeProvider.setCurrentScope(ScopeProvider.getScope(ScopeProvider.createScope(null)));
        IOC.register(
                IOC.getKeyForKeyStorage(),
                new CreateNewInstanceStrategy(objects -> {
                    try {
                        return new Key(objects[0].toString());
                    } catch (InvalidArgumentException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
        IOC.register(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class), new CreateNewInstanceStrategy(objects -> null));
    }

    @Test
    public void test_messageProcessingPerformance()
            throws Exception {
        final Thread mainThread = Thread.currentThread();
        final AtomicLong startNanoTime = new AtomicLong();
        final AtomicLong deltaTime = new AtomicLong();
        final AtomicBoolean done = new AtomicBoolean(false);
        ConcurrentMap<Long, Long> threadUseCount = new ConcurrentHashMap<>();

        IMessage messageMock = mock(IMessage.class);
        IObject contextMock = mock(IObject.class);

        IMessageReceiver countReceiver = (message, args, onEnd) -> {
            Long tid = Thread.currentThread().getId();
            long n = threadUseCount.computeIfAbsent(tid, l -> 0L) + 1;
            threadUseCount.put(tid, n);
            try {
                onEnd.execute(null);
            } catch (ActionExecuteException | InvalidArgumentException e) {
                throw new MessageReceiveException(e);
            }
        };

        IMessageReceiver dummyReceiver = (message, args, onEnd) -> {
            try {
                onEnd.execute(null);
            } catch (ActionExecuteException | InvalidArgumentException e) {
                throw new MessageReceiveException(e);
            }
        };

        ITask putTask = () -> {
            while (!mainThread.isInterrupted() && !Thread.interrupted()) {
                try {
                    new MessageProcessor(taskQueue, new MessageProcessingSequence(4, standardChain)).process(messageMock, contextMock);
                    Thread.sleep(PUT_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (InvalidArgumentException | ResolutionException e) {}
            }
        };

        for (int i = 0; i < PUT_TASKS; i++) {
            taskQueue.put(putTask);
        }

        IMessageReceiver countStartReceiver = (message, args, onEnd) -> {
            startNanoTime.set(System.nanoTime());
            try {
                onEnd.execute(null);
            } catch (ActionExecuteException | InvalidArgumentException e) {
                throw new MessageReceiveException(e);
            }
        };

        IMessageReceiver countEndReceiver = (message, args, onEnd) -> {
            deltaTime.set(System.nanoTime() - startNanoTime.get());
            System.out.println(MessageFormat.format("Messages handled in {0}ns ({1}s)", deltaTime, 0.000000001*(double)deltaTime.get()));
            done.set(true);
            synchronized (done) {
                done.notifyAll();
            }
            try {
                onEnd.execute(null);
            } catch (ActionExecuteException | InvalidArgumentException e) {
                throw new MessageReceiveException(e);
            }
        };

        standardChain = new ImmutableReceiverChain("standard",
                new IMessageReceiver[] {
//                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
//                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, countReceiver},
                new IObject[8],
                null);

        IReceiverChain countStartChain = new ImmutableReceiverChain("countStart",
                new IMessageReceiver[] {dummyReceiver, countStartReceiver, countReceiver},
                new IObject[3],
                null);

        IReceiverChain countEndChain = new ImmutableReceiverChain("countEnd",
                new IMessageReceiver[] {
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        countEndReceiver},
                new IObject[21],
                null);

        new MessageProcessor(taskQueue, new MessageProcessingSequence(4, countStartChain)).process(messageMock, contextMock);

        for (int i = 0; i < PAYLOAD_MESSAGES; i++) {
            new MessageProcessor(taskQueue, new MessageProcessingSequence(4, standardChain)).process(messageMock, contextMock);
        }

        new MessageProcessor(taskQueue, new MessageProcessingSequence(4, countEndChain)).process(messageMock, contextMock);

        dispatcher.start();

        synchronized (done) {
            while (!done.get()) {
                done.wait();
            }
        }

        long total = 0;

        for (Long tid : threadUseCount.keySet()) {
            long nTasks = threadUseCount.get(tid);
            System.out.println(MessageFormat.format("T#{0}\t{1} messages", tid, nTasks));
            total += nTasks;
        }

        System.out.println(MessageFormat.format("Total:\t{0} messages", total));
        System.out.println();
        System.out.println(MessageFormat.format("Bandwidth:\t{0} messages/second",
                ((double)total)/(0.000000001*(double)deltaTime.get())));
    }
}
