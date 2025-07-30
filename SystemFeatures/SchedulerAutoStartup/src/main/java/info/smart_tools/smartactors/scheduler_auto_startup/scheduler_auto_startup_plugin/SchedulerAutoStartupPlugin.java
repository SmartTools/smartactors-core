package info.smart_tools.smartactors.scheduler_auto_startup.scheduler_auto_startup_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

public class SchedulerAutoStartupPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public SchedulerAutoStartupPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    /**
     * Register a action that will start scheduler service of scheduler actor after feature group load completion.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if some unexpected error suddenly occurs
     */
    @Item("scheduler_actor_delayed_startup_action")
    public void doSomeThing()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("scheduler service activation action for scheduler actor"),
                new SingletonStrategy((IAction<ISchedulerService>) service -> {
                    try {
                        IQueue<ITask> featureLoadCompletionQueue = IOC.resolve(Keys.getKeyByName("feature group load completion task queue"));
                        featureLoadCompletionQueue.put(() -> {
                            try {
                                service.start();
                            } catch (ServiceStartException | IllegalServiceStateException e) {
                                throw new TaskExecutionException(e);
                            }
                        });
                    } catch (ResolutionException e) {
                        throw new ActionExecutionException(e);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
        }));
    }
}
