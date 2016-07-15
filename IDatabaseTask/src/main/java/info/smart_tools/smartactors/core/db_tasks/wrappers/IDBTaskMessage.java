package info.smart_tools.smartactors.core.db_tasks.wrappers;

import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * General wrapper for an another database queries messages wrappers.
 * Provides methods to obtains and sets the name of the collection to which you sent the query.
 */
public interface IDBTaskMessage {
    /**
     * Takes collection name from the message to database.
     *
     * @return name of current collection.
     *
     * @exception  ReadValueException when the collection name field value couldn't been read.
     */
    ICollectionName getCollection() throws ReadValueException;

    /**
     * Adds collection name field to the message to database.
     *
     * @param collectionName - collection name for updating.
     *
     * @exception ChangeValueException when the collection name field couldn't been added to the message.
     */
    void setCollection(ICollectionName collectionName) throws ChangeValueException;
}
