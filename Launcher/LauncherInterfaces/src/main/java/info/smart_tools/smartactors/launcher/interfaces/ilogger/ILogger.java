package info.smart_tools.smartactors.launcher.interfaces.ilogger;

public interface ILogger {

    void debug(String message, Object... args);
    void info(String message, Object... args);
    void warning(String message, Object... args);
    void error(String message, Object... args);
}
