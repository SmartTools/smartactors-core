package info.smart_tools.smartactors.core.security.encoding;


import info.smart_tools.smartactors.core.security.encoding.codecs.ICharSequenceCodec;
import info.smart_tools.smartactors.core.security.encoding.encoders.EncodingException;
import info.smart_tools.smartactors.core.security.encoding.encoders.IEncoder;
import info.smart_tools.smartactors.core.security.encoding.encoders.IPasswordEncoder;

import javax.annotation.Nonnull;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class MDPasswordEncoder implements IPasswordEncoder {
    private final String algorithm;
    private final IEncoder encoder;
    private final ICharSequenceCodec charSequenceCodec;
    private int iterations;

    private MDPasswordEncoder(
            final String algorithm,
            final IEncoder encoder,
            final ICharSequenceCodec charSequenceCodec
    ) {
        this.algorithm = algorithm;
        this.iterations = 1;
        this.encoder = encoder;
        this.charSequenceCodec = charSequenceCodec;
    }

    /**
     *
     * @param algorithm
     * @param encoder
     * @param charSequenceCodec
     * @return
     */
    public static MDPasswordEncoder create(
            @Nonnull final String algorithm,
            @Nonnull final IEncoder encoder,
            @Nonnull final ICharSequenceCodec charSequenceCodec
    ) {
        return new MDPasswordEncoder(algorithm, encoder, charSequenceCodec);
    }

    @Override
    public String encode(@Nonnull final String password) throws EncodingException {
        try {
            MessageDigest messageDigest = getMessageDigest();

            byte[] digest = messageDigest.digest(charSequenceCodec.encode(password));
            for (int i = 1; i < iterations; i++) {
                digest = messageDigest.digest(digest);
            }

            return charSequenceCodec.decode(encoder.encode(digest));
        } catch (Exception e) {
            throw new EncodingException("Password encoding has been failed because: " + e.getMessage(), e);
        }
    }

    @Override
    public String encode(@Nonnull final String password, @Nonnull final String salt) throws EncodingException {
        String saltedPass = mergePasswordAndSalt(password, salt);
        return encode(saltedPass);
    }

    /**
     *
     * @param iterations
     */
    @Override
    public void setIterations(final int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("Iterations value should be greater than zero");
        }
        this.iterations = iterations;
    }

    private String mergePasswordAndSalt(final String password, final String salt) {
        return new StringBuilder(password.length() + salt.length() + 2)
                .append(password)
                .append("{")
                .append(salt)
                .append("}")
                .toString();
    }

    private MessageDigest getMessageDigest() throws IllegalArgumentException {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm '" + algorithm + "'.");
        }
    }
}
