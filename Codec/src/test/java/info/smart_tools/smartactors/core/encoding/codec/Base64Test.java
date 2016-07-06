package info.smart_tools.smartactors.core.encoding.codec;

import info.smart_tools.smartactors.core.security.encoding.codecs.ICodec;
import info.smart_tools.smartactors.core.security.encoding.decoders.DecodingException;
import info.smart_tools.smartactors.core.security.encoding.encoders.EncodingException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class Base64Test {
    private final ICodec base64 = Base64.create();
    private final byte[] SOURCE_MSG = ("Hello my young friend. " +
            "You can see how it's is a nice text will converted to some shit, " +
            "but to see it, you should debug this text. Here are the things here.").getBytes();

    private final byte[] BASE64_MSG = ("SGVsbG8gbXkgeW91bmcgZnJpZW5kLi" +
            "BZb3UgY2FuIHNlZSBob3cgaXQncyBpcyBhIG5pY2UgdGV4" +
            "dCB3aWxsIGNvbnZlcnRlZCB0byBzb21lIHNoaXQsIGJ1dC" +
            "B0byBzZWUgaXQsIHlvdSBzaG91bGQgZGVidWcgdGhpcyB0" +
            "ZXh0LiBIZXJlIGFyZSB0aGUgdGhpbmdzIGhlcmUu").getBytes();

    @Test
    public void encodeBase64Test() throws EncodingException {
        byte[] base64EncodeResult = base64.encode(SOURCE_MSG);
        assertTrue(Arrays.equals(base64EncodeResult, BASE64_MSG));
    }

    @Test
    public void decodeBase64Test() throws DecodingException {
        byte[] base64DecodeResult = base64.decode(BASE64_MSG);
        assertTrue(Arrays.equals(base64DecodeResult, SOURCE_MSG));
    }
}
