package info.smart_tools.smartactors.testing.test_http_endpoint;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link TestChannelHandler}.
 */
public class TestChannelHandlerTest {

    @Test
    public void checkCreation()
            throws Exception {
        List<Object> storage = new ArrayList<Object>();
        TestChannelHandler channelHandler = new TestChannelHandler(storage);
        assertNotNull(channelHandler);
    }

    @Test
    public void checkSendMethod()
            throws Exception {
        List<Object> storage = new ArrayList<Object>();
        Object test = new Object();
        TestChannelHandler channelHandler = new TestChannelHandler(storage);
        channelHandler.send(test);
        assertSame(storage.get(0), test);
    }

    @Test
    public void checkInitMethod()
            throws Exception {
        List<Object> storage = new ArrayList<>();
        Object test = new Object();
        TestChannelHandler channelHandler = new TestChannelHandler(storage);
        channelHandler.send(test);
        Object newTest = new Object();
        List<Object> anotherStorage = new ArrayList<>();
        channelHandler.init(anotherStorage);
        channelHandler.send(newTest);
        assertEquals(storage.size(), 1);
        assertSame(storage.get(0), test);
        assertEquals(anotherStorage.size(), 1);
        assertSame(anotherStorage.get(0), newTest);
    }
}
