package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.utils;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link PrependSensorReceiverStrategy}.
 */
public class PrependSensorReceiverStrategyTest {
    @Test
    public void Should_returnSensorWhenThereWasNoOriginalReceiver()
            throws Exception {
        IMessageReceiver sr = mock(IMessageReceiver.class);

        assertSame(sr, new PrependSensorReceiverStrategy().resolve(null, sr));
    }

    @Test
    public void Should_returnCompositeReceiverWhenBothOriginalReceiverAndSensorReceiverArePresent()
            throws Exception {
        IMessageReceiver sr = mock(IMessageReceiver.class);
        IMessageReceiver or = mock(IMessageReceiver.class);

        IMessageReceiver cr = new PrependSensorReceiverStrategy().resolve(or, sr);

        IMessageProcessor mp = mock(IMessageProcessor.class);

        cr.receive(mp);

        verify(sr).receive(same(mp));
        verify(or).receive(same(mp));
    }
}