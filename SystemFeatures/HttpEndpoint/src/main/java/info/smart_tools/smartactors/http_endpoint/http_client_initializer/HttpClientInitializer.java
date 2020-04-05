package info.smart_tools.smartactors.http_endpoint.http_client_initializer;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.http_endpoint.http_client.HttpClient;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.IResponseStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
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
    public static void init() throws InvalidArgumentException, ResolutionException, RegistrationException {
        IFieldName uuidFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "uuid");
        IFieldName timeFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "timeout");

        IFieldName queueFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "queue");
        IFieldName stackDepthFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "stackDepth");
        IFieldName responseStrategyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responseStrategy");
        IKey messageProcessingSequenceKey = Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence");
        IKey messageProcessorKey = Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor");
        IKey responseHandlerConfigurationKey = Keys.getKeyByName("responseHandlerConfiguration");
        IKey iObjectKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
        IKey nullResponseStrategyKey = Keys.getKeyByName("null response strategy");

        Map<Object, ITimerTask> timerTasks = new HashMap<>();
        IOC.register(
                Keys.getKeyByName("createTimerOnRequest"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            ITimer timer =
                                    null;
                            IObject message = (IObject) args[0];
                            Object chainName = args[1];

                            try {
                                ITask task = () -> {
                                    try {
                                        IObject clientConfig = IOC.resolve(responseHandlerConfigurationKey);
                                        Integer stackDepth = (Integer)clientConfig.getValue(stackDepthFieldName);
                                        IMessageProcessingSequence processingSequence = IOC.resolve(
                                                messageProcessingSequenceKey,
                                                stackDepth,
                                                chainName,
                                                message,
                                                true
                                        );
                                        IQueue<ITask> taskQueue = (IQueue<ITask>)clientConfig.getValue(queueFieldName);
                                        IMessageProcessor messageProcessor =  IOC.resolve(
                                                messageProcessorKey,
                                                taskQueue,
                                                processingSequence
                                        );
                                        IResponseStrategy nullResponseStrategy = IOC.resolve(nullResponseStrategyKey);
                                        IObject context = IOC.resolve(iObjectKey);
                                        context.setValue(responseStrategyFieldName, nullResponseStrategy);
                                        messageProcessor.process(message, context);

                                        ITimerTask timerTask = timerTasks.get(message.getValue(uuidFieldName));
                                        timerTask.cancel();
                                        timerTasks.remove(message.getValue(uuidFieldName));
                                    } catch (ResolutionException | ReadValueException | InvalidArgumentException
                                            | ChangeValueException | MessageProcessorProcessException e) {
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

        IOC.register(
                Keys.getKeyByName("cancelTimerOnRequest"),
                new ApplyFunctionToArgumentsStrategy(
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
