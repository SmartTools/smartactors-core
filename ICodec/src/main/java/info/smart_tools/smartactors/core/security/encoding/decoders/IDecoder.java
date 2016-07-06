package info.smart_tools.smartactors.core.security.encoding.decoders;

import javax.annotation.Nonnull;

/**
 *
 */
public interface IDecoder {
    /**
     *
     * @param message
     * @return
     * @throws DecodingException
     */
    byte[] decode(@Nonnull final byte[] message) throws DecodingException;
}
