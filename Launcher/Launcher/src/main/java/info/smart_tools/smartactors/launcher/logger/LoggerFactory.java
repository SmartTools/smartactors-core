package info.smart_tools.smartactors.launcher.logger;

import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;

public final class LoggerFactory {

    private static ILogger logger;

    public static ILogger getLogger() {
        boolean isDebug = Boolean.parseBoolean(System.getProperty("launcher.debug"));
        if (logger == null) {
            logger = new Logger(isDebug);
        }

        return logger;
    }
}
