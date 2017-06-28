package info.smart_tools.smartactors.scheduler.actor.impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;

/**
 * Default scheduler action - send message stored in "message" field of arguments to {@link
 * info.smart_tools.smartactors.message_bus.message_bus.MessageBus message bus}.
 */
public class DefaultSchedulerAction implements ISchedulerAction {
    private final IFieldName messageFieldName;
    private final IFieldName setEntryIdFieldName;
    private final IFieldName preShutdownExecFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if cannot resolve any dependency
     */
    public DefaultSchedulerAction()
            throws ResolutionException {
        messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        setEntryIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "setEntryId");
        preShutdownExecFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "preShutdownExec");
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args)
            throws SchedulerActionInitializationException {
        try {
            Object message = args.getValue(messageFieldName);

            if (message == null || !(message instanceof IObject)) {
                throw new SchedulerActionInitializationException("\"message\" field of arguments should contain a message object", null);
            }

            String entryIdFieldFN = (String) args.getValue(setEntryIdFieldName);

            if (entryIdFieldFN != null && !entryIdFieldFN.isEmpty()) {
                ((IObject) message).setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), entryIdFieldFN), entry.getId());
            }

            entry.getState().setValue(messageFieldName, message);

            Object preShutdownExecConfig = args.getValue(preShutdownExecFieldName);
            entry.getState().setValue(preShutdownExecFieldName,
                    (preShutdownExecConfig == null || preShutdownExecConfig == Boolean.FALSE) ? Boolean.FALSE : Boolean.TRUE);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException | ResolutionException e) {
            throw new SchedulerActionInitializationException("Error occurred copying message from arguments to entry state.", e);
        }
    }

    @Override
    public void execute(final ISchedulerEntry entry)
            throws SchedulerActionExecutionException {
        try {
            IObject message = (IObject) entry.getState().getValue(messageFieldName);

            String serialized = message.serialize();

            message = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), serialized);

            MessageBus.send(message);
        } catch (ReadValueException | InvalidArgumentException | SendingMessageException | SerializeException | ResolutionException e) {
            throw new SchedulerActionExecutionException("Error occurred sending the message.", e);
        }
    }
}
