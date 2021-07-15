package info.smart_tools.smartactors.launcher;

import info.smart_tools.smartactors.launcher.interfaces.ILauncher;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.logger.Logger;

public class Application {

    public static void main(final String[] args) throws Exception {
        ILogger log = new Logger();
        log.info("Initializing launcher...");

        Thread.sleep(10000);

        ILauncher launcher = new Launcher();
        launcher.initialize();
        launcher.start();
    }
}
