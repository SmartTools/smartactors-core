package info.smart_tools.smartactors.event_handler_with_logging_to_file.event_handler_with_logging_to_file;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionTwoArgs;
import info.smart_tools.smartactors.event_handler.event_handler.IEvent;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Queue;

/**
 * Support class for initialization writer of {@link PrintToFileEventHandler}
 */
final class PrintToFileWriterParameters {

    private Queue<IEvent> queue;
    private Map<String, IActionTwoArgs<IEvent, PrintWriter>> processors;
    private IActionTwoArgs<IEvent, PrintWriter> defaultProcessor;
    private String fileName;

    /**
     * Default constructor
     */
    private PrintToFileWriterParameters() {
    }

    /**
     * The constructor
     * @param queue the queue
     * @param processors the list of processors
     * @param defaultProcessor the default processor
     * @param fileName the name of file
     * @throws InvalidArgumentException if incoming arguments are incorrect
     */
    PrintToFileWriterParameters(
            final Queue<IEvent> queue,
            final Map<String, IActionTwoArgs<IEvent, PrintWriter>> processors,
            final IActionTwoArgs<IEvent, PrintWriter> defaultProcessor,
            final String fileName
    ) throws InvalidArgumentException {
        if (null == queue || null == processors || null == defaultProcessor || null == fileName) {
            throw new InvalidArgumentException("All arguments should be not null.");
        }
        this.queue = queue;
        this.processors = processors;
        this.defaultProcessor = defaultProcessor;
        this.fileName = fileName;
    }

    Queue<IEvent> getQueue() {
        return queue;
    }

    Map<String, IActionTwoArgs<IEvent, PrintWriter>> getProcessors() {
        return processors;
    }

    IActionTwoArgs<IEvent, PrintWriter> getDefaultProcessor() {
        return defaultProcessor;
    }

    String getFileName() {
        return fileName;
    }
}
