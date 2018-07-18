package info.smart_tools.smartactors.endpoint_components_generic.create_empty_message_message_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Message handler that creates an empty internal inbound message.
 *
 * <p>
 *  This handler should e used when a external inbound message has no body that may be directly deserialized to a
 *  {@link IObject}. Example of such message is a HTTP GET request.
 * </p>
 *
 * @param <T>
 */
public class CreateEmptyMessageMessageHandler<T extends IDefaultMessageContext<?, IObject, ?>>
        implements IBypassMessageHandler<T> {
    private final IFieldName messageFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public CreateEmptyMessageMessageHandler()
            throws ResolutionException {
        messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
    }

    @Override
    public void handle(final IMessageHandlerCallback<T> next, final T context) throws MessageHandlerException {
        try {
            IObject emptyMessage = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));
            context.getDstMessage().setValue(messageFieldName, emptyMessage);
        } catch (ResolutionException | InvalidArgumentException | ChangeValueException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
