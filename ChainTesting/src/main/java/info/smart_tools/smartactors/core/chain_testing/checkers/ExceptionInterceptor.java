package info.smart_tools.smartactors.core.chain_testing.checkers;

import info.smart_tools.smartactors.core.chain_testing.exceptions.AssertionFailureException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.text.MessageFormat;

/**
 * {@link TestResultChecker} that expects some exception to be thrown by a chain.
 */
public class ExceptionInterceptor extends TestResultChecker {
    private Class<?> expectedExceptionClass;
    private IMessageReceiver expectedReceiver;

    /**
     * The constructor.
     *
     * @param description "intercept" section of test description
     * @throws TestStartupException if any error occurs initializing interceptor
     */
    public ExceptionInterceptor(final IObject description)
            throws TestStartupException {
        String expectedExceptionClassName = null;

        try {
            Object expectedReceiverId = IOC.resolve(Keys.getOrAdd("receiver_id_from_iobject"), description);
            IRouter router = IOC.resolve(Keys.getOrAdd(IRouter.class.getCanonicalName()));

            expectedReceiver = router.route(expectedReceiverId);

            IFieldName exceptionClassFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "class");
            expectedExceptionClassName = String.valueOf(description.getValue(exceptionClassFieldName));

            expectedExceptionClass = getClass().getClassLoader().loadClass(expectedExceptionClassName);
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new TestStartupException(e);
        } catch (RouteNotFoundException e) {
            throw new TestStartupException("Receiver expected to throw exception is not found.");
        } catch (ClassNotFoundException e) {
            throw new TestStartupException(
                    MessageFormat.format("Expected exception class ({0}) is not a class.", expectedExceptionClassName));
        }
    }

    @Override
    public void check(final IMessageProcessor mp, final Throwable exc)
            throws AssertionFailureException {
        if (null == exc) {
            throw new AssertionFailureException(
                    MessageFormat.format("Exception of class '{0}' was expected to be thrown but the chain completed without exception.",
                            expectedExceptionClass.getCanonicalName()));
        }

        checkExceptionClass(exc);
        checkExceptionLocation(mp);
    }

    @Override
    public IObject getSuccessfulReceiverArguments() {
        return null;
    }

    private void checkExceptionClass(final Throwable e)
            throws AssertionFailureException {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (expectedExceptionClass.isInstance(t)) {
                return;
            }
        }

        throw new AssertionFailureException(
                MessageFormat.format("Exception of class {0} was expected but another exception was thrown.",
                        expectedExceptionClass.getCanonicalName()), e);
    }

    private void checkExceptionLocation(final IMessageProcessor mp)
            throws AssertionFailureException {
        if (expectedReceiver == mp.getSequence().getCurrentReceiver()) {
            return;
        }

        Object receiverId;

        try {
            receiverId = IOC.resolve(Keys.getOrAdd("receiver_id_from_iobject"), mp.getSequence().getCurrentReceiverArguments());
        } catch (ResolutionException e) {
            receiverId = "<cannot resolve id>";
        }

        throw new AssertionFailureException(
                MessageFormat.format("Exception was thrown at unexpected position (at '{0}').",
                        String.valueOf(receiverId)));
    }
}
