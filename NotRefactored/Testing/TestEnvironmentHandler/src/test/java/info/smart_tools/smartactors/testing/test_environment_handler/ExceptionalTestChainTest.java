package info.smart_tools.smartactors.testing.test_environment_handler;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link ExceptionalTestChain}.
 */
public class ExceptionalTestChainTest {

    @Test
    public void checkCreation()
            throws Exception {
        IReceiverChain chain = new ExceptionalTestChain();
        assertNotNull(chain);
    }

    @Test
    public void checkGetName()
            throws Exception {
        IReceiverChain chain = new ExceptionalTestChain();
        assertNotNull(chain.getId());
    }

    @Test
    public void checkNullOnCallMethods()
            throws Exception {
        IReceiverChain chain = new ExceptionalTestChain();
        assertNull(chain.get(0));
        assertNull(chain.getArguments(0));
        assertNull(chain.getExceptionalChainNamesAndEnvironments(new Exception()));
    }
}
