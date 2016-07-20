package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;

/**
 * Test incorrect mapping in target
 */
public interface IIncorrectMappingInTarget {

    void setValue(Integer value)
            throws ChangeValueException;
}
