package info.smart_tools.smartactors.core.chain_testing.checkers;

import info.smart_tools.smartactors.core.chain_testing.exceptions.AssertionFailureException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.InvalidTestDescriptionException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.List;

/**
 * Base class for objects checking correctness of test message processing result.
 */
public abstract class TestResultChecker {
    /**
     * Check if the test was completed as expected.
     *
     * @param mp the message processor processed the test message
     * @param exc the exception occurred processing message or {@code null} if chain completed successful
     * @throws AssertionFailureException if test is completed not as expected
     */
    public abstract void check(final IMessageProcessor mp, final Throwable exc) throws AssertionFailureException;

    /**
     * Get arguments object that should be associated with the step of {@link info.smart_tools.smartactors.core.chain_testing.MainTestChain}
     * that will be reached in case of successful completion of chain.
     *
     * @return the arguments object
     */
    public abstract IObject getSuccessfulReceiverArguments();

    /**
     * Creates a instance of {@link TestResultChecker}.
     *
     * @param description test description
     * @return instance of required subclass of {@link TestResultChecker}
     * @throws TestStartupException if any error occurs
     * @throws InvalidTestDescriptionException if description contains both "intercept" and "assert" sections
     * @throws InvalidTestDescriptionException if description contains no "intercept" and no "assert" section
     */
    public static TestResultChecker createChecker(final IObject description)
            throws TestStartupException, InvalidTestDescriptionException {
        try {
            IFieldName interceptFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "intercept");
            IFieldName assertFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "assert");

            List<IObject> assertions = (List<IObject>) description.getValue(assertFieldName);
            IObject intercept = (IObject) description.getValue(interceptFieldName);

            if ((null == assertions) && (null == intercept)) {
                throw new InvalidTestDescriptionException("None of \"assert\" and \"intercept\" sections are present in test description.");
            }

            if ((null != assertions) && (null != intercept)) {
                throw new InvalidTestDescriptionException("Both \"assert\" and \"intercept\" sections are present in test description.");
            }

            if (null != assertions) {
                return new AssertionChecker(assertions);
            } else {
                return new ExceptionInterceptor(intercept);
            }
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new TestStartupException("Could not create checker instance.", e);
        }
    }
}
