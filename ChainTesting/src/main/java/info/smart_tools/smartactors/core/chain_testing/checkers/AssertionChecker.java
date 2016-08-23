package info.smart_tools.smartactors.core.chain_testing.checkers;

import info.smart_tools.smartactors.core.chain_testing.Assertion;
import info.smart_tools.smartactors.core.chain_testing.exceptions.AssertionFailureException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link TestResultChecker} that verifies assertions when test execution is completed.
 */
public class AssertionChecker extends TestResultChecker {
    /**
     * Object representing assertion that is already resolved and will be checked when chain execution completes.
     */
    private class PreparedAssertion {
        private String name;
        private Assertion assertion;
        private IFieldName fieldName;
        private IObject description;

        PreparedAssertion(final String name, final Assertion assertion, final IFieldName fieldName, final IObject description) {
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
     * @throws TestStartupException if any error occurs initializing checker
     */
    public AssertionChecker(final List<IObject> description)
            throws TestStartupException {
        try {
            preparedSuccessReceiverArguments = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
            preparedSuccessReceiverWrapperConfig = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

            IFieldName wrapperFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "wrapper");
            preparedSuccessReceiverArguments.setValue(wrapperFieldName, preparedSuccessReceiverWrapperConfig);

            prepareAssertions(description);
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
            throw new TestStartupException(e);
        }
    }

    @Override
    public void check(final IMessageProcessor mp, final Throwable exc) throws AssertionFailureException {
        if (exc != null) {
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
            throws TestStartupException {
        try {
            IFieldName assertNameFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "name");
            IFieldName assertTypeFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "type");
            IFieldName assertValueFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "value");

            for (IObject assertion : descriptions) {
                String name = (String) assertion.getValue(assertNameFieldName);
                String type = (String) assertion.getValue(assertTypeFieldName);
                IFieldName getterFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "in_" + name);

                try {
                    Assertion assertion1 = IOC.resolve(Keys.getOrAdd("assertion of type " + type));

                    preparedSuccessReceiverWrapperConfig.setValue(getterFieldName, assertion.getValue(assertValueFieldName));

                    preparedAssertions.add(new PreparedAssertion(name, assertion1, getterFieldName, assertion));
                } catch (ResolutionException e) {
                    throw new TestStartupException(
                            MessageFormat.format("Could not resolve assertion \"{0}\" of type \"{1}\".", name, type), e);
                }
            }
        } catch (ResolutionException | ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new TestStartupException(e);
        }
    }
}
