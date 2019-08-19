package info.smart_tools.smartactors.event_handler.event_handler;

/**
 * The interface for extending instances of {@link IEventHandlerContainer}. An implementation of this
 * interface provide an ability to add and remove any instances of {@link IEventHandler}
 * to the {@link EventHandler} container.
 */
public interface IExtendedEventHandlerContainer extends IEventHandlerContainer {

    /**
     * registers given instance of {@link IEventHandler} to the {@link EventHandler} container.
     * @param eventHandler the instance of {@link IEventHandler}
     */
    void register(IEventHandler eventHandler);

    /**
     * unregisters previously registered instance of {@link IEventHandler} from
     * the {@link EventHandler} container by given event handler key
     * @param eventHandlerKey the event handler key
     * @return unregistered instance of {@link IEventHandler}
     */
    IEventHandler unregister(String eventHandlerKey);
}
