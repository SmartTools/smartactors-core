package info.smart_tools.smartactors.core.receiver_chain;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ImmutableReceiverChain}.
 */
public class ImmutableReceiverChainTest {
    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidNamePassed()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain(null, new IMessageReceiver[0], new IObject[0], mock(Map.class)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidArgumentsListPassed()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain("theChain", new IMessageReceiver[0], null, mock(Map.class)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_numberOfReceiversDoesNotMatchNumberOfArgumentsObjects()
            throws Exception {
        new ImmutableReceiverChain("theChain", new IMessageReceiver[1], new IObject[0], mock(Map.class));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidReceiversListPassed()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain("theChain", null, new IObject[0], mock(Map.class)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidExceptionsMappingGiven()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain("theChain", new IMessageReceiver[0], new IObject[0], null));
    }

    @Test
    public void Should_beConstructedWithValidParameters()
            throws Exception {
        IMessageReceiver[] receivers = new IMessageReceiver[0];

        IReceiverChain chain = new ImmutableReceiverChain("theChain", receivers, new IObject[0], mock(Map.class));

        assertEquals("theChain", chain.getName());
    }

    @Test
    public void Should_get_returnMessageReceiversAndArgumentObjects()
            throws Exception {
        IMessageReceiver[] receivers = new IMessageReceiver[] {
                mock(IMessageReceiver.class),
                mock(IMessageReceiver.class)};
        IObject[] arguments = new IObject[] {
                mock(IObject.class),
                mock(IObject.class)};

        IReceiverChain chain = new ImmutableReceiverChain("theChain", receivers, arguments, mock(Map.class));

        assertSame(receivers[0], chain.get(0));
        assertSame(arguments[0], chain.getArguments(0));
        assertSame(receivers[1], chain.get(1));
        assertSame(arguments[1], chain.getArguments(1));
        assertNull(chain.get(2));
        assertNull(chain.getArguments(2));
    }

    @Test
    public void Should_getExceptionalChainUsingMappingMap()
            throws Exception {
        Map<Class<? extends Throwable>, IReceiverChain> mappingMap = new HashMap<Class<? extends Throwable>, IReceiverChain>() {{
            put(InvalidArgumentException.class, mock(IReceiverChain.class));
        }};
        Throwable selfCaused = mock(Throwable.class);

        when(selfCaused.getCause()).thenReturn(selfCaused);

        IReceiverChain chain = new ImmutableReceiverChain("theChain", new IMessageReceiver[0], new IObject[0], mappingMap);

        assertNull(chain.getExceptionalChain(new NullPointerException()));
        assertNull(chain.getExceptionalChain(new IllegalStateException()));
        assertNull(chain.getExceptionalChain(selfCaused));
        assertSame(mappingMap.get(InvalidArgumentException.class), chain.getExceptionalChain(new InvalidArgumentException("invalid")));
        assertSame(mappingMap.get(InvalidArgumentException.class), chain.getExceptionalChain(
                new IllegalStateException(new InvalidArgumentException(new Throwable()))));
    }
}
