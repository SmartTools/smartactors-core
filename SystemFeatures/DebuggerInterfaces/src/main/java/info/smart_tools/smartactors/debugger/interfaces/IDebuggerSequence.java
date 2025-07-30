package info.smart_tools.smartactors.debugger.interfaces;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;

/**
 * {@link IMessageProcessingSequence Message processing sequence} used by a debugger to substitute debugger calls between real sequence
 * steps.
 */
public interface IDebuggerSequence extends IMessageProcessingSequence {
    /**
     * @return {@code true} if a exception is thrown by previous receiver
     */
    boolean isExceptionOccurred();

    /**
     * @return {@code true} if a sequence is completed and it just returned debugger as the target last time
     */
    boolean isCompleted();

    /**
     * Stop processing of the message. Subsequent calls to {@link IMessageProcessingSequence#next()} will always return {@code false}.
     */
    void stop();

    /**
     * @return the exception thrown by previous receiver or {@code null} if {@link #isExceptionOccurred()} is {@code false}
     */
    Throwable getException();

    /**
     * Process the exception thrown by previous receiver.
     *
     * @return {@code true} if exception is processed successful
     */
    // TODO: Exception when there is no exception occurred.
    boolean processException();

    /**
     * Get real sequence.
     *
     * @return the real sequence
     */
    IMessageProcessingSequence getRealSequence();
}
