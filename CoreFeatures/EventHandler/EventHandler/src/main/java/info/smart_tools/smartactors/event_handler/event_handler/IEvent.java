package info.smart_tools.smartactors.event_handler.event_handler;

/**
 * The interface for wrapping any system events(messages, exceptions, etc) to a uniform form
 */
public interface IEvent {

    /**
     * return the event in original form
     * @return the original event
     */
    Object getBody();

    /**
     * returns parameters which can be useful for event processing
     * @return the original parameters
     */
    Object getParameters();
}
