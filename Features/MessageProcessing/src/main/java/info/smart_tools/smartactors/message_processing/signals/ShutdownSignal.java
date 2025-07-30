package info.smart_tools.smartactors.message_processing.signals;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.Signal;

/**
 * Signal sent to message processors when the server is shutting down.
 */
public class ShutdownSignal extends Signal {
    /**
     * The constructor.
     */
    public ShutdownSignal() {
        super("shutdown");
    }
}
