package info.smart_tools.smartactors.security.encoding.codec;

import info.smart_tools.smartactors.security.encoding.codecs.ICodec;
import info.smart_tools.smartactors.security.encoding.decoders.DecodingException;
import info.smart_tools.smartactors.security.encoding.encoders.EncodingException;

import javax.annotation.Nonnull;

/**
 * Codec implementation for base64 algorithm
 */
public class Base64 implements ICodec {
    private final java.util.Base64.Encoder base64Encoder;
    private final java.util.Base64.Decoder base64Decoder;

    private Base64() {
        this.base64Encoder = java.util.Base64.getEncoder();
        this.base64Decoder = java.util.Base64.getDecoder();
    }

    /**
     * Factory method
     * @return instance of Base64
     */
    public static Base64 create() {
        return new Base64();
    }

    @Override
    public byte[] encode(@Nonnull final byte[] message) throws EncodingException {
        try {
            return base64Encoder.encode(message);
        } catch (Exception e) {
            throw new EncodingException("Base64 encoding has been failed because: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decode(@Nonnull final byte[] message) throws DecodingException {
        try {
            return base64Decoder.decode(message);
        } catch (Exception e) {
            throw new DecodingException("Base64 decoding has been failed because: " + e.getMessage(), e);
        }
    }
}
