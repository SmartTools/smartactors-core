package info.smart_tools.smartactors.core.iresource_source.exceptions;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresource_source.IResourceSource;
import org.junit.Test;

import static org.junit.Assert.*;
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
