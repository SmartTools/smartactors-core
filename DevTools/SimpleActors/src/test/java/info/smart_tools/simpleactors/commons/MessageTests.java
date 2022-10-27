package info.smart_tools.simpleactors.commons;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MessageTests {

    @Test
    public void creatingMessage() {
        IMessage message = Message
                .builder()
                .add("test", 1)
                .build();
        Integer value = message.get("test");
        assertEquals((Integer) 1, value);

        message.put("test2", "1");
        assertEquals("1", message.get("test2"));
    }

    @Test(expected = ClassCastException.class)
    public void exceptionOnCast() {
        IMessage message = Message
                .builder()
                .add("test", 1)
                .build();
        String test = message.get("test");
        fail();
    }
}
