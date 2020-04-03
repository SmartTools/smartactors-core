package info.smart_tools.smartactors.security.encoding.decoders;

import javax.annotation.Nonnull;

/**
 * Interface for charset decoder
 */
public interface ICharSequenceDecoder {

    /**
     * Encodes from a bytes to string.
     * @param message encoded message
     * @return decoded message
     * @throws DecodingException if any error is occurred during decoding
     */
    String decode(@Nonnull final byte[] message) throws DecodingException;
}
