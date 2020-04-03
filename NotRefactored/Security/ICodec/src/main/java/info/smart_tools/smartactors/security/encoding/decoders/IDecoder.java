package info.smart_tools.smartactors.security.encoding.decoders;

import javax.annotation.Nonnull;

/**
 * Message decoder interface
 */
public interface IDecoder {

    /**
     * Decodes bytes to string
     * @param message encoded message
     * @return decoded message
     * @throws DecodingException if any errors is occurred
     */
    byte[] decode(@Nonnull final byte[] message) throws DecodingException;
}
