package info.smart_tools.smartactors.testing.interfaces.iresult_checker;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.testing.interfaces.iassertion.exception.AssertionFailureException;

/**
 * Interface {@link IResultChecker}.
 * Implementation of this interface created for checking result of test.
 */
public interface IResultChecker {

    /**
     * Check if the test was completed as expected.
     *
     * @param mp the message processor processed the test message
     * @param exc the exception occurred processing message or {@code null} if chain completed successful
     * @throws AssertionFailureException if test is completed not as expected
     */
    void check(final IMessageProcessor mp, final Throwable exc)
            throws AssertionFailureException;

    /**
     * Get arguments object that should be associated with the step of
     * {@code info.smart_tools.smartactors.test.test_environment_handler.MainTestChain}
     * that will be reached in case of successful completion of chain.
     *
     * @return the arguments object
     */
    IObject getSuccessfulReceiverArguments();
}
