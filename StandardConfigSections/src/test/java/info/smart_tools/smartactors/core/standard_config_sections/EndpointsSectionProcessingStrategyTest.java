package info.smart_tools.smartactors.core.standard_config_sections;

import info.smart_tools.smartactors.core.HttpEndpoint;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.endpoint_handler.exceptions.EndpointException;
import info.smart_tools.smartactors.core.environment_handler.EnvironmentHandler;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.core.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.core.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
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
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EndpointsSectionProcessingStrategyTest {

    private IQueue<ITask> taskQueue;
    private Object mapId;
    private IChainStorage chainStorage;
    private IReceiverChain receiverChain;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        taskQueue = mock(IQueue.class);
        mapId = mock(Object.class);
        receiverChain = mock(IReceiverChain.class);
        chainStorage = mock(IChainStorage.class);
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
        IKey iFieldNameKey = Keys.getOrAdd(IFieldName.class.getCanonicalName());

        IOC.register(iFieldNameKey,
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                )
        );

        IKey chainIdFromMapNameKey = Keys.getOrAdd("chain_id_from_map_name");
        IOC.register(chainIdFromMapNameKey,
                new SingletonStrategy(mapId));

        IKey taskQueueKey = Keys.getOrAdd("task_queue");
        IOC.register(taskQueueKey,
                new SingletonStrategy(taskQueue));

        IKey chainStorageKey = Keys.getOrAdd(IChainStorage.class.getCanonicalName());
        IOC.register(chainStorageKey,
                new SingletonStrategy(chainStorage));


        IOC.register(
            Keys.getOrAdd(IEnvironmentHandler.class.getCanonicalName()),
            new CreateNewInstanceStrategy(
                (args) -> {
                    IObject configuration = (IObject) args[0];
                    IQueue queue = null;
                    Integer stackDepth = null;
                    try {
                        queue = (IQueue) configuration.getValue(new FieldName("queue"));
                        stackDepth =
                            (Integer) configuration.getValue(new FieldName("stackDepth"));
                        return new EnvironmentHandler(queue, stackDepth);
                    } catch (ReadValueException | InvalidArgumentException e) {
                    }
                    return null;
                }
            )
        );

        IOC.register(Keys.getOrAdd("http_endpoint"),
            new CreateNewInstanceStrategy(
                (args) -> {
                    IObject configuration = (IObject) args[0];
                    try {
                        IEnvironmentHandler environmentHandler = IOC.resolve(
                            Keys.getOrAdd(IEnvironmentHandler.class.getCanonicalName()),
                            configuration);
                        return new HttpEndpoint((Integer) configuration.getValue(new FieldName("port")),
                            (Integer) configuration.getValue(new FieldName("maxContentLength")),
                            ScopeProvider.getCurrentScope(), environmentHandler,
                            (IReceiverChain) configuration.getValue(new FieldName("startChain")));
                    } catch (ReadValueException | InvalidArgumentException | ScopeProviderException |
                            ResolutionException | EndpointException e) {
                    }
                    return null;
                }
            )
        );
    }

    @Test
    public void testLoadingConfig() throws InvalidArgumentException, ResolutionException, ConfigurationProcessingException, ChainNotFoundException {
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
        EndpointsSectionProcessingStrategy strategy = new EndpointsSectionProcessingStrategy();
        strategy.onLoadConfig(config);
    }
}
