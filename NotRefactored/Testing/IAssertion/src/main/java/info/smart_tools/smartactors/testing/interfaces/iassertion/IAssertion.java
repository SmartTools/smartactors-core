package info.smart_tools.smartactors.testing.interfaces.iassertion;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.testing.interfaces.iassertion.exception.AssertionFailureException;

/**
 * Interface for the object checking
 */
@FunctionalInterface
public interface IAssertion {
    /**
     * Check the assertion.
     *
     * @param description    description of the single assertion
     * @param value          value to check
     * @throws AssertionFailureException if check is impossible because of any error
     * @throws AssertionFailureException if assertion failed
     */
    void check(IObject description, Object value)
            throws AssertionFailureException;
}
