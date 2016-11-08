package info.smart_tools.smartactors.checkpoint_plugins.checkpoint_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.CheckpointActor;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Plugin that registers stub checkpoint actor dependency.
 *
 * TODO: Implement real actor and plugin.
 */
public class CheckpointActorPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public CheckpointActorPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register the checkpoint actor.
     *
     * @throws ResolutionException
     * @throws RegistrationException
     * @throws InvalidArgumentException
     */
    @Item("checkpoint_actor")
    public void registerCheckpointActorStub()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getOrAdd("checkpoint actor"), new ApplyFunctionToArgumentsStrategy(a -> {
            try {
                IObject args = (IObject) a[0];

                IFieldName connectionOptionsFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionOptionsDependency");
                IFieldName connectionPoolFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionPoolDependency");
                IFieldName collectionNameFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "collectionName");

                if (!(args.getValue(collectionNameFN) instanceof String) ||
                        !(args.getValue(connectionPoolFN) instanceof String) ||
                        !(args.getValue(connectionOptionsFN) instanceof String)) {
                    throw new InvalidArgumentException("Invalid checkpoint actor configuration.");
                }

                return new CheckpointActor();
            } catch (Exception e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}
