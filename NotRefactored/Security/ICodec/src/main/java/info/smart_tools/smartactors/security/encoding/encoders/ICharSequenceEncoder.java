package info.smart_tools.smartactors.security.encoding.encoders;

import javax.annotation.Nonnull;

/**
 * Interface for charset encoder
 */
public interface ICharSequenceEncoder {

    /**
     * Encodes from a CharSequence message to bytes.
     * @param message raw message
     * @return encoded message
     * @throws EncodingException if any error is occurred during encoding
     */
    byte[] encode(@Nonnull final CharSequence message) throws EncodingException;
}
