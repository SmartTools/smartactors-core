package info.smart_tools.smartactors.event_handler.event_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.event_handler.event_handler.exception.EventHandlerException;
import info.smart_tools.smartactors.event_handler.event_handler.exception.ExtendedEventHandlerException;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link IEventHandler} which output data of {@link IEvent} to system console.
 */
public final class PrintToConsoleEventHandler implements IExtendedEventHandler {

    private String eventHandlerKey;
    private IAction<IEvent> defaultProcessor;
    private Map<String, IAction<IEvent>> processors = new HashMap<>();

    /**
     * The constructor
     * @param eventHandlerKey the key of created instance of {@link PrintToConsoleEventHandler}
     * @param defaultProcessor the default processor for event processing
     */
    public PrintToConsoleEventHandler(final String eventHandlerKey, final IAction<IEvent> defaultProcessor) {
        this.eventHandlerKey = eventHandlerKey;
        this.defaultProcessor = defaultProcessor;
    }

    /**
     * The constructor
     * @param eventHandlerKey the key of created instance of {@link PrintToConsoleEventHandler}
     * @param defaultProcessor the default processor for event processing
     * @param processors initialization map of processors
     */
    public PrintToConsoleEventHandler(
            final String eventHandlerKey,
            final IAction<IEvent> defaultProcessor,
            final Map<Object, Object> processors
            ) {
        this.eventHandlerKey = eventHandlerKey;
        this.defaultProcessor = defaultProcessor;

        processors.forEach((type, processor) -> {
            try {
                this.addProcessor(type, processor);
            } catch (Exception e) {
                throw new RuntimeException(
                        "PrintToConsoleEventHandler: One of the processors cannot be casted to a specified type", e
                );
            }
        });
    }


    @Override
    public void handle(final IEvent event)
            throws EventHandlerException {
        if (event == null) {
            return;
        }
        String type = event.getBody().getClass().getCanonicalName();
        IAction<IEvent> processor = processors.getOrDefault(type, defaultProcessor);
        try {
            processor.execute(event);
        } catch (Exception e) {
            throw new EventHandlerException(
                    String.format("PrintToConsoleEventHandler: Event handler action '%s' throws exception.", type),
                    e
            );
        }
    }

    @Override
    public String getEventHandlerKey() {

        return this.eventHandlerKey;
    }

    @Override
    public void addProcessor(final Object eventKey, final Object processor)
            throws ExtendedEventHandlerException {
        String castedEventKey = castEventKey(eventKey);
        IAction<IEvent> castedProcessor = castProcessor(processor);

        processors.put(castedEventKey, castedProcessor);
    }

    @Override
    public Object removeProcessor(final Object eventKey)
            throws ExtendedEventHandlerException {
        String castedEventKey = castEventKey(eventKey);

        return processors.remove(castedEventKey);
    }

    private String castEventKey(final Object eventKey)
            throws ExtendedEventHandlerException {
        try {
            return  (String) eventKey;
        } catch (Exception e) {
            throw new ExtendedEventHandlerException(
                    "PrintToConsoleEventHandler: Could not cast event type to String.", e
            );
        }
    }

    @SuppressWarnings("unchecked")
    private IAction<IEvent> castProcessor(final Object processor)
            throws ExtendedEventHandlerException {
        try {
            return  (IAction<IEvent>) processor;
        } catch (Exception e) {
            throw new ExtendedEventHandlerException(
                    "PrintToConsoleEventHandler: Could not cast processor to IAction<IEvent>.", e
            );
        }
    }
}
