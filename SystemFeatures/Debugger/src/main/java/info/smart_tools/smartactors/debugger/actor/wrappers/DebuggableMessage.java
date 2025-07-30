package info.smart_tools.smartactors.debugger.actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 * Wrapper for a message being debugged.
 */
public interface DebuggableMessage {
    /**
     * Get the message processor processing this message.
     *
     * @return the message processor.
     * @throws ReadValueException if any error occurs
     */
    IMessageProcessor getProcessor() throws ReadValueException;

    /**
     * Get the identifier of the debugging session.
     *
     * @return identifier of the session
     * @throws ReadValueException if any error occurs
     */
    String getSessionId() throws ReadValueException;
}
