package info.smart_tools.smartactors.core.security.encoding;

import info.smart_tools.smartactors.core.security.encoding.codecs.ICharSequenceCodec;
import info.smart_tools.smartactors.core.security.encoding.decoders.DecodingException;
import info.smart_tools.smartactors.core.security.encoding.encoders.EncodingException;
import info.smart_tools.smartactors.core.security.encoding.encoders.IEncoder;
import info.smart_tools.smartactors.core.security.encoding.encoders.IPasswordEncoder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class MDPasswordEncoderTest {
    private IEncoder encoder;
    private ICharSequenceCodec charSequenceCodec;
    private IPasswordEncoder passwordEncoder;

    private final String password = "testPassword";
    private final String encodePassword = "testPasswordEncode";
    private final String charSequenceEncodePassword = "testPasswordCharSequenceEncode";

    @Before
    public void setUp() throws DecodingException, EncodingException {
        final String algorithm = "MD5";

        this.encoder = mock(IEncoder.class);
        this.charSequenceCodec = mock(ICharSequenceCodec.class);
        this.passwordEncoder = MDPasswordEncoder.create(algorithm, encoder, charSequenceCodec);
    }

    @Test
    public void encodePasswordTest() throws EncodingException, DecodingException {
        resetVerifyMockObject();

        String result = passwordEncoder.encode(password);

        assertNotEquals(result, null);
        assertEquals(result, charSequenceEncodePassword);

        verify(encoder, times(1)).encode(anyObject());
        verify(charSequenceCodec, times(1)).encode(password);
        verify(charSequenceCodec).decode(eq(encodePassword.getBytes()));
    }

    @Test
    public void encodePasswordWithSaltTest() throws EncodingException, DecodingException {
        resetVerifyMockObject();

        final String salt = "testSalt";
        String result = passwordEncoder.encode(password, salt);

        assertNotEquals(result, null);
        assertEquals(result, charSequenceEncodePassword);

        verify(encoder, times(1)).encode(anyObject());
        verify(charSequenceCodec, times(1)).encode(password + "{" + salt + "}");
        verify(charSequenceCodec).decode(eq(encodePassword.getBytes()));
    }

    private void resetVerifyMockObject() throws EncodingException, DecodingException {
        reset(encoder);
        reset(charSequenceCodec);

        when(encoder.encode(anyObject())).thenReturn(encodePassword.getBytes());
        when(charSequenceCodec.encode(anyString())).thenAnswer((Answer<byte[]>) invocation -> {
            Object[] args = invocation.getArguments();
            return args[0].toString().getBytes();
        });
        when(charSequenceCodec.decode(eq(encodePassword.getBytes()))).thenReturn(charSequenceEncodePassword);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_ThrowException_WithReason_InvalidIterations() throws EncodingException, DecodingException {
        resetVerifyMockObject();
        final int iterations = -1;
        passwordEncoder.setIterations(iterations);
    }
}
