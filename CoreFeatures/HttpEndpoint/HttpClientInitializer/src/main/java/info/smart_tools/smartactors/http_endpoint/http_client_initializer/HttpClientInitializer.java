package info.smart_tools.smartactors.http_endpoint.http_client_initializer;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.http_endpoint.http_client.HttpClient;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

import java.util.HashMap;
import java.util.Map;

/**
 * Strategy initializer for {@link HttpClient}
 */
public class HttpClientInitializer {

    private HttpClientInitializer() {
    }

    public static void init() throws InvalidArgumentException, ResolutionException, RegistrationException {
        IFieldName uuidFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "uuid");
        IFieldName timeFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "timeout");

        Map<Object, ITimerTask> timerTasks = new HashMap<>();
        IOC.register(Keys.getKeyByName("createTimerOnRequest"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            ITimer timer =
                                    null;
                            IObject message = (IObject) args[0];
                            Object chainName = args[1];

                            try {
                                ITask task = () -> {
                                    try {
                                        MessageBus.send(message, chainName);
                                        ITimerTask timerTask = timerTasks.get(message.getValue(uuidFieldName));
                                        timerTask.cancel();
                                        timerTasks.remove(message.getValue(uuidFieldName));
                                    } catch (ReadValueException | InvalidArgumentException | SendingMessageException e) {
                                        throw new RuntimeException(e);
                                    }
                                };
                                timer = IOC.resolve(Keys.getKeyByName("timer"));
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
                            } catch (ResolutionException | TaskScheduleException | ReadValueException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );

        IOC.register(Keys.getKeyByName("cancelTimerOnRequest"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            Object uuid = args[0];
                            ITimerTask timerTask = timerTasks.get(uuid);
                            timerTask.cancel();
                            timerTasks.remove(uuid);
                            return null;
                        }
                )
        );
    }
}
