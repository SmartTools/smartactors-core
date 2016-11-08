package info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 *
 */
public interface EnteringMessage {
    IMessageProcessor getProcessor() throws ReadValueException;
}
