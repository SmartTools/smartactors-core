package info.smart_tools.smartactors.timer.interfaces.itimer;

/**
 * Interface for a object providing information about current system time.
 */
public interface ITime {
    /**
     * Get current time in milliseconds.
     *
     * @return current time in milliseconds since 01.01.1970 00:00
     */
    long currentTimeMillis();
}
