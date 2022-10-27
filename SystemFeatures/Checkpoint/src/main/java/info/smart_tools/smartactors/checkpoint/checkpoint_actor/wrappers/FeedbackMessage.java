package info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 *
 */
public interface FeedbackMessage {
    /**
     * Get identifier of checkpoint responsible for the message (the one after the checkpoint received this message).
     *
     * @return identifier of checkpoint responsible for the message
     * @throws ReadValueException if error occurs reading the value
     */
    String getResponsibleCheckpointId() throws ReadValueException;

    /**
     * Get identifier of the entry of checkpoint currently responsible for the message.
     *
     * @return identifier of the entry of checkpoint currently responsible for the message.
     * @throws ReadValueException if error occurs reading the value
     */
    String getCheckpointEntryId() throws ReadValueException;

    /**
     * Get identifier of checkpoint previously responsible for the message (the checkpoint that should receive this message).
     *
     * @return identifier of checkpoint previously responsible for the message
     * @throws ReadValueException if error occurs reading the value
     */
    String getPrevCheckpointId() throws ReadValueException;

    /**
     * Get identifier of the entry of checkpoint previously responsible for the message.
     *
     * @return identifier of the entry of checkpoint previously responsible for the message
     * @throws ReadValueException if error occurs reading the value
     */
    String getPrevCheckpointEntryId() throws ReadValueException;
}
