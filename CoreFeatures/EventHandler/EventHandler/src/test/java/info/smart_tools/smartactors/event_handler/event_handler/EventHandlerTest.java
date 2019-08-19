package info.smart_tools.smartactors.event_handler.event_handler;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventHandlerTest {

    @Before
    public void init()
            throws Exception {

    }

    @Test
    public void should_be_initialized() {
        IEventHandlerContainer container = EventHandler.getEventHandlerContainer();
        assertNotNull(container);
        assertEquals(EventHandlerContainer.class, container.getClass());
    }

    @Test
    public void should_call_method_handle_of_inner_container()
            throws Exception {
        IEventHandlerContainer container = EventHandler.getEventHandlerContainer();
        ((IExtendedEventHandlerContainer) container).unregister(null);
        ((IExtendedEventHandlerContainer) container).unregister(null);
        IEventHandler handler = mock(IEventHandler.class);
        IEvent event = mock(IEvent.class);
        ((IExtendedEventHandlerContainer) container).register(handler);
        EventHandler.handle(event);
        verify(handler, times(1)).handle(event);
    }
}
