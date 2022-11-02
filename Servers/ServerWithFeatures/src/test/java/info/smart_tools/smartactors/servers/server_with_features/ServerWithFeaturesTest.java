package info.smart_tools.smartactors.launcher.server_with_features;

import info.smart_tools.smartactors.server_development_tools.interfaces.iserver.IServer;
import info.smart_tools.smartactors.server_development_tools.interfaces.iserver.exception.ServerExecutionException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by sevenbits on 10/4/16.
 */
public class ServerWithFeaturesTest {

    @Test
    @Ignore
    public void checkServer()
            throws Exception {
        IServer server = new ServerWithFeatures();
        server.initialize();
        server.start();
        try {
            while (!Thread.interrupted()) {
                Thread.sleep(1L);
            }
        } catch (Throwable e) {
            throw new ServerExecutionException(e);
        }
    }
}
