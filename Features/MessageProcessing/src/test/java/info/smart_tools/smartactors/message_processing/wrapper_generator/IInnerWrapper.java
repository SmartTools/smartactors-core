package info.smart_tools.smartactors.message_processing.wrapper_generator;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Test interface for generation wrapper
 */
public interface IInnerWrapper {

    Double getDoubleValue()
            throws ReadValueException;

    void setDoubleValue(Double doubleValue)
            throws ChangeValueException;
}
