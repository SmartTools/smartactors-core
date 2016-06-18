package info.smart_tools.smartactors.core.server_with_plugin_loader;

import org.junit.Test;

/**
 * Tests for implementation of {@link info.smart_tools.smartactors.core.iserver.IServer}
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
