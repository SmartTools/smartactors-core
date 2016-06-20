package info.smart_tools.smartactors.core.db_create_collection_with_key_task.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Created by user on 6/8/16.
 */
public interface CreateCollectionWithKeyMessage {

    String  getCollectionName() throws ReadValueException, ChangeValueException;

    String getKey() throws ReadValueException, ChangeValueException;
}
