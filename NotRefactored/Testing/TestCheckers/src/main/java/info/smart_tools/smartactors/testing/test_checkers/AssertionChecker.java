package info.smart_tools.smartactors.testing.test_checkers;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.testing.interfaces.iassertion.IAssertion;
import info.smart_tools.smartactors.testing.interfaces.iassertion.exception.AssertionFailureException;
import info.smart_tools.smartactors.testing.interfaces.iresult_checker.IResultChecker;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@link IResultChecker} that verifies assertions when test execution is completed.
 */
public class AssertionChecker implements IResultChecker {
    /**
     * Object representing assertion that is already resolved and will be checked when chain execution completes.
     */
    private class PreparedAssertion {
        private String name;
        private IAssertion assertion;
        private IFieldName fieldName;
        private IObject description;

        PreparedAssertion(final String name, final IAssertion assertion, final IFieldName fieldName, final IObject description) {
            this.name = name;
            this.assertion = assertion;
            this.fieldName = fieldName;
            this.description = description;
        }

        void check(final IObject resultingEnvironment)
                throws AssertionFailureException {
            try {
                Object value = resultingEnvironment.getValue(fieldName);

                assertion.check(description, value);
            } catch (ReadValueException | InvalidArgumentException e) {
                throw new AssertionFailureException(
                        MessageFormat.format("Could not check assertion ''{0}'' because of error reading a value.", name), e);
            } catch (AssertionFailureException e) {
                throw new AssertionFailureException(
                        MessageFormat.format("Assertion '{0}' failed.", name), e);
            }
        }
    }

    private List<PreparedAssertion> preparedAssertions = new LinkedList<>();
    private IObject preparedSuccessReceiverArguments;
    private IObject preparedSuccessReceiverWrapperConfig;

    /**
     * The constructor.
     *
     * @param description    "assert" section of test description
     * @throws InitializationException if any error occurs initializing checker
     */
    public AssertionChecker(final List<IObject> description)
            throws InitializationException {
        try {
            preparedSuccessReceiverArguments = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "configuration object")
            );
            preparedSuccessReceiverWrapperConfig = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "configuration object")
            );

            IFieldName wrapperFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "wrapper"
            );
            preparedSuccessReceiverArguments.setValue(wrapperFieldName, preparedSuccessReceiverWrapperConfig);

            prepareAssertions(description);
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
            throw new InitializationException(e);
        }
    }

    @Override
    public void check(final IMessageProcessor mp, final Throwable exc)
            throws AssertionFailureException {
        if (null != exc) {
            throw new AssertionFailureException("Unexpected exception thrown by tested chain:", exc);
        }

        IObject env = mp.getEnvironment();

        for (PreparedAssertion pa : preparedAssertions) {
            pa.check(env);
        }
    }

    @Override
    public IObject getSuccessfulReceiverArguments() {
        return preparedSuccessReceiverArguments;
    }

    private void prepareAssertions(final List<IObject> descriptions)
            throws InitializationException {
        try {
            IFieldName assertNameFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name"
            );
            IFieldName assertTypeFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "type"
            );
            IFieldName assertValueFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "value"
            );

            for (IObject assertion : descriptions) {
                String name = (String) assertion.getValue(assertNameFieldName);
                String type = (String) assertion.getValue(assertTypeFieldName);
                IFieldName getterFieldName = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "in_" + name
                );

                try {
                    IAssertion assertion1 = IOC.resolve(
                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "assertion of type " + type)
                    );

                    preparedSuccessReceiverWrapperConfig.setValue(getterFieldName, assertion.getValue(assertValueFieldName));

                    preparedAssertions.add(new PreparedAssertion(name, assertion1, getterFieldName, assertion));
                } catch (ResolutionException e) {
                    throw new InitializationException(
                            MessageFormat.format("Could not resolve assertion \"{0}\" of type \"{1}\".", name, type), e);
                }
            }
        } catch (ResolutionException | ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new InitializationException(e);
        }
    }
}
