package info.smart_tools.smartactors.core.chain_testing;

import info.smart_tools.smartactors.core.chain_testing.exceptions.AssertionFailureException;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Interface for the object checking
 */
@FunctionalInterface
public interface Assertion {
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
