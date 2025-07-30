package info.smart_tools.smartactors.endpoint_service_starter.endpoint_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
//import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
//import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
//import info.smart_tools.smartactors.http_endpoint.environment_handler.EnvironmentHandler;
//import info.smart_tools.smartactors.http_endpoint.http_endpoint.HttpEndpoint;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EndpointsSectionProcessingStrategyTest extends IOCInitializer {

    private IQueue<ITask> taskQueue;
    private Object mapId;
    private IChainStorage chainStorage;
    private IReceiverChain receiverChain;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        taskQueue = mock(IQueue.class);
        mapId = mock(Object.class);
        receiverChain = mock(IReceiverChain.class);
        chainStorage = mock(IChainStorage.class);

        IKey chainIdFromMapNameKey = Keys.getKeyByName("chain_id_from_map_name_and_message");
        IOC.register(chainIdFromMapNameKey,
                new SingletonStrategy(mapId));

        IKey taskQueueKey = Keys.getKeyByName("task_queue");
        IOC.register(taskQueueKey,
                new SingletonStrategy(taskQueue));

        IKey chainStorageKey = Keys.getKeyByName(IChainStorage.class.getCanonicalName());
        IOC.register(chainStorageKey,
                new SingletonStrategy(chainStorage));


//        IOC.register(
//            Keys.getKeyByName(IEnvironmentHandler.class.getCanonicalName()),
//            new ApplyFunctionToArgumentsStrategy(
//                (args) -> {
//                    IObject configuration = (IObject) args[0];
//                    IQueue queue = null;
//                    Integer stackDepth = null;
//                    Boolean scopeSwitching = null;
//                    try {
//                        queue = (IQueue) configuration.getValue(new FieldName("queue"));
//                        stackDepth =
//                            (Integer) configuration.getValue(new FieldName("stackDepth"));
//                        scopeSwitching = (Boolean) configuration.getValue(new FieldName("scopeSwitching"));
//                        if (scopeSwitching == null) {
//                            scopeSwitching = true;
//                        }
//                        return new EnvironmentHandler(queue, stackDepth, scopeSwitching);
//                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
//                    }
//                    return null;
//                }
//            )
//        );

//        IOC.register(Keys.getKeyByName("http_endpoint"),
//            new ApplyFunctionToArgumentsStrategy(
//                (args) -> {
//                    IObject configuration = (IObject) args[0];
//                    try {
//                        IEnvironmentHandler environmentHandler = IOC.resolve(
//                            Keys.getKeyByName(IEnvironmentHandler.class.getCanonicalName()),
//                            configuration);
//                        return new HttpEndpoint((Integer) configuration.getValue(new FieldName("port")),
//                            (Integer) configuration.getValue(new FieldName("maxContentLength")),
//                            ScopeProvider.getCurrentScope(),
//                            ModuleManager.getCurrentModule(),
//                            environmentHandler,
//                            configuration.getValue(new FieldName("startChain")),
//                                "default", mock(IUpCounter.class));
//                    } catch (ReadValueException | InvalidArgumentException
//                        | ScopeProviderException | ResolutionException | UpCounterCallbackExecutionException e) {
//                    }
//                    return null;
//                }
//            )
//        );
    }

    @Test
    @Ignore("Need to rewrite because current tests depend on 'http_endpoint' feature but the general feature doesn't depend on.")
    public void testLoadingConfig() throws Exception {
        when(chainStorage.resolve(mapId)).thenReturn(receiverChain);
        DSObject config = new DSObject("\n" +
                "     {\n" +
                "         \"endpoints\": [\n" +
                "             {\n" +
                "                 \"name\": \"enpointName\"," +
                "                 \"type\": \"http\",\n" +
                "                 \"port\": 8080,\n" +
                "                 \"startChain\": \"mainChain\",\n" +
                "                 \"maxContentLength\": 4098,\n" +
                "                 \"stackDepth\": 5\n" +
                "             }\n" +
                "         ]\n" +
                "     }");
        EndpointsSectionProcessingStrategy strategy = new EndpointsSectionProcessingStrategy(new FieldName("endpoints"), "schema");
        strategy.onLoadConfig(config);
        strategy.getSectionName();
        strategy.onRevertConfig(config);

        IObject configMock1 = mock(IObject.class);
        IObject configMock2 = mock(IObject.class);
        IObject configMock3 = mock(IObject.class);
        IObject configMock4 = mock(IObject.class);
        when(configMock1.getValue(any())).thenThrow(ReadValueException.class);
        when(configMock2.getValue(any())).thenThrow(ResolutionException.class);
        when(configMock3.getValue(any())).thenThrow(InvalidArgumentException.class);
        when(configMock4.getValue(any())).thenThrow(ChangeValueException.class);
        try {
            strategy.onLoadConfig(configMock1);
            fail();
        } catch(ConfigurationProcessingException e) {}
        try {
            strategy.onLoadConfig(configMock2);
            fail();
        } catch(ConfigurationProcessingException e) {}
        try {
            strategy.onLoadConfig(configMock3);
            fail();
        } catch(ConfigurationProcessingException e) {}

        strategy.onLoadConfig(configMock4);
    }
}
