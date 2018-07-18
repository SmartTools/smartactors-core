package info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.impl.json;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base.AbstractBlockDecoder;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base.exceptions.BlockCodecException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * A {@link AbstractBlockDecoder block decoder} that decodes each block as a JSON document.
 *
 * @param <T>
 */
public class JsonBlockDecoder<T extends IDefaultMessageContext<? extends IInboundMessageByteArray<?>, IObject, ?>>
        extends AbstractBlockDecoder<T> {
    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public JsonBlockDecoder() throws ResolutionException {
    }

    @Override
    protected IObject decode(final ByteBuffer buf) throws BlockCodecException {
        try {
            byte[] bytes;

            if (buf.hasArray()) {
                bytes = buf.array();
            } else {
                int size = buf.capacity();
                bytes = new byte[size];
                buf.get(bytes, 0, size);
            }

            // TODO:: use some more efficient way to deserialize JSON...
            //  maybe Jackson streaming API or something alike
            return IOC.resolve(
                    Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"),
                    new String(bytes, Charset.forName("UTF-8"))
            );
        } catch (ResolutionException e) {
            throw new BlockCodecException(e);
        }
    }
}
