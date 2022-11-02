package info.smart_tools.smartactors.database_postgresql.postgres_delete_task;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * A query message to be passed to {@link PostgresDeleteTask#prepare(IObject)}.
 */
public interface DeleteMessage {

    /**
     * Returns the collection name where to search the document
     * @return the collection name
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    CollectionName getCollectionName() throws ReadValueException;

    /**
     * Returns the document to delete. Only the "collectionNameID" field in the document is required.
     * @return the document
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getDocument() throws ReadValueException;

}
