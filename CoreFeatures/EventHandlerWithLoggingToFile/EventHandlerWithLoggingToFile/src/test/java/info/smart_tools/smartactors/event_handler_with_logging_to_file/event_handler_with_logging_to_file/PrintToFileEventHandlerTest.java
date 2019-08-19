package info.smart_tools.smartactors.event_handler_with_logging_to_file.event_handler_with_logging_to_file;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionTwoArgs;
import info.smart_tools.smartactors.event_handler.event_handler.IEvent;
import info.smart_tools.smartactors.event_handler.event_handler.IEventHandler;
import info.smart_tools.smartactors.event_handler.event_handler.IExtendedEventHandler;
import info.smart_tools.smartactors.event_handler.event_handler.exception.EventHandlerException;
import info.smart_tools.smartactors.event_handler.event_handler.exception.ExtendedEventHandlerException;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrintToFileEventHandlerTest {

    @Test
    public void should_be_not_null()
            throws Exception {
        IEventHandler handler = new PrintToFileEventHandler(
                "test1", (params) -> {}, (event, writer) -> {}
                );
        assertNotNull(handler);
        assertNotNull("test1", handler.getEventHandlerKey());

        IActionTwoArgs<IEvent, PrintWriter> testAction = mock(IActionTwoArgs.class);
        handler = new PrintToFileEventHandler(
                "test2",
                (params) -> {},
                (event, writer) -> {},
                new HashMap<Object, Object>(){{
                    put("action", testAction);
                }}
        );
        assertNotNull(handler);
        assertNotNull("test2", handler.getEventHandlerKey());
        Object removedAction = ((IExtendedEventHandler) handler).removeProcessor("action");
        assertEquals(testAction, removedAction);
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_on_creating_with_invalid_processor() {
        IEventHandler handler = new PrintToFileEventHandler(
                "test",
                (params) -> {},
                (event, writer) -> {},
                new HashMap<Object, Object>(){{
                    put("action", "");
                }}
        );
    }

    @Test (expected = EventHandlerException.class)
    public void should_throw_the_specified_exception_on_an_exception_in_an_processor()
            throws Exception {
        IActionTwoArgs<IEvent, PrintWriter> testAction = mock(IActionTwoArgs.class);

        IEvent event = mock(IEvent.class);
        when(event.getBody()).thenReturn(new TestClass1());
        IEventHandler handler = new PrintToFileEventHandler(
                "test",
                (params) -> {
                    throw new RuntimeException();
                },
                (e, writer) -> {},
                new HashMap<Object, Object>() {{
                    put(TestClass1.class.getCanonicalName(), testAction);
                }}
        );
        handler.handle(event);
    }

    @Test
    public void should_process_an_event_if_nested_writer_already_busy_by_other_thread()
            throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        IEvent event1 = mock(IEvent.class);
        when(event1.getBody()).thenReturn(new TestClass1());
        IEvent event2 = mock(IEvent.class);
        when(event2.getBody()).thenReturn(new TestClass2());
        AtomicInteger counter = new AtomicInteger(0);
        IAction<PrintToFileWriterParameters> writer = (params) -> {
            counter.incrementAndGet();
            while (!params.getQueue().isEmpty()) {
                IEvent event = params.getQueue().poll();
                params
                        .getProcessors()
                        .get(event.getBody().getClass().getCanonicalName())
                        .execute(event, null);
            }
        };
        IEventHandler handler = new PrintToFileEventHandler(
                "testHandler",
                writer,
                (event, pw) -> {},
                new HashMap<Object, Object>(){{
                    put(TestClass1.class.getCanonicalName(), (IActionTwoArgs<IEvent, PrintWriter>)(event, pw) -> {
                        try {
                            System.out.println(Thread.currentThread().getName());
                            latch.await();
                        } catch (Exception ignore) {}
                    });
                    put(
                        TestClass2.class.getCanonicalName(),
                        (IActionTwoArgs<IEvent, PrintWriter>)(event, pw) -> System.out.println(
                                Thread.currentThread().getName()
                        )
                    );
                }}
        );
        Thread thread1 = new Thread(() -> {
            try {
                handler.handle(event1);
            } catch (Exception ignored) {}
        });
        thread1.start();
        Thread thread2 = new Thread(() -> {
            try {
                handler.handle(event2);
                latch.countDown();
            } catch (Exception ignored) {}
        });
        thread2.start();
        Thread.sleep(300);
        verify(event1, times(1)).getBody();
        verify(event2, times(1)).getBody();
        // The writer must be called only once
        assertEquals(1, counter.get());
    }

    @Test
    public void should_throw_specified_exception_with_event_on_invalid_processor_calling()
            throws Exception {
        IActionTwoArgs<IEvent, PrintWriter> testAction = mock(IActionTwoArgs.class);

        IEventHandler handler = new PrintToFileEventHandler(
                "test",
                (params) -> {
                    IEvent e = params.getQueue().poll();
                    throw new EventHandlerException(TestClass1.class.getCanonicalName(), e);
                },
                (e, writer) -> {},
                new HashMap<Object, Object>() {{put("test", testAction);}}
        );
        IEvent event = mock(IEvent.class);
        when(event.getBody()).thenReturn(new TestClass1());
        try {
            handler.handle(event);
        } catch (EventHandlerException e) {
            assertEquals(event, e.getEvent());
        }
    }

    @Test
    public void should_do_nothing_on_null_event()
            throws Exception {
        IEventHandler handler = new PrintToFileEventHandler(
                "test", (params) -> {}, (event, writer) -> {}
                );
        handler.handle(null);
    }

    @Test (expected = ExtendedEventHandlerException.class)
    public void should_throw_specified_exception_on_invalid_key()
            throws Exception {
        IEventHandler handler = new PrintToFileEventHandler(
                "test", (params) -> {}, (event, writer) -> {}
                );
        ((IExtendedEventHandler) handler).addProcessor(5, mock(IActionTwoArgs.class));
    }

    @Test (expected = ExtendedEventHandlerException.class)
    public void should_throw_specified_exception_on_invalid_processor()
            throws Exception {
        IEventHandler handler = new PrintToFileEventHandler(
                "test", (params) -> {}, (event, writer) -> {}
        );
        ((IExtendedEventHandler) handler).addProcessor("test", 5);
    }
}

class TestClass1 {
}

class TestClass2 {
}