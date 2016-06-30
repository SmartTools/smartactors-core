package info.smart_tools.smartactors.core.server_with_iobject;

import org.junit.Test;

/**
 * Tests for {@link Server} with {@link info.smart_tools.smartactors.core.iobject.IObject}
 */
public class ServerTest {

    @Test
    public void checkServer()
            throws Exception {
        Server server = new Server();
        server.initialize();
        server.start();
    }

}
