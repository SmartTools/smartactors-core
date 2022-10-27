package info.smart_tools.smartactors.security.encoding.codec;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.security.encoding.codecs.ICharSequenceCodec;
import info.smart_tools.smartactors.security.encoding.decoders.DecodingException;
import info.smart_tools.smartactors.security.encoding.encoders.EncodingException;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * Charset codec implementation
 */
public class CharSequenceCodec implements ICharSequenceCodec {

    private final CharsetEncoder encoder;
    private final CharsetDecoder decoder;

    private CharSequenceCodec(final String name) throws InvalidArgumentException {
        try {
            final Charset charsetUTF8 = Charset.forName(name);
            this.encoder = charsetUTF8.newEncoder();
            this.decoder = charsetUTF8.newDecoder();
        } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            throw new InvalidArgumentException("Error during create charset.", e);
        }
    }

    /**
     * Factory method for create charset
     * @param name of charset
     * @return charset instance
     * @throws InvalidArgumentException if any error is occurred
     */
    public static CharSequenceCodec create(final String name) throws InvalidArgumentException {
        if (name == null) {
            throw new InvalidArgumentException("Charset name can't be null");
        }
        return new CharSequenceCodec(name);
    }

    @Override
    public byte[] encode(@Nonnull final CharSequence message) throws EncodingException {
        try {
            ByteBuffer encodedBuffer = encoder.encode(CharBuffer.wrap(message));
            byte[] bufferCopy = new byte[encodedBuffer.limit()];
            System.arraycopy(encodedBuffer.array(), 0, bufferCopy, 0, encodedBuffer.limit());

            return bufferCopy;
        } catch (CharacterCodingException e) {
            throw new EncodingException("UTF-8 encoding has been failed because: " + e.getMessage(), e);
        }
    }

    @Override
    public String decode(@Nonnull final byte[] message) throws DecodingException {
        try {
            return decoder.decode(ByteBuffer.wrap(message)).toString();
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("UTF-8 decoding has been failed because: " + e.getMessage(), e);
        }
    }
}
