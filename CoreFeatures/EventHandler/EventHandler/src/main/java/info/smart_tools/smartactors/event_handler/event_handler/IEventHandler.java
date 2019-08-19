package info.smart_tools.smartactors.event_handler.event_handler;

import info.smart_tools.smartactors.event_handler.event_handler.exception.EventHandlerException;

/**
 * The interface for implementing handlers for processing any events like a messages, exceptions, etc.
 */
public interface IEventHandler {

    /**
     * starts to process an instance of {@link IEvent}
     * @param event the event which should be processed
     * @throws EventHandlerException should be thrown if any errors were occurring
     */
    void handle(IEvent event) throws EventHandlerException;

    /**
     * returns the key of current instance of {@link IEventHandler}
     * @return the key of current instance of {@link IEventHandler}
     */
    String getEventHandlerKey();
}
