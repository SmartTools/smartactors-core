package info.smart_tools.smartactors.event_handler.event_handler;

/**
 * This class contains method {@link EventHandler#handle(IEvent)}
 * for processing any events.
 * Also this class contains method {@link EventHandler#getEventHandlerContainer()} to provide an inner container of handlers.
 * This container may be extended by any custom handlers.
 */
public final class EventHandler {

    /**
     * Implementation of {@link IEventHandlerContainer}.
     * Will be initialized by default implementation of {@link EventHandlerContainer}
     * ReInitialization possible only with using java reflection API
     * Example:
     * <pre>
     * {@code
     * Field field = EventHandler.class.getDeclaredField("container");
     * field.setAccessible(true);
     * field.set(null, new Object());
     * field.setAccessible(false);
     * }
     * </pre>
     */
    private static IEventHandlerContainer container;

    static {
        container = new EventHandlerContainer();
    }

    /**
     * Default private constructor
     */
    private EventHandler(){
    }

    /**
     * starts to execute event handlers one after another until the execution of the next handler is successful
     * @param event the event which should be processed
     */
    public static void handle(final IEvent event) {
        container.handle(event);
    }

    /**
     * returns the inner container of event handlers
     * @return the inner instance of {@link IEventHandlerContainer}
     */
    public static IEventHandlerContainer getEventHandlerContainer() {

        return container;
    }
}
