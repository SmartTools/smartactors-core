package info.smart_tools.smartactors.core.security.encoding.encoders;


import javax.annotation.Nonnull;

/**
 *
 */
public interface IPasswordEncoder {
    /**
     *
     * @param password
     * @return
     * @throws EncodingException
     */
    String encode(@Nonnull final String password) throws EncodingException;

    /**
     *
     * @param password
     * @param salt
     * @return
     * @throws EncodingException
     */
    String encode(@Nonnull final String password, @Nonnull final String salt) throws EncodingException;

    /**
     *
     * @param iterations
     */
    void setIterations(final int iterations);
}
