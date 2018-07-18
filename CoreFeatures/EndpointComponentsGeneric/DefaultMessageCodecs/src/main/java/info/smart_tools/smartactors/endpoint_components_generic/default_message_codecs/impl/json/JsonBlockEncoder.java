package info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.impl.json;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base.AbstractBlockEncoder;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base.exceptions.BlockCodecException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;

import java.nio.ByteBuffer;

/**
 * A {@link AbstractBlockEncoder block encoder} that encodes a message to JSON.
 *
 * @param <T>
 */
public class JsonBlockEncoder<T extends IDefaultMessageContext<IObject, ? extends IOutboundMessageByteArray<?>, ?>>
        extends AbstractBlockEncoder<T> {
    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public JsonBlockEncoder() throws ResolutionException {
    }

    @Override
    protected ByteBuffer encode(final IObject message) throws BlockCodecException {
        try {
            String serialized = message.serialize();
            return ByteBuffer.wrap(serialized.getBytes());
        } catch (SerializeException e) {
            throw new BlockCodecException(e);
        }
    }
}
