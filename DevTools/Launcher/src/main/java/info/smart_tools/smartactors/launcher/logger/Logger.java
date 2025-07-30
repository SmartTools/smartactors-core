package info.smart_tools.smartactors.launcher.logger;

import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;

import java.text.MessageFormat;

public class Logger implements ILogger {

    private final boolean isDebug;

    public Logger() {
        this.isDebug = Boolean.parseBoolean(System.getProperty("launcher.debug"));
    }

    @Override
    public void debug(
            final String message,
            final Object... args
    ) {
        String formattedMessage = formatMessage(message, args);
        if (isDebug) {
            System.out.println("[DEBUG] " + formattedMessage);
        }
    }

    @Override
    public void info(
            final String message,
            final Object... args
    ) {
        String formattedMessage = formatMessage(message, args);
        System.out.println("[\033[1;34mINFO\033[0m] " + formattedMessage);
    }

    @Override
    public void warning(
            final String message,
            final Object... args
    ) {
        String formattedMessage = formatMessage(message, args);
        System.out.println("[\033[1;33mWARNING\033[0m] " + formattedMessage);
    }

    @Override
    public void error(
            final String message,
            final Object... args
    ) {
        String formattedMessage = formatMessage(message, args);
        System.out.println("[\033[1;31mERROR\033[0m] " + formattedMessage);
    }

    private String formatMessage(
            final String message,
            final Object... args
    ) {
        return MessageFormat.format(message, args);
    }
}
