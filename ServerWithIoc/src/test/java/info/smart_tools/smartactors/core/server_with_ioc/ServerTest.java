package info.smart_tools.smartactors.core.server_with_ioc;

import org.junit.Test;

/**
 * Created by sevenbits on 4/29/16.
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
