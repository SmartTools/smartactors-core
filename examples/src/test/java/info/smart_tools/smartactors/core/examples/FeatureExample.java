package info.smart_tools.smartactors.core.examples;

import info.smart_tools.smartactors.core.examples.feature.FeatureServer;
import info.smart_tools.smartactors.core.feature_manager.FeatureManager;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 *  Example how to use {@link FeatureManager} and auto load plugins.
 */
public class FeatureExample {

    @Test
    public void initializeServer() throws ServerInitializeException, InterruptedException, IOException {
        // create dir for jars
        Files.createDirectories(Paths.get("target", "libs"));
        // init server
        Object initWaiter = new Object();
        FeatureServer server = new FeatureServer(initWaiter);
        server.initialize();
        // copy jars
        Files.copy(Paths.get("libs", "Plugin1.jar"), Paths.get("target", "libs", "Plugin1.jar"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get("libs", "Plugin2.jar"), Paths.get("target", "libs", "Plugin2.jar"), StandardCopyOption.REPLACE_EXISTING);
        // wait for initialization to complete
        synchronized (initWaiter) {
            while (!server.isInitialized()) {
                initWaiter.wait();
            }
        }
    }

}
