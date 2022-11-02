package info.smart_tools.smartactors.timer.timer;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test for {@link SystemTimeImpl}.
 */
public class SystemTimeImplTest {
    @Test
    public void Should_returnCurrentTimeInMilliseconds() {
        assertTrue(Math.abs(System.currentTimeMillis() - new SystemTimeImpl().currentTimeMillis()) < 10);
    }
}
