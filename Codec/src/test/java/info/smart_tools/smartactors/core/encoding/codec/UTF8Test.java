package info.smart_tools.smartactors.core.encoding.codec;

import info.smart_tools.smartactors.core.security.encoding.codecs.ICharSequenceCodec;
import info.smart_tools.smartactors.core.security.encoding.decoders.DecodingException;
import info.smart_tools.smartactors.core.security.encoding.encoders.EncodingException;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class UTF8Test {
    private final ICharSequenceCodec utf8 = UTF8.create();

    private final String SOURCE_MSG = "Hello my young friend. " +
            "You can see how it's is a nice text will converted to some shit, " +
            "but to see it, you should debug this text. Here are the things here.";

    // java utf-8 converter.
    private byte[] UTF_8_MSG;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        UTF_8_MSG = SOURCE_MSG.getBytes("UTF-8");
    }

    @Test
    public void encodeHexTest() throws EncodingException {
        byte[] utf8EncodeResult = utf8.encode(SOURCE_MSG);
        assertTrue(Arrays.equals(utf8EncodeResult, UTF_8_MSG));
    }

    @Test
    public void decodeHexTest() throws DecodingException {
        String utf8DecodeResult = utf8.decode(UTF_8_MSG);
        assertTrue(utf8DecodeResult.equals(SOURCE_MSG));
    }
}
