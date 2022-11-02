package info.smart_tools.smartactors.checkpoint_plugins.checkpoint_failure_actions_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.checkpoint.failure_action.SendEnvelopeFailureAction;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

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
        IStrategy strategy = new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                IObject config = (IObject) args[0];

                IAction<IObject> currentAction = IOC.resolve(Keys.getKeyByName("checkpoint failure action"));
                IFieldName chainFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "targetChain");
                IFieldName messageFieldFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "messageField");

                // Object chainName = IOC.resolve(Keys.getKeyByName("chain_id_from_map_name_and_message"), config.getValue(chainFN));
                Object chainName = config.getValue(chainFN);
                IFieldName messageFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), config.getValue(messageFieldFN));

                return new SendEnvelopeFailureAction(chainName, messageFN, currentAction);
            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                throw new FunctionExecutionException(e);
            }
        });

        IOC.register(Keys.getKeyByName("default configurable checkpoint failure action"), strategy);
        IOC.register(Keys.getKeyByName("send to chain checkpoint failure action"), strategy);
    }
}
