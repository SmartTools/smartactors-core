package info.smart_tools.smartactors.checkpoint_plugins.checkpoint_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.CheckpointActor;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.CheckpointSchedulerAction;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;

/**
 * Plugin that registers stub checkpoint actor dependency.
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
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering actor creation strategy
     * @throws InvalidArgumentException if unexpected error occurs
     */
    @Item("checkpoint_actor")
    public void registerCheckpointActorStub()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("checkpoint actor"), new ApplyFunctionToArgumentsStrategy(a -> {
            try {
                return new CheckpointActor((IObject) a[0]);
            } catch (Exception e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Register action that should be executed when checkpoint entry fires.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering actor creation strategy
     * @throws InvalidArgumentException i unexpected error occurs
     */
    @Item("checkpoint_scheduler_action")
    @Before({"checkpoint_actor"})
    public void registerSchedulerAction()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("checkpoint scheduler action"),
                new SingletonStrategy(new CheckpointSchedulerAction()));
    }

    /**
     * Register default action to be executed for messages that has not reached next checkpoint until scheduling strategy completion.
     *
     * <p>
     *     The default action registered just prints contents of the message to {@code stderr}.
     * </p>
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering actor creation strategy
     * @throws InvalidArgumentException if unexpected error occurs
     */
    @Item("checkpoint_failure_action_default")
    @Before({"checkpoint_actor"})
    public void registerDefaultFailureAction()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("checkpoint failure action"),
                new SingletonStrategy((IAction<IObject>) msg -> {
                    try {
                        String msgString = msg.serialize();
                        System.err.printf("Lost message: %s\n", msgString);
                    } catch (SerializeException e) {
                        throw new ActionExecutionException(e);
                    }
                }));
    }

    /**
     * Register default scheduler service activation action for checkpoints - do nothing.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering actor creation strategy
     * @throws InvalidArgumentException if unexpected error occurs
     */
    @Item("default_scheduler_activation_action_for_checkpoint_actor")
    @Before({
        "checkpoint_actor"
    })
    public void registerDefaultActivationAction()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("scheduler service activation action for checkpoint actor"),
                new SingletonStrategy((IAction<ISchedulerService>) service -> { }));
    }
}
