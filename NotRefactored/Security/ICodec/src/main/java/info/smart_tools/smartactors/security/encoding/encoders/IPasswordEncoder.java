package info.smart_tools.smartactors.security.encoding.encoders;


import javax.annotation.Nonnull;

/**
 * Interface for password encoder component
 */
public interface IPasswordEncoder {

    /**
     * Encodes raw password
     * @param password raw password
     * @return encoded string
     * @throws EncodingException if any errors is occurred during encoding
     */
    String encode(@Nonnull final String password) throws EncodingException;

    /**
     * Merges salt and raw password and encodes
     * @param password raw password
     * @param salt generated salt string
     * @return encoded string
     * @throws EncodingException if any errors is occurred during encoding
     */
    String encode(@Nonnull final String password, @Nonnull final String salt) throws EncodingException;

    /**
     * Setter for encode iterations
     * @param iterations amount
     */
    void setIterations(final int iterations);
}
