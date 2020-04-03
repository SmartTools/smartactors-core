package info.smart_tools.smartactors.security.encoding.codec;

import info.smart_tools.smartactors.security.encoding.codecs.ICodec;
import info.smart_tools.smartactors.security.encoding.decoders.DecodingException;
import info.smart_tools.smartactors.security.encoding.encoders.EncodingException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class HexTest {
    private final ICodec hex = Hex.create();
    private final byte[] SOURCE_MSG = ("Hello my young friend. " +
            "You can see how it's is a nice text will converted to some shit, " +
            "but to see it, you should debug this text. Here are the things here.").getBytes();

    private final byte[] HEX_MSG = ("48656c6c6f206d7920796f756e672066726" +
            "9656e642e20596f752063616e2073656520686f7720697427" +
            "732069732061206e69636520746578742077696c6c20636f6" +
            "e76657274656420746f20736f6d6520736869742c20627574" +
            "20746f207365652069742c20796f752073686f756c6420646" +
            "5627567207468697320746578742e20486572652061726520" +
            "746865207468696e677320686572652e").getBytes();

    @Test
    public void encodeHexTest() throws EncodingException {
        byte[] hexEncodeResult = hex.encode(SOURCE_MSG);
        assertTrue(Arrays.equals(hexEncodeResult, HEX_MSG));
    }

    @Test
    public void decodeHexTest() throws DecodingException {
        byte[] hexDecodeResult = hex.decode(HEX_MSG);
        assertTrue(Arrays.equals(hexDecodeResult, SOURCE_MSG));
    }
}
