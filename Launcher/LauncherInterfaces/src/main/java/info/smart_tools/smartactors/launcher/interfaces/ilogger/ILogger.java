package info.smart_tools.smartactors.launcher.interfaces.ilogger;

/**
 * Basic logger interface
 */
public interface ILogger {

    /**
     * Log message with DEBUG level
     * @param message message with formatting
     * @param args arguments for formatting
     */
    void debug(String message, Object... args);

    /**
     * Log message with INFO level
     * @param message message with formatting
     * @param args arguments for formatting
     */
    void info(String message, Object... args);

    /**
     * Log message with WARNING level
     * @param message message with formatting
     * @param args arguments for formatting
     */
    void warning(String message, Object... args);

    /**
     * Log message with ERROR level
     * @param message message with formatting
     * @param args arguments for formatting
     */
    void error(String message, Object... args);
}
