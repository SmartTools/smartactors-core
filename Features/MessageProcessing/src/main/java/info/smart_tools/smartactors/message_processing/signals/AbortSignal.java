package info.smart_tools.smartactors.message_processing.signals;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.Signal;

/**
 * Signal sent to message processor when it's operation should be interrupted immediately.
 *
 * Of course there may be defined a handler chain to ignore even this signal as some operations should not be ever interrupted.
 */
public class AbortSignal extends Signal {
    /**
     * The constructor.
     */
    public AbortSignal() {
        super("abort");
    }
}
