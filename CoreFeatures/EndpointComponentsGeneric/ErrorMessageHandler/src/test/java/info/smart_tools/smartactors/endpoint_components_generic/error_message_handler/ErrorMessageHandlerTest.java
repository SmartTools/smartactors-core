package info.smart_tools.smartactors.endpoint_components_generic.error_message_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link ErrorMessageHandler}.
 */
public class ErrorMessageHandlerTest {
    private IMessageHandlerCallback callback;
    private IMessageContext context;
    private ErrorMessageHandler handler;

    @Before public void setUp() {
        callback = mock(IMessageHandlerCallback.class);
        context = mock(IMessageContext.class);
        handler = null;
    }

    @Test public void Should_throwUncheckedExceptionWithConfiguredMessage() throws Exception {
        handler = new ErrorMessageHandler(IllegalStateException.class, "foobarerror");

        try {
            handler.handle(callback, context);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("foobarerror"));
        }
    }

    @Test public void Should_throwCheckedExceptionWithConfiguredMessage() throws Exception {
        handler = new ErrorMessageHandler(MessageHandlerException.class, "foobarerror");

        try {
            handler.handle(callback, context);
            fail();
        } catch (MessageHandlerException e) {
            assertTrue(e.getMessage().contains("foobarerror"));
        }
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenExceptionClassDoesNotExtentAllowedClasses() throws Exception {
        new ErrorMessageHandler<>(IOException.class, "");
    }

    public static abstract class TheException0 extends MessageHandlerException {
        public TheException0(String message) {super(message);}
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenExceptionClassIsAbstract() throws Exception {
        new ErrorMessageHandler<>(TheException0.class, "");
    }

    public static class TheException1 extends MessageHandlerException {}

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenExceptionClassHasNoMatchingConstructor() throws Exception {
        new ErrorMessageHandler<>(TheException1.class, "");
    }

    public static class TheException2 extends MessageHandlerException {
        public TheException2(String msg) {throw new RuntimeException();}
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenExceptionClassConstructorThrows() throws Exception {
        new ErrorMessageHandler<>(TheException2.class, "");
    }
}
