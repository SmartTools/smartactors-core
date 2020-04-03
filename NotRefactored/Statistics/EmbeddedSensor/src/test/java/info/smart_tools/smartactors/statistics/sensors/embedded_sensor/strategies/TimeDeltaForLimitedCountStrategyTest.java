package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.strategies;

import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions.EmbeddedSensorStrategyException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link TimeDeltaForLimitedCountStrategy}.
 */
public class TimeDeltaForLimitedCountStrategyTest {
    private final IFieldName startTimeField = mock(IFieldName.class);
    private TimeDeltaForLimitedCountStrategy strategy;

    private IMessageProcessor makeMPWithTime(final Object time)
            throws Exception {
        IMessageProcessor mp = mock(IMessageProcessor.class);
        IObject ctx = mock(IObject.class);

        when(mp.getContext()).thenReturn(ctx);
        when(ctx.getValue(same(startTimeField))).thenReturn(time);

        return mp;
    }

    private void create(final int count) {
        strategy = new TimeDeltaForLimitedCountStrategy(count, startTimeField);
    }

    @Test
    public void Should_recordTimeDeltas()
            throws Exception {
        create(10);
        TimeDeltaForLimitedCountStrategy.PeriodState state = strategy.initPeriod();

        strategy.updatePeriod(state, makeMPWithTime(100L), 200);
        strategy.updatePeriod(state, makeMPWithTime(200L), 300);
        strategy.updatePeriod(state, makeMPWithTime(300L), 400);

        assertEquals(Arrays.asList(100L, 100L, 100L), strategy.extractPeriod(state));
    }

    @Test
    public void Should_limitAmountOfMeasurements()
            throws Exception {
        create(2);
        TimeDeltaForLimitedCountStrategy.PeriodState state = strategy.initPeriod();

        strategy.updatePeriod(state, makeMPWithTime(100L), 200);
        strategy.updatePeriod(state, makeMPWithTime(200L), 300);
        strategy.updatePeriod(state, makeMPWithTime(300L), 400);

        assertEquals(Arrays.asList(100L, 100L), strategy.extractPeriod(state));
    }

    @Test(expected = EmbeddedSensorStrategyException.class)
    public void Should_wrapClassCastException()
            throws Exception {
        create(1);
        strategy.updatePeriod(strategy.initPeriod(), makeMPWithTime(100), 200L);
    }
}
