package info.smart_tools.smartactors.core.db_task.get_by_id.psql.wrapper;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for SearchById message
 */
public interface SearchByIdQuery {
    /**
     * Return the collectionName
     * @return String the name of collection where object is stored
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    String getCollectionName() throws ReadValueException, ChangeValueException;

    /**
     * Return the id of document
     * @return String the id of document should to be found
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    String getId() throws ReadValueException, ChangeValueException;

    /**
     * Set the found object to message
     * @param object the found document
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    void setSearchResult(IObject object) throws ReadValueException, ChangeValueException;
}
