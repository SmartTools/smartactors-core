package info.smart_tools.smartactors.event_handler.event_handler;

import info.smart_tools.smartactors.event_handler.event_handler.exception.ExtendedEventHandlerException;

/**
 * The interface for extending instances of {@link IEventHandler}. An implementation of this
 * interface provide an ability to add and remove any actions to processing specific types of
 * {@link IEvent}.
 */
public interface IExtendedEventHandler extends IEventHandler {

    /**
     * registers given processor with given event key to the instance of {@link EventHandler}
     * @param eventKey the event key
     * @param processor the processor
     * @throws ExtendedEventHandlerException should be thrown if any errors were occurring
     */
    void addProcessor(Object eventKey, Object processor) throws ExtendedEventHandlerException;

    /**
     * unregisters an executor by given event key from the instance of {@link EventHandler}
     * @param eventKey the event key
     * @return unregistered processor
     * @throws ExtendedEventHandlerException should be thrown if any errors were occurring
     */
    Object removeProcessor(Object eventKey) throws ExtendedEventHandlerException;
}
