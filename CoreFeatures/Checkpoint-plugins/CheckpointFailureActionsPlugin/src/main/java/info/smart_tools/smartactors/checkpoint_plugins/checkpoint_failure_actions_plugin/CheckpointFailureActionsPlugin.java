package info.smart_tools.smartactors.checkpoint_plugins.checkpoint_failure_actions_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.checkpoint.failure_action.SendEnvelopeFailureAction;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 *
 */
public class CheckpointFailureActionsPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public CheckpointFailureActionsPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register strategy creating a {@link SendEnvelopeFailureAction} instance from configuration object.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException i unexpected error occurs
     */
    @Item("checkpoint_failure_action_send_envelope")
    @After({"checkpoint_failure_action_default"})
    @Before({"checkpoint_actor"})
    public void registerSendEnvelopeAction()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IResolveDependencyStrategy strategy = new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                IObject config = (IObject) args[0];

                IAction<IObject> currentAction = IOC.resolve(Keys.getOrAdd("checkpoint failure action"));
                IFieldName chainFN = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "targetChain");
                IFieldName messageFieldFN = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "messageField");

                Object chainId = IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), config.getValue(chainFN));
                IFieldName messageFN = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), config.getValue(messageFieldFN));

                return new SendEnvelopeFailureAction(chainId, messageFN, currentAction);
            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                throw new FunctionExecutionException(e);
            }
        });

        IOC.register(Keys.getOrAdd("default configurable checkpoint failure action"), strategy);
        IOC.register(Keys.getOrAdd("send to chain checkpoint failure action"), strategy);
    }
}
