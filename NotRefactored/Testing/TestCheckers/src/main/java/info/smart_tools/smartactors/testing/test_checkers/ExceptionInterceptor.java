package info.smart_tools.smartactors.testing.test_checkers;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.testing.interfaces.iassertion.exception.AssertionFailureException;
import info.smart_tools.smartactors.testing.interfaces.iresult_checker.IResultChecker;

import java.text.MessageFormat;

/**
 * {@link IResultChecker} that expects some exception to be thrown by a chain.
 */
public class ExceptionInterceptor implements IResultChecker {
    private Class<?> expectedExceptionClass;
    private IMessageReceiver expectedReceiver;

    /**
     * The constructor.
     *
     * @param description "intercept" section of test description
     * @throws InitializationException if any error occurs initializing interceptor
     */
    public ExceptionInterceptor(final IObject description)
            throws InitializationException {
        String expectedExceptionClassName = null;

        try {
            Object expectedReceiverId = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "receiver_id_from_iobject"), description
            );
            IRouter router = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IRouter.class.getCanonicalName())
            );

            expectedReceiver = router.route(expectedReceiverId);

            IFieldName exceptionClassFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "class"
            );
            expectedExceptionClassName = String.valueOf(description.getValue(exceptionClassFieldName));

            expectedExceptionClass = getClass().getClassLoader().loadClass(expectedExceptionClassName);
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new InitializationException(e);
        } catch (RouteNotFoundException e) {
            throw new InitializationException("Receiver expected to throw exception is not found.");
        } catch (ClassNotFoundException e) {
            throw new InitializationException(
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
            receiverId = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "receiver_id_from_iobject"), mp.getSequence().getCurrentReceiverArguments()
            );
        } catch (ResolutionException e) {
            receiverId = "<cannot resolve id>";
        }

        throw new AssertionFailureException(
                MessageFormat.format("Exception was thrown at unexpected position (at '{0}').",
                        String.valueOf(receiverId)));
    }
}
