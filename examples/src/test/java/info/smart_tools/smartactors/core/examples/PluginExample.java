package info.smart_tools.smartactors.core.examples;

import info.smart_tools.smartactors.core.examples.plugin.PluginServer;
import info.smart_tools.smartactors.core.iserver.IServer;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import org.junit.Test;

/**
 *  These are examples of using plugins.
 */
public class PluginExample {

    @Test
    public void initializeServer() throws ServerInitializeException {
        IServer server = new PluginServer();
        server.initialize();
    }

}
