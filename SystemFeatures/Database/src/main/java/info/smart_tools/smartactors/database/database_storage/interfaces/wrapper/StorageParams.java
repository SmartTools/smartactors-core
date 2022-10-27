package info.smart_tools.smartactors.database.database_storage.interfaces.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public interface StorageParams {
    String getDriver() throws ReadValueException, ChangeValueException;
}
