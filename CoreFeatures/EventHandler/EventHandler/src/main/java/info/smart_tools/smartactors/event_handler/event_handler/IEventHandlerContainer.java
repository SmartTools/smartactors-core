package info.smart_tools.smartactors.event_handler.event_handler;

/**
 * The interface for implementing {@link EventHandler} container.
 */
public interface IEventHandlerContainer {

    /**
     * starts to process instance of {@link IEvent} by calling chain of handlers
     * @param event the event which should be processed
     */
    void handle(IEvent event);
}
