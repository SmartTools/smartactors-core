package info.smart_tools.smartactors.message_processing.null_response_strategy;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Test for {@link NullResponseStrategy}.
 */
public class NullResponseStrategyTest {
    @Test
    public void Should_doNothing() throws Exception {
        NullResponseStrategy.INSTANCE.sendResponse(mock(IObject.class));
    }
}
