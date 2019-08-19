package info.smart_tools.smartactors.security.encoding.encoders;

import javax.annotation.Nonnull;

/**
 * Message encoder interface
 */
public interface IEncoder {

    /**
     * Encodes string to byte array
     * @param message given text
     * @return encoded
     * @throws EncodingException if any errors is occurred
     */
    byte[] encode(@Nonnull byte[] message) throws EncodingException;
}
