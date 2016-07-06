package info.smart_tools.smartactors.core.encoding.codec;

import info.smart_tools.smartactors.core.security.encoding.codecs.ICodec;
import info.smart_tools.smartactors.core.security.encoding.decoders.DecodingException;
import info.smart_tools.smartactors.core.security.encoding.encoders.EncodingException;

import javax.annotation.Nonnull;

/**
 *
 */
public class Hex implements ICodec {
    private final org.apache.commons.codec.binary.Hex hexCodec;

    private Hex() {
        this.hexCodec = new org.apache.commons.codec.binary.Hex();
    }

    /**
     *
     * @return
     */
    public static Hex create() {
        return new Hex();
    }

    @Override
    public byte[] encode(@Nonnull final byte[] message) throws EncodingException {
        try {
            return hexCodec.encode(message);
        } catch (Exception e) {
            throw new EncodingException("Hex encoding has been failed because: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decode(@Nonnull final byte[] message) throws DecodingException {
        try {
            return hexCodec.decode(message);
        } catch (Exception e) {
            throw new DecodingException("Hex decoding has been failed because: " + e.getMessage(), e);
        }
    }
}
