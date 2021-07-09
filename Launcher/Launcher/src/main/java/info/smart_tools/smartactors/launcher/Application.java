package info.smart_tools.smartactors.launcher;

import info.smart_tools.smartactors.launcher.interfaces.ILauncher;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.logger.LoggerFactory;

public class Application {

    private static final ILogger log = LoggerFactory.getLogger();

    public static void main(final String[] args) throws Exception {
        log.info("Initializing launcher...");
        Thread.sleep(10000);
        ILauncher launcher = new Launcher();
        launcher.initialize();
        launcher.start();
    }
}
