package info.smart_tools.smartactors.test.test_environment_handler;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.test.test_environment_handler.exception.AssertionFailureException;

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