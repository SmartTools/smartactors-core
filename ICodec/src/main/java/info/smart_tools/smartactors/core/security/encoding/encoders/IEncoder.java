package info.smart_tools.smartactors.core.security.encoding.encoders;

import javax.annotation.Nonnull;

/**
 *
 */
public interface IEncoder {
    /**
     *
     * @param message
     * @return
     * @throws EncodingException
     */
    byte[] encode(@Nonnull final byte[] message) throws EncodingException;
}
