package info.smart_tools.smartactors.core.security.encoding.decoders;

import javax.annotation.Nonnull;

/**
 *
 */
public interface ICharSequenceDecoder {
    /**
     *
     * @param message
     * @return
     * @throws DecodingException
     */
    String decode(@Nonnull final byte[] message) throws DecodingException;
}
