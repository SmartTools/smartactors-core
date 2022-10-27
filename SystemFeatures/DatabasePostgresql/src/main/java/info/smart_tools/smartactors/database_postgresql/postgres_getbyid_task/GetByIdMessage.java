package info.smart_tools.smartactors.database_postgresql.postgres_getbyid_task;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * A query message to be passed to {@link PostgresGetByIdTask#prepare(IObject)}.
 */
public interface GetByIdMessage {

    /**
     * Returns the collection name where to search the document
     * @return the collection name
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    CollectionName getCollectionName() throws ReadValueException;

    /**
     * Returns the document ID to be found.
     * @return the ID as Object
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    Object getId() throws ReadValueException;

    /**
     * Returns the callback function to be called when the document is found.
     * @return the callback function
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IAction<IObject> getCallback() throws ReadValueException;

}
