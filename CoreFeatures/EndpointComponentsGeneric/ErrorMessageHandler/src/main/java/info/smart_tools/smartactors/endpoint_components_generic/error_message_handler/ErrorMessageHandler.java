package info.smart_tools.smartactors.endpoint_components_generic.error_message_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link IMessageHandler Message handler} that always throws a exception with configured message.
 *
 * @param <TCtx>
 * @param <TNextCtx>
 */
public class ErrorMessageHandler<TCtx extends IMessageContext, TNextCtx extends IMessageContext>
        implements IMessageHandler<TCtx, TNextCtx> {
    private MessageHandlerException checkedException;
    private RuntimeException uncheckedException;

    /**
     * The constructor.
     *
     * @param clz     exception class
     * @param message exception message
     * @throws InvalidArgumentException if given class does not extend acceptable exception classes or it's instance
     *                                  cannot be constructed
     */
    public ErrorMessageHandler(final Class<? extends Throwable> clz, final String message)
            throws InvalidArgumentException {
        try {
            Throwable e = clz.getConstructor(String.class).newInstance(message);

            try {
                checkedException = (MessageHandlerException) e;
            } catch (ClassCastException cce) {
                uncheckedException = (RuntimeException) e;
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new InvalidArgumentException("Exception class '" + clz.getCanonicalName() +
                    "' has no accessible constructor accepting string parameter.", e);
        } catch (InstantiationException e) {
            throw new InvalidArgumentException("Exception class is abstract.", e);
        } catch (InvocationTargetException e) {
            throw new InvalidArgumentException("Exception constructor has thrown exception.", e);
        } catch (ClassCastException e) {
            throw new InvalidArgumentException("Exception class does not extend MessageHandlerException or RuntimeException.", e);
        }
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<TNextCtx> next,
        final TCtx context)
            throws MessageHandlerException {
        if (null != checkedException) {
            throw checkedException;
        } else {
            throw uncheckedException;
        }
    }
}
