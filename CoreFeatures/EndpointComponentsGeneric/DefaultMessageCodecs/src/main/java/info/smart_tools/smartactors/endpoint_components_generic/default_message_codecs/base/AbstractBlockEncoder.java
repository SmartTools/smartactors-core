package info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_components_generic.common_exceptions.OutboundMessageEncoderException;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base.exceptions.BlockCodecException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.nio.ByteBuffer;

/**
 * Base class for a message handlers that encode one outbound internal message to one outbound external message.
 *
 * @param <T>
 */
public abstract class AbstractBlockEncoder<T extends IDefaultMessageContext<IObject, ? extends IOutboundMessageByteArray<?>, ?>>
        implements IBypassMessageHandler<T> {
    private final IFieldName messageFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public AbstractBlockEncoder()
            throws ResolutionException {
        messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
    }

    @Override
    public void handle(final IMessageHandlerCallback<T> next, final T context)
            throws MessageHandlerException {
        try {
            IObject message = (IObject) context.getSrcMessage().getValue(messageFieldName);
            context.getDstMessage().setAsByteBuffer(encode(message));
        } catch (InvalidArgumentException | ReadValueException | BlockCodecException e) {
            throw new OutboundMessageEncoderException(e);
        }

        next.handle(context);
    }

    /**
     * Encode the internal message.
     *
     * @param message the message
     * @return message encoded to sequence of bytes
     * @throws BlockCodecException if any error occurs
     */
    protected abstract ByteBuffer encode(IObject message) throws BlockCodecException;
}
