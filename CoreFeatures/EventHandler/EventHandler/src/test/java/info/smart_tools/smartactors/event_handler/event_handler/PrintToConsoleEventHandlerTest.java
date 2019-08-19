package info.smart_tools.smartactors.event_handler.event_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.event_handler.event_handler.exception.EventHandlerException;
import info.smart_tools.smartactors.event_handler.event_handler.exception.ExtendedEventHandlerException;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrintToConsoleEventHandlerTest {

    @Test
    public void should_be_not_null() {
        IEventHandler handler = new PrintToConsoleEventHandler("test1", (event) -> {});
        assertNotNull(handler);
        assertNotNull("test1", handler.getEventHandlerKey());

        IAction<IEvent> testAction = mock(IAction.class);
        handler = new PrintToConsoleEventHandler(
                "test2",
                (event) -> {}, new HashMap<Object, Object>(){{
                    put("action", testAction);
                }}
        );
        assertNotNull(handler);
        assertNotNull("test2", handler.getEventHandlerKey());
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_on_creating_with_invalid_processor() {
        IEventHandler handler = new PrintToConsoleEventHandler(
                "test",
                (event) -> {}, new HashMap<Object, Object>(){{
                    put("action", "");
                }}
        );
    }

    @Test
    public void should_call_specific_processor_or_default()
            throws Exception {
        IAction<IEvent> defaultAction = mock(IAction.class);
        IAction<IEvent> testAction = mock(IAction.class);
        IEventHandler handler = new PrintToConsoleEventHandler(
                "test",
                defaultAction,
                new HashMap<Object, Object>(){{
                    put(TestClass1.class.getCanonicalName(), testAction);
                }}
        );
        IEvent eventWithSpecifiedType = mock(IEvent.class);
        IEvent event = mock(IEvent.class);
        when(eventWithSpecifiedType.getBody()).thenReturn(new TestClass1());
        when(event.getBody()).thenReturn(new TestClass2());
        handler.handle(eventWithSpecifiedType);
        verify(testAction, times(1)).execute(eventWithSpecifiedType);
        verify(defaultAction, times(0)).execute(eventWithSpecifiedType);
        handler.handle(event);
        verify(testAction, times(0)).execute(event);
        verify(defaultAction, times(1)).execute(event);
        Object action = ((PrintToConsoleEventHandler) handler).removeProcessor(TestClass1.class.getCanonicalName());
        assertEquals(testAction, action);
    }

    @Test (expected = EventHandlerException.class)
    public void should_throw_the_specified_exception_on_an_exception_in_an_processor()
            throws Exception {
        IAction<IEvent> testAction = mock(IAction.class);
        IEventHandler handler = new PrintToConsoleEventHandler(
                "test",
                (event) -> {},
                new HashMap<Object, Object>() {{
                    put(TestClass1.class.getCanonicalName(), testAction);
                }}
        );
        IEvent event = mock(IEvent.class);
        when(event.getBody()).thenReturn(new TestClass1());
        doThrow(new RuntimeException()).when(testAction).execute(event);
        handler.handle(event);
    }

    @Test
    public void should_do_nothing_on_null_event()
            throws Exception {
        IEventHandler handler = new PrintToConsoleEventHandler("test", (event) -> {});
        handler.handle(null);
    }

    @Test (expected = ExtendedEventHandlerException.class)
    public void should_throw_specified_exception_on_invalid_key()
            throws Exception {
        IEventHandler handler = new PrintToConsoleEventHandler("test", (event) -> {});
        ((IExtendedEventHandler) handler).addProcessor(5, mock(IAction.class));
    }

    @Test (expected = ExtendedEventHandlerException.class)
    public void should_throw_specified_exception_on_invalid_processor()
            throws Exception {
        IEventHandler handler = new PrintToConsoleEventHandler("test", (event) -> {});
        ((IExtendedEventHandler) handler).addProcessor("test", 5);
    }
}


class TestClass1 {
}

class TestClass2 {
}