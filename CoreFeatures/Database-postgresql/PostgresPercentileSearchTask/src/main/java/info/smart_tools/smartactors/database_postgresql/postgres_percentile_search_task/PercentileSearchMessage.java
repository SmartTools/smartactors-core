package info.smart_tools.smartactors.database_postgresql.postgres_percentile_search_task;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public interface PercentileSearchMessage {

    /**
     * Returns the collection name where to search the document
     * @return the collection name
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    CollectionName getCollectionName() throws ReadValueException;

    /**
     * Returns the document with filters and other criteria to select the percentiles from the collection.
     * @return the set of criteria as IObject
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getCriteria() throws ReadValueException;

    /**
     * Returns the document with parameters for the percentile search in the collection
     * @return percentile config
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getPercentileCriteria() throws ReadValueException;

    /**
     * Returns the callback function to be called when the percentiles are found.
     * @return the callback function
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IAction<Number[]> getCallback() throws ReadValueException;
}
