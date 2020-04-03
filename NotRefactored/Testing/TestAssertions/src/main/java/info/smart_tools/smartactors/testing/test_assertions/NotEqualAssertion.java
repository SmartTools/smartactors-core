package info.smart_tools.smartactors.testing.test_assertions;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.testing.interfaces.iassertion.IAssertion;
import info.smart_tools.smartactors.testing.interfaces.iassertion.exception.AssertionFailureException;

import java.text.MessageFormat;

/**
 * Assertion verifying that value is not equal to a reference value.
 */
public class NotEqualAssertion implements IAssertion {
    private IFieldName referenceFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if resolution of any dependencies fails
     */
    public NotEqualAssertion()
            throws ResolutionException {
        referenceFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "to");
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
