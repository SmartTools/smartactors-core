package info.smart_tools.smartactors.timer.timer;

import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;

/**
 * Default implementation of {@link ITime}.
 */
public class SystemTimeImpl implements ITime {
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
