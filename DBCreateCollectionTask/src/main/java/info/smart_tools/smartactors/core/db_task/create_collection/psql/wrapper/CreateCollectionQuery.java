package info.smart_tools.smartactors.core.db_task.create_collection.psql.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.Map;

/**
 * Wrapper for create collection query iobject
 */
public interface CreateCollectionQuery {

    /**
     * @return collection name
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    String  getCollectionName() throws ReadValueException, ChangeValueException;
    /**
     *  What indexes to create on collection.
     *  @return map filedName->indexType
     *
     *  Index types:
     *  ordered     - for sortable fields (numeric or strings).
     *  tags        - for search by tags (tags field should be an JSON array).
     *
     */
    Map<String, String> getIndexes() throws ReadValueException, ChangeValueException;;
}
