package info.smart_tools.smartactors.core.proof_of_assumption;

import info.smart_tools.smartactors.core.blocking_queue.BlockingQueue;
import info.smart_tools.smartactors.core.chain_call_receiver.ChainCallReceiver;
import info.smart_tools.smartactors.core.chain_call_receiver.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.core.chain_storage.ChainStorage;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.ds_object.FieldName;
import info.smart_tools.smartactors.core.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.imessage.IMessage;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask_dispatcher.ITaskDispatcher;
import info.smart_tools.smartactors.core.ithread_pool.IThreadPool;
import info.smart_tools.smartactors.core.map_router.MapRouter;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.core.message_processing_sequence.MessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processor.MessageProcessor;
import info.smart_tools.smartactors.core.receiver_chain.ImmutableReceiverChain;
import info.smart_tools.smartactors.core.receiver_chain.ImmutableReceiverChainResolutionStrategy;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import info.smart_tools.smartactors.core.task_dispatcher.TaskDispatcher;
import info.smart_tools.smartactors.core.thread_pool.ThreadPool;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()),
                new CreateNewInstanceStrategy(objects -> new DSObject()));

        IOC.register(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                new ResolveByNameIocStrategy(objects -> {
                    try {
                        return new FieldName(String.valueOf(objects[0]));
                    } catch (InvalidArgumentException e) {
                        throw new RuntimeException(e);
                    }
                }));
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

        IMessageReceiver countReceiver = (mp) -> {
            Long tid = Thread.currentThread().getId();
            long n = threadUseCount.computeIfAbsent(tid, l -> 0L) + 1;
            threadUseCount.put(tid, n);
        };

        IMessageReceiver dummyReceiver = (mp) -> {};

        ITask putTask = () -> {
            while (!mainThread.isInterrupted() && !Thread.interrupted()) {
                try {
                    new MessageProcessor(taskQueue, new MessageProcessingSequence(4, standardChain)).process(messageMock, contextMock);
                    Thread.sleep(PUT_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (InvalidArgumentException | ResolutionException | ChangeValueException e) {}
            }
        };

        for (int i = 0; i < PUT_TASKS; i++) {
            taskQueue.put(putTask);
        }

        IMessageReceiver countStartReceiver = (mp) -> {
            startNanoTime.set(System.nanoTime());
        };

        IMessageReceiver countEndReceiver = (mp) -> {
            deltaTime.set(System.nanoTime() - startNanoTime.get());
            System.out.println(MessageFormat.format("Messages handled in {0}ns ({1}s)", deltaTime, 0.000000001*(double)deltaTime.get()));
            done.set(true);
            synchronized (done) {
                done.notifyAll();
            }
        };

        standardChain = new ImmutableReceiverChain("standard",
                new IMessageReceiver[] {
//                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
//                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, countReceiver},
                new IObject[8],
                new HashMap<>());

        IReceiverChain countStartChain = new ImmutableReceiverChain("countStart",
                new IMessageReceiver[] {dummyReceiver, countStartReceiver, countReceiver},
                new IObject[3],
                new HashMap<>());

        IReceiverChain countEndChain = new ImmutableReceiverChain("countEnd",
                new IMessageReceiver[] {
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        dummyReceiver, dummyReceiver, dummyReceiver, dummyReceiver,
                        countEndReceiver},
                new IObject[21],
                new HashMap<>());

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

    @Test
    public void test_messageProcessingWithChainChoicePerformance()
            throws Exception {
        final Thread mainThread = Thread.currentThread();
        final AtomicLong startNanoTime = new AtomicLong();
        final AtomicLong deltaTime = new AtomicLong();
        final AtomicBoolean done = new AtomicBoolean(false);
        ConcurrentMap<Long, Long> threadUseCount = new ConcurrentHashMap<>();

        IMessage messageMock = mock(IMessage.class);
        IObject contextMock = mock(IObject.class);

        IOC.register(IOC.resolve(IOC.getKeyForKeyStorage(), "chain_id"),
                new ResolveByNameIocStrategy(objects -> String.valueOf(objects[0])));

        final IFieldName targetNameFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "target");

        IOC.register(IOC.resolve(IOC.getKeyForKeyStorage(), "receiver_id_from_iobject"),
                new IResolveDependencyStrategy() {
                    @Override
                    public <T> T resolve(Object... args) throws ResolveDependencyStrategyException {
                        try {
                            return (T)String.valueOf(((IObject)args[0]).getValue(targetNameFieldName));
                        } catch (ReadValueException | InvalidArgumentException e) {
                            throw new ResolveDependencyStrategyException(e);
                        }
                    }
                });

        IOC.register(IOC.resolve(IOC.getKeyForKeyStorage(), IReceiverChain.class.toString()),
                new ImmutableReceiverChainResolutionStrategy());

        IOC.register(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class),
                new CreateNewInstanceStrategy(objects -> new DSObject()));

        IObject prepareTarget = new DSObject("{\"target\":\"prepare\"}");
        IObject callTarget = new DSObject("{\"target\":\"call\"}");
        IObject call2Target = new DSObject("{\"target\":\"call2\"}");
        IObject startMeasureTarget = new DSObject("{\"target\":\"startMeasure\"}");
        IObject endMeasureTarget = new DSObject("{\"target\":\"endMeasure\"}");
        IObject dummyTarget = new DSObject("{\"target\":\"dummy\"}");
        IObject countTarget = new DSObject("{\"target\":\"count\"}");

        IObject mainChainDesc = new DSObject();
        IObject measureStartChainDesc = new DSObject();
        IObject measureEndChainDesc = new DSObject();
        IObject payloadChainDesc = new DSObject();
        IObject innerPayloadChainDesc = new DSObject();

        IFieldName exceptionalFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "exceptional");
        IFieldName pathFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "steps");

        // Main chain
        mainChainDesc.setValue(exceptionalFieldName, new ArrayList<>());
        mainChainDesc.setValue(pathFieldName, new ArrayList<>());
        ((List)mainChainDesc.getValue(pathFieldName)).add(prepareTarget);
        ((List)mainChainDesc.getValue(pathFieldName)).add(callTarget);
        ((List)mainChainDesc.getValue(pathFieldName)).add(countTarget);

        // Measure start chain
        measureStartChainDesc.setValue(exceptionalFieldName, new ArrayList<>());
        measureStartChainDesc.setValue(pathFieldName, new ArrayList<>());
        ((List)measureStartChainDesc.getValue(pathFieldName)).add(dummyTarget);
        ((List)measureStartChainDesc.getValue(pathFieldName)).add(startMeasureTarget);

        // Measure end chain
        measureEndChainDesc.setValue(exceptionalFieldName, new ArrayList<>());
        measureEndChainDesc.setValue(pathFieldName, new ArrayList<>());
        for (int i = 0; i < 20; i++) {
            ((List)measureEndChainDesc.getValue(pathFieldName)).add(dummyTarget);
        }
        ((List)measureEndChainDesc.getValue(pathFieldName)).add(endMeasureTarget);

        // Payload chain
        payloadChainDesc.setValue(exceptionalFieldName, new ArrayList<>());
        payloadChainDesc.setValue(pathFieldName, new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            ((List)payloadChainDesc.getValue(pathFieldName)).add(call2Target);
        }

        // Inner payload chain
        innerPayloadChainDesc.setValue(exceptionalFieldName, new ArrayList<>());
        innerPayloadChainDesc.setValue(pathFieldName, new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            ((List)innerPayloadChainDesc.getValue(pathFieldName)).add(dummyTarget);
        }

        // Router & chain storage
        IRouter router = new MapRouter(new ConcurrentHashMap<>());
        IChainStorage storage = new ChainStorage(new ConcurrentHashMap<>(), router);

        // Receivers
        final AtomicLong messageCounter = new AtomicLong(0);

        router.register("prepare", (mp) -> {
            long i = messageCounter.getAndIncrement();
            String target = null;

            if (i == 0) {
                target = "measureStart";
            } else if (i == PAYLOAD_MESSAGES+1) {
                target = "measureEnd";
            } else {
                target = "payload";
            }

            try {
                mp.getMessage().setValue(targetNameFieldName, target);
            } catch (ChangeValueException | InvalidArgumentException e) {
                throw new MessageReceiveException(e);
            }
        });

        router.register("call", new ChainCallReceiver(storage, messageProcessor -> {
            try {
                return messageProcessor.getMessage().getValue(targetNameFieldName);
            } catch (ReadValueException | InvalidArgumentException e) {
                throw new ChainChoiceException("Couldn't choose chain.", e);
            }
        }));

        router.register("call2", new ChainCallReceiver(storage, messageProcessor ->
            "innerPayload"));

        router.register("count", (mp) -> {
            Long tid = Thread.currentThread().getId();
            long n = threadUseCount.computeIfAbsent(tid, l -> 0L) + 1;
            threadUseCount.put(tid, n);
        });

        router.register("dummy", (mp) -> { });

        router.register("startMeasure", (mp) -> {
            startNanoTime.set(System.nanoTime());
        });

        router.register("endMeasure", (mp) -> {
            deltaTime.set(System.nanoTime() - startNanoTime.get());
            System.out.println(MessageFormat.format("Messages handled in {0}ns ({1}s)", deltaTime, 0.000000001*(double)deltaTime.get()));
            done.set(true);
            synchronized (done) {
                done.notifyAll();
            }
        });

        // Register chins
        storage.register("main", mainChainDesc);
        storage.register("measureStart", measureStartChainDesc);
        storage.register("measureEnd", measureEndChainDesc);
        storage.register("payload", payloadChainDesc);
        storage.register("innerPayload", innerPayloadChainDesc);

        IReceiverChain mainChain = storage.resolve("main");

        //

        for (int i = 0; i < PAYLOAD_MESSAGES + MEASURE_MESSAGES; i++) {
            new MessageProcessor(taskQueue, new MessageProcessingSequence(5, mainChain))
                    .process(new DSObject(), new DSObject());
        }

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
