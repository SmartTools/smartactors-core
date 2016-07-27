package info.smart_tools.smartactors.core.receiver_generator;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

public interface ICustomWrapper {

    Integer getIntValue() throws ReadValueException;

    void setIntValue(Integer i) throws ChangeValueException;
}
