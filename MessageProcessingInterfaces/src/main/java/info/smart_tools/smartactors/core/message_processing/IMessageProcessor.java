package info.smart_tools.smartactors.core.message_processing;

import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Interface for a object responding for processing of a single message.
 */
public interface IMessageProcessor {
    /**
     * Returns the message object.
     *
     * @return the message being processed
     */
    IObject getMessage();

    /**
     * Returns the context object of the message being processed. Context contains all the non-serializable data related
     * to the message.
     *
     * @return the context of message processing
     */
    IObject getContext();

    /**
     * Returns the response object being formed for the processed message. The response object will be sent to the
     * sender of the message being processed (and may be serialized in process of sending) so should contain only
     * serializable data.
     *
     * @return the response for the message
     */
    IObject getResponse();

    /**
     * Returns the {@link IMessageProcessingSequence} managing order of receivers receiving this message.
     *
     * @return the sequence of receivers that should receive the message
     */
    IMessageProcessingSequence getSequence();

    /**
     * Start processing new message using this instance of {@link IMessageProcessor}.
     *
     * @param message    the message to process
     * @param context    the context of message processing
     * @throws InvalidArgumentException if message is {@code null}
     * @throws InvalidArgumentException if context is {@code null}
     * @throws ResolutionException if fails to resolve any dependency
     */
    void process(IObject message, IObject context)
            throws InvalidArgumentException, ResolutionException;
}
