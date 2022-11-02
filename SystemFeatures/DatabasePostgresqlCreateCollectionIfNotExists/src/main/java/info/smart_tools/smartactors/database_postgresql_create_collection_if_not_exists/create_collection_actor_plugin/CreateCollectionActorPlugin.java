package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.CreateCollectionActor;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

public class CreateCollectionActorPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public CreateCollectionActorPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("CreateCollectionIfNotExistsActorPlugin")
    @After({"CreatePostgresCollectionIfNotExistsPlugin"})
    @Before("")
    public void registerActor() throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("CreateCollectionIfNotExistsActor"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        return new CreateCollectionActor();
                    } catch (Exception e) {
                        throw new FunctionExecutionException("Couldn't create CreateCollectionActor: " + e.getMessage(), e);
                    }
                })
        );
    }
}
