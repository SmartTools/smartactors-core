package info.smart_tools.smartactors.event_handler_with_logging_to_file.event_handler_with_logging_to_file;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.event_handler.event_handler.IEvent;
import info.smart_tools.smartactors.event_handler.event_handler.IEventHandler;
import info.smart_tools.smartactors.event_handler.event_handler.IExtendedEventHandler;
import info.smart_tools.smartactors.event_handler.event_handler.exception.EventHandlerException;
import info.smart_tools.smartactors.event_handler.event_handler.exception.ExtendedEventHandlerException;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of {@link IEventHandler} which output data of {@link IEvent} to the file.
 */
public final class PrintToFileEventHandler implements IExtendedEventHandler {

    private String eventHandlerKey;
    private Queue<IEvent> queue = new ConcurrentLinkedQueue<>();
    private ReentrantLock writeLock = new ReentrantLock();
    private static final String FILENAME = "server.log";
    private IActionTwoArgs<IEvent, PrintWriter> defaultProcessor;
    private Map<String, IActionTwoArgs<IEvent, PrintWriter>> processors = new HashMap<>();

    // Default writer
    private IAction<PrintToFileWriterParameters> writer = (params) -> {
        IEvent event = null;
        try (PrintWriter writer = new PrintWriter(new FileWriter(params.getFileName(), true))) {
            while (!params.getQueue().isEmpty()) {
                event = params.getQueue().poll();
                String eventType = event.getBody().getClass().getCanonicalName();
                IActionTwoArgs<IEvent, PrintWriter> processor = params
                        .getProcessors()
                        .getOrDefault(eventType, params.getDefaultProcessor());
                processor.execute(event, writer);
            }
        } catch (Exception e) {
            throw new EventHandlerException(
                    "PrintToFileEventHandler: Could not execute action of file writer .", e, event
            );
        }
    };

    /**
     * The constructor
     * @param eventHandlerKey the key of created instance of {@link PrintToFileEventHandler}
     * @param writer the action for writing event data
     * @param defaultProcessor the default processor for event processing
     */
    public PrintToFileEventHandler(
            final String eventHandlerKey,
            final IAction<PrintToFileWriterParameters> writer,
            final IActionTwoArgs<IEvent, PrintWriter> defaultProcessor
    ) {
        this.eventHandlerKey = eventHandlerKey;
        if (null != writer) {
            this.writer = writer;
        }
        this.defaultProcessor = defaultProcessor;
    }

    /**
     * The constructor
     * @param eventHandlerKey the key of created instance of {@link PrintToFileEventHandler}
     * @param writer the action for writing event data
     * @param defaultProcessor the default processor for event processing
     * @param processors initialization map of processors
     */
    public PrintToFileEventHandler(
            final String eventHandlerKey,
            final IAction<PrintToFileWriterParameters> writer,
            final IActionTwoArgs<IEvent, PrintWriter> defaultProcessor,
            final Map<Object, Object> processors
    ) {
        this.eventHandlerKey = eventHandlerKey;
        if (null != writer) {
            this.writer = writer;
        }
        this.defaultProcessor = defaultProcessor;

        processors.forEach((type, processor) -> {
            try {
                this.addProcessor(type, processor);
            } catch (Exception e) {
                throw new RuntimeException(
                        "PrintToFileEventHandler: One of the processors cannot be casted to a specified type", e
                );
            }
        });
    }

    @Override
    public void handle(final IEvent event)
            throws EventHandlerException {
        if (event != null) {
            this.queue.offer(event);
        }
        if (writeLock.tryLock()) {
            if (writeLock.isHeldByCurrentThread()) {
                try {
                    writeToFile();
                } catch (EventHandlerException e) {
                    throw e;
                } catch (Exception e) {
                    throw new EventHandlerException(
                            "PrintToFileEventHandler: Error on saving data into a log file.", e
                    );
                } finally {
                    writeLock.unlock();
                }
            }
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
        IActionTwoArgs<IEvent, PrintWriter> castedProcessor = castProcessor(processor);

        processors.put(castedEventKey, castedProcessor);
    }

    @Override
    public Object removeProcessor(final Object eventKey)
            throws ExtendedEventHandlerException {
        String castedEventKey = castEventKey(eventKey);

        return processors.remove(castedEventKey);
    }

    private void writeToFile()
            throws ActionExecutionException, InvalidArgumentException {
        this.writer.execute(
                new PrintToFileWriterParameters(this.queue, this.processors, this.defaultProcessor, FILENAME)
        );
    }

    private String castEventKey(final Object eventKey)
            throws ExtendedEventHandlerException {
        try {
            return  (String) eventKey;
        } catch (Exception e) {
            throw new ExtendedEventHandlerException(
                    "PrintToFileEventHandler: Could not cast event type to String.", e
            );
        }
    }

    @SuppressWarnings("unchecked")
    private IActionTwoArgs<IEvent, PrintWriter> castProcessor(final Object processor)
            throws ExtendedEventHandlerException {
        try {
            return  (IActionTwoArgs<IEvent, PrintWriter>) processor;
        } catch (Exception e) {
            throw new ExtendedEventHandlerException(
                    "PrintToFileEventHandler: Could not cast processor to IActionTwoArgs<IEvent, PrintWriter>.", e
            );
        }
    }
}
