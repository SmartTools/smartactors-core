package info.smart_tools.smartactors.core.encoding.codec;

import info.smart_tools.smartactors.core.security.encoding.codecs.ICharSequenceCodec;
import info.smart_tools.smartactors.core.security.encoding.decoders.DecodingException;
import info.smart_tools.smartactors.core.security.encoding.encoders.EncodingException;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 *
 */
public class UTF8 implements ICharSequenceCodec {
    private final CharsetEncoder encoder;
    private final CharsetDecoder decoder;

    private UTF8() {
        final Charset charsetUTF8 = Charset.forName("UTF-8");
        this.encoder = charsetUTF8.newEncoder();
        this.decoder = charsetUTF8.newDecoder();
    }

    public static UTF8 create() {
        return new UTF8();
    }

    /**
     *
     * @param message
     * @return
     * @throws EncodingException
     */
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

    /**
     *
     * @param message
     * @return
     * @throws DecodingException
     */
    @Override
    public String decode(@Nonnull final byte[] message) throws DecodingException {
        try {
            return decoder.decode(ByteBuffer.wrap(message)).toString();
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("UTF-8 decoding has been failed because: " + e.getMessage(), e);
        }
    }
}
