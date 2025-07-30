package info.smart_tools.smartactors.message_processing_interfaces.message_processing;

/**
 * Base class for throwable's representing signals sent to message processor.
 */
public abstract class Signal extends Throwable {
    /**
     * The constructor.
     *
     * @param name    name of the signal
     */
    protected Signal(final String name) {
        super(name);
    }
}
