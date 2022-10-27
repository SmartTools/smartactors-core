package info.smart_tools.smartactors.database_postgresql.postgres_upsert_task;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * A query message to be passed to {@link PostgresUpsertTask#prepare(IObject)}.
 */
public interface UpsertMessage {

    /**
     * Returns the collection name where to upsert the document
     * @return the collection name
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    CollectionName getCollectionName() throws ReadValueException;

    /**
     * Returns the document to be upserted.
     * @return the document as IObject
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getDocument() throws ReadValueException;

}
