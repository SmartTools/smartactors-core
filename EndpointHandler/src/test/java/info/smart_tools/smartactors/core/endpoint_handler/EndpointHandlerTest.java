package info.smart_tools.smartactors.core.endpoint_handler;

import info.smart_tools.smartactors.core.blocking_queue.BlockingQueue;
import info.smart_tools.smartactors.core.endpoint_handler.exceptions.EndpointException;
import info.smart_tools.smartactors.core.ienvironment_extractor.IEnvironmentExtractor;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EndpointHandlerTest {
    IQueue<ITask> taskIQueue;
    IEnvironmentExtractor environmentExtractor;
    EndpointHandlerTask endpointHandlerTask;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        environmentExtractor = mock(IEnvironmentExtractor.class);
        taskIQueue = new BlockingQueue<ITask>(new ArrayBlockingQueue<ITask>(10));
        endpointHandlerTask = mock(EndpointHandlerTask.class);
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );

        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);
        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy()
        );
        IOC.register(Keys.getOrAdd("task_queue"),
                new SingletonStrategy(taskIQueue));
        IOC.register(Keys.getOrAdd(IEnvironmentExtractor.class.getCanonicalName()),
                new SingletonStrategy(environmentExtractor));
        IOC.register(Keys.getOrAdd(EndpointHandlerTask.class.getCanonicalName()), new SingletonStrategy(endpointHandlerTask));
    }

    @Test
    public void testAdditionTaskToQueue() throws EndpointException, ScopeProviderException, ExecutionException, InterruptedException {
        IReceiverChain receiverChain = mock(IReceiverChain.class);
        IEnvironmentHandler environmentHandler = mock(IEnvironmentHandler.class);
        IScope scope = ScopeProvider.getCurrentScope();
        FullHttpRequest request = mock(FullHttpRequest.class);
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);
        EndpointHandler handler = new EndpointHandler<ChannelHandlerContext, FullHttpRequest>(receiverChain, environmentHandler, scope) {
            @Override
            public IObject getEnvironment(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
                return null;
            }
        };
        handler.handle(context, request);
        assertTrue(taskIQueue.tryTake() != null);
    }
}
