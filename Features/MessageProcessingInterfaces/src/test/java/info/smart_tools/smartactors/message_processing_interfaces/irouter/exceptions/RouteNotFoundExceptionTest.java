package info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link RouteNotFoundException}.
 */
public class RouteNotFoundExceptionTest {
    @Test(expected = RouteNotFoundException.class)
    public void checkMessageMethod()
            throws RouteNotFoundException {
        String str = "test";
        RouteNotFoundException exception = new RouteNotFoundException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }
}
