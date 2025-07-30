package info.smart_tools.smartactors.message_processing.wrapper_generator;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;

/**
 * Test incorrect mapping in target
 */
public interface IIncorrectMappingInTarget {

    void setValue(Integer value)
            throws ChangeValueException;
}
