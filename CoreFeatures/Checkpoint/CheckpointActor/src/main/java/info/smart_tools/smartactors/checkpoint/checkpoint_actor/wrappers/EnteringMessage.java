package info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 *
 */
public interface EnteringMessage {
    /**
     * Get message processor associated with this message.
     *
     * @return the message processor
     * @throws ReadValueException if error occurs reading the value
     */
    IMessageProcessor getProcessor() throws ReadValueException;

    /**
     * Get the message itself.
     *
     * @return the message processor
     * @throws ReadValueException if error occurs reading the value
     */
    IObject getMessage() throws ReadValueException;

    /**
     * Get identifier of the checkpoint.
     *
     * @return the identifier of checkpoint
     * @throws ReadValueException if error occurs reading the value
     */
    String getCheckpointId() throws ReadValueException;

    /**
     * Get configuration of scheduling strategy to use to re-send the message.
     * @return configuration of scheduling strategy
     * @throws ReadValueException if error occurs reading the value
     */
    IObject getSchedulingConfiguration() throws ReadValueException;

    /**
     * Get configuration of recover strategy.
     *
     * @return configuration of recover strategy
     * @throws ReadValueException if error occurs reading the value
     */
    IObject getRecoverConfiguration() throws ReadValueException;

    /**
     * Get object containing identifier of checkpoint responsible for this message and identifier of entry associated with this message.
     *
     * @return checkpoint status object
     * @throws ReadValueException if error occurs reading the value
     */
    IObject getCheckpointStatus() throws ReadValueException;

    /**
     * Set checkpoint status object.
     *
     * @param checkpointStatus new status object
     * @throws ChangeValueException if error occurs changing the value
     * @see #getCheckpointStatus()
     */
    void setCheckpointStatus(IObject checkpointStatus) throws ChangeValueException;
}
