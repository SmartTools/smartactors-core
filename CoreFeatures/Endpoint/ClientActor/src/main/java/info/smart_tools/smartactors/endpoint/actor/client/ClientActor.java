package info.smart_tools.smartactors.endpoint.actor.client;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.actor.client.exception.RequestSenderActorException;
import info.smart_tools.smartactors.endpoint.actor.client.wrapper.ClientActorMessage;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

import java.util.UUID;

/**
 * Actor for sending requests to other servers
 */
public class ClientActor {
    /**
     * Constructor for actor
     */
    public ClientActor() {
    }

    /**
     * Handler of the actor for send response
     *
     * @param message Wrapper of the actor
     * @throws RequestSenderActorException if there are some problems on sending response
     */
    public void sendRequest(final ClientActorMessage message)
            throws RequestSenderActorException {
        try {
            IQueue<ITask> queue = IOC.resolve(Keys.getOrAdd("task_queue"));
            IFieldName uidFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uid");
            ITask task =
                    () -> {
                        try {
                            IObject request = message.getRequest();
                            request.setValue(uidFieldName, UUID.randomUUID().toString());
                            MessageBus.send(message.getRequest(), message.getSendingChain());
                        } catch (SendingMessageException | InvalidArgumentException | ChangeValueException e) {
                            throw new RuntimeException(e);
                        }
                    };
            queue.put(task);
        } catch (ResolutionException | InterruptedException e) {
            throw new RequestSenderActorException(e);
        }
    }
}
