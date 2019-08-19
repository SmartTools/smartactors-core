package info.smart_tools.smartactors.event_handler.event_handler.exception;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.event_handler.event_handler.IEvent;

/**
 * The checked exception that should be thrown if execution of an implementation
 * of the method {@link info.smart_tools.smartactors.event_handler.event_handler.IEventHandler#handle(IEvent)}
 * will be unexpectedly terminated by any reason
 */
public class EventHandlerException extends ActionExecutionException {

    private IEvent event;

    /**
     * Constructor with a specific error message as the argument
     * @param message the specific error message
     */
    public EventHandlerException(final String message) {
        super(message);
    }

    /**
     * Constructor with a specific error message and a specific cause as arguments
     * @param message the specific error message
     * @param cause the specific cause
     */
    public EventHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a specific cause as the argument
     * @param cause the specific cause
     */
    public EventHandlerException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with a specific error message and an event as arguments
     * @param message the specific error message
     * @param event the event
     */
    public EventHandlerException(final String message, final IEvent event) {
        super(message);
        this.event = event;
    }

    /**
     * Constructor with a specific error message, cause and an event as arguments
     * @param message the specific error message
     * @param cause the specific cause
     * @param event the event
     */
    public EventHandlerException(final String message, final Throwable cause, final IEvent event) {
        super(message, cause);
        this.event = event;
    }

    /**
     * Constructor with a specific cause and an event as arguments
     * @param cause the specific cause
     * @param event the event
     */
    public EventHandlerException(final Throwable cause, final IEvent event) {
        super(cause);
        this.event = event;
    }

    public IEvent getEvent() {
        return event;
    }
}
