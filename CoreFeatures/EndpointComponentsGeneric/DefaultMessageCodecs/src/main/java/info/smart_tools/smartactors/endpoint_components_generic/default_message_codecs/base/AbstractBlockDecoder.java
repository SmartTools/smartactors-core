package info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_components_generic.common_exceptions.InboundMessageDecoderException;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base.exceptions.BlockCodecException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
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

import java.nio.ByteBuffer;

/**
 * Base class for message handlers that decode one inbound external message to one internal message.
 *
 * @param <T>
 */
public abstract class AbstractBlockDecoder<T extends IDefaultMessageContext<? extends IInboundMessageByteArray<?>, IObject, ?>>
        implements IBypassMessageHandler<T> {
    private final IFieldName messageFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public AbstractBlockDecoder()
            throws ResolutionException {
        messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<T> next,
            final T context)
                throws MessageHandlerException {
        try {
            context.getDstMessage().setValue(messageFieldName, decode(context.getSrcMessage().getAsByteBuffer()));
        } catch (ChangeValueException | InvalidArgumentException | BlockCodecException e) {
            throw new InboundMessageDecoderException(e);
        }

        next.handle(context);
    }

    /**
     * Decode the external message.
     *
     * @param buf external message content
     * @return internal message object
     * @throws BlockCodecException if any error occurs decoding the message
     */
    protected abstract IObject decode(ByteBuffer buf) throws BlockCodecException;
}
