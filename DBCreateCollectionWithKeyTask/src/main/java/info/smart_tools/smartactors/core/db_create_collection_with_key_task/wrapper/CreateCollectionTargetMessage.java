package info.smart_tools.smartactors.core.db_create_collection_with_key_task.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.Map;

public interface CreateCollectionTargetMessage {

    void setCollectionName(String collectionName) throws ReadValueException, ChangeValueException;

    void setIndexes(Map<String, String> indexes) throws ReadValueException, ChangeValueException;

}
