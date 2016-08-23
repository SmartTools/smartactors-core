package info.smart_tools.smartactors.core.chain_testing.assertions;

import info.smart_tools.smartactors.core.chain_testing.Assertion;
import info.smart_tools.smartactors.core.chain_testing.exceptions.AssertionFailureException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.text.MessageFormat;

/**
 * Assertion verifying that value is not equal to a reference value.
 */
public class NotEqualAssertion implements Assertion {
    private IFieldName referenceFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if resolution of any dependencies fails
     */
    public NotEqualAssertion()
            throws ResolutionException {
        referenceFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "to");
    }

    @Override
    public void check(final IObject description, final Object value)
            throws AssertionFailureException {
        try {
            Object reference = description.getValue(referenceFieldName);

            if (reference != null && value != null && !reference.equals(value) || (reference == null && value == null)) {
                return;
            }

            throw new AssertionFailureException(MessageFormat.format(
                    "Value ({0}){1} is not equal to expected value ({2}){3}.",
                    value == null ? "<null>" : value.getClass().getName(), value,
                    reference == null ? "<null>" : reference.getClass().getName(), reference));
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new AssertionFailureException("Could not read reference value.", e);
        }
    }
}
