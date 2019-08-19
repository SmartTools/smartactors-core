package info.smart_tools.smartactors.event_handler.event_handler;

import info.smart_tools.smartactors.event_handler.event_handler.exception.EventHandlerException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventHandlerContainerTest {

    @Before
    public void init()
            throws Exception {

    }

    @Test
    public void should_be_not_null_and_initialized() {
        IEventHandlerContainer container = new EventHandlerContainer();

        assertNotNull(container);
        IEventHandler consoleWriterHandler = ((IExtendedEventHandlerContainer) container).unregister(null);
        IEventHandler emptyHandler = ((IExtendedEventHandlerContainer) container).unregister(null);

        // check default container handlers
        assertEquals(PrintToConsoleEventHandler.class, consoleWriterHandler.getClass());
        assertNull(emptyHandler);

        container = new EventHandlerContainer();
        consoleWriterHandler = ((IExtendedEventHandlerContainer) container).unregister("consoleLogger");
        emptyHandler = ((IExtendedEventHandlerContainer) container).unregister("empty");
        assertEquals(PrintToConsoleEventHandler.class, consoleWriterHandler.getClass());
        assertNull(emptyHandler);
    }

    @Test
    public void should_call_sequentially_inner_handlers()
            throws Exception {
        IEventHandlerContainer container = new EventHandlerContainer();
        IEventHandler firstHandler = mock(IEventHandler.class);
        IEventHandler secondHandler = mock(IEventHandler.class);
        // remove default handlers
        ((IExtendedEventHandlerContainer) container).unregister(null);
        ((IExtendedEventHandlerContainer) container).unregister(null);
        ((IExtendedEventHandlerContainer) container).register(firstHandler);
        ((IExtendedEventHandlerContainer) container).register(secondHandler);
        IEvent event = mock(IEvent.class);
        container.handle(event);
        verify(firstHandler, times(0)).handle(event);
        verify(secondHandler, times(1)).handle(event);

        doThrow( new EventHandlerException("test")).when(secondHandler).handle(event);
        container.handle(event);
        verify(firstHandler, times(1)).handle(event);
        verify(secondHandler, times(2)).handle(event);
    }
}
