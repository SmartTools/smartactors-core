package info.smart_tools.smartactors.core.security.encoding.encoders;

import javax.annotation.Nonnull;

/**
 *
 */
public interface ICharSequenceEncoder {
    /**
     *
     * @param message
     * @return
     * @throws EncodingException
     */
    byte[] encode(@Nonnull final CharSequence message) throws EncodingException;
}
