package info.smart_tools.smartactors.http_endpoint.http_client_initializer;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.http_endpoint.http_client.HttpClient;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Strategy initializer for {@link HttpClient}
 */
public class HttpClientInitializer {
    public static void init() throws InvalidArgumentException, ResolutionException, RegistrationException {
        IFieldName uuidFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uuid");
        IFieldName timeFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "timeout");

        Lock timerTaskLock = new ReentrantLock();

        Map<Object, ITimerTask> timerTasks = new HashMap<>();
        IOC.register(Keys.getOrAdd("createTimerOnRequest"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            ITimer timer =
                                    null;
                            IObject message = (IObject) args[0];
                            Object chainName = args[1];

                            try {
                                ITask task = () -> {
                                    try {
                                        try {
                                            IOC.resolve(Keys.getOrAdd("cancelTimerOnRequest"), message.getValue(uuidFieldName));
                                        } catch (ResolutionException e) {
                                            return;
                                        }
                                        MessageBus.send(message, chainName);
                                    } catch (ReadValueException | InvalidArgumentException | SendingMessageException e) {
                                        throw new RuntimeException(e);
                                    }
                                };
                                timer = IOC.resolve(Keys.getOrAdd("timer"));
                                timerTaskLock.lock();
                                try {
                                    ITimerTask timerTask =
                                            timer.schedule(task,
                                                    System.currentTimeMillis() + Long.valueOf(
                                                            String.valueOf(
                                                                    message.getValue(timeFieldName)
                                                            )
                                                    )
                                            );
                                    timerTasks.put(message.getValue(uuidFieldName), timerTask);
                                    return timerTask;
                                } finally {
                                    timerTaskLock.unlock();
                                }
                            } catch (ResolutionException | TaskScheduleException | ReadValueException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );

        IOC.register(Keys.getOrAdd("cancelTimerOnRequest"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            Object uuid = args[0];
                            timerTaskLock.lock();
                            try {
                                ITimerTask timerTask = timerTasks.remove(uuid);
                                if (null == timerTask) {
                                    throw new FunctionExecutionException("Can't find timer");
                                }
                                timerTask.cancel();
                            } finally {
                                timerTaskLock.unlock();
                            }
                            return null;
                        }
                )
        );
    }
}
