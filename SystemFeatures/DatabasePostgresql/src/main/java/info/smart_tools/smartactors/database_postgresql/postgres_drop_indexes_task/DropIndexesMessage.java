package info.smart_tools.smartactors.database_postgresql.postgres_drop_indexes_task;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * A query message to be passed to {@link PostgresDropIndexesTask#prepare(IObject)}.
 */
public interface DropIndexesMessage {

    /**
     * Returns the name of the collection to drop indexes
     * @return the collection name
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    CollectionName getCollectionName() throws ReadValueException;

    /**
     * Returns the indexes describing options of the collection.
     * @return the options as IObject
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getOptions() throws ReadValueException;

}
