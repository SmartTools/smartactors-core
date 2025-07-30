package info.smart_tools.smartactors.base.interfaces.iresource_source.exceptions;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresource_source.IResourceSource;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link OutOfResourceException}.
 */
public class OutOfResourceExceptionTest {
    @Test(expected = InvalidArgumentException.class)
    public void Should_notBeCreated_When_givenResourceSourceIsNull()
            throws Exception {
        assertNotNull(new OutOfResourceException(null));
    }

    @Test
    public void Should_storeTheResourceSourceCausedTheException()
            throws Exception {
        IResourceSource source = mock(IResourceSource.class);
        OutOfResourceException exception = new OutOfResourceException(source);
        assertSame(source, exception.getSource());
    }
}
