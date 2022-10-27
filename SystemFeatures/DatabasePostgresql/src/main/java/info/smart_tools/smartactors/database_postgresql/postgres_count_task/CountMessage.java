package info.smart_tools.smartactors.database_postgresql.postgres_count_task;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * A query message to be passed to {@link PostgresCountTask#prepare(IObject)}.
 */
public interface CountMessage {

    /**
     * Returns the collection name where to count documents
     * @return the collection name
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    CollectionName getCollectionName() throws ReadValueException;

    /**
     * Returns the document with filters to count only specified documents in the collection.
     * @return the set of criteria as IObject
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getCriteria() throws ReadValueException;

    /**
     * Returns the callback function to be called when the documents are counted.
     * @return the callback function
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IAction<Long> getCallback() throws ReadValueException;

}
