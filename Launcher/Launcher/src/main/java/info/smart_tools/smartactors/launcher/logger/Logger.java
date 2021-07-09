package info.smart_tools.smartactors.launcher.logger;

import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;

import java.text.MessageFormat;

public class Logger implements ILogger {

    private final boolean isDebug;

    public Logger(
            boolean isDebug
    ) {
        this.isDebug = isDebug;
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
        System.out.println("[INFO] " + formattedMessage);
    }

    @Override
    public void warning(
            final String message,
            final Object... args
    ) {
        String formattedMessage = formatMessage(message, args);
        System.out.println("[WARNING] " + formattedMessage);
    }

    @Override
    public void error(
            final String message,
            final Object... args
    ) {
        String formattedMessage = formatMessage(message, args);
        System.out.println("[ERROR] " + formattedMessage);
    }

    private String formatMessage(
            final String message,
            final Object... args
    ) {
        return MessageFormat.format(message, args);
    }
}
