package info.smart_tools.smartactors.core.db_task.upsert.psql.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

public interface UpsertMessage {

    String getCollectionName() throws ReadValueException, ChangeValueException;
}
