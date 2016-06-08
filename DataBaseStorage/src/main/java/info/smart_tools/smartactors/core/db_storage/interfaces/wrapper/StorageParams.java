package info.smart_tools.smartactors.core.db_storage.interfaces.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

public interface StorageParams {
    String getDriver() throws ReadValueException, ChangeValueException;
}
