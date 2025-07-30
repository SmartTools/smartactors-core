package info.smart_tools.smartactors.database_postgresql.postgres_search_by_limit_and_offset_task;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * A query message to be passed to {@link PostgresSearchByLimitAndOffsetTask#prepare(IObject)}.
 */
public interface SearchByLimitAndOffsetMessage {

    /**
     * Returns the collection name where to search the document
     *
     * @return the collection name
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    CollectionName getCollectionName() throws ReadValueException;

    /**
     * Returns the document with filters and other criteria to select the document from the collection.
     *
     * @return the set of criteria as IObject
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getCriteria() throws ReadValueException;

    /**
     * Returns the callback function to be called when the documents are found.
     *
     * @return the callback function
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IAction<IObject[]> getCallback() throws ReadValueException;
}
