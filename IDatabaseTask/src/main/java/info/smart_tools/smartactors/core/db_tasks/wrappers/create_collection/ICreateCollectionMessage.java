package info.smart_tools.smartactors.core.db_tasks.wrappers.create_collection;

import info.smart_tools.smartactors.core.db_tasks.wrappers.IDBTaskMessage;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.Map;

/**
 * Message wrapper for create collection query to database.
 * Contains a collection name and indexes to the collection.
 * @see IDBTaskMessage
 */
public interface ICreateCollectionMessage extends IDBTaskMessage {
    /**
     * Gives indexes to create on collection.
     *
     * Index types:
     * ordered     - for sortable fields (numeric or strings).
     * tags        - for search by tags (tags field should be an JSON array).
     * fulltext    - for full text search.
     * datetime    - for data and time (JSON String ISO-8601)
     * id          - default index for primary key.
     *
     * @return map filedName->indexType
     * @exception ReadValueException when error of reading indexes in the message.
     */
    Map<String, String> getIndexes() throws ReadValueException;

    /**
     * Sets the indexes to collection in the message.
     *
     * Index types:
     * ordered     - for sortable fields (numeric or strings).
     * tags        - for search by tags (tags field should be an JSON array).
     * fulltext    - for full text search.
     * datetime    - for data and time (JSON String ISO-8601)
     * id          - default index for primary key.
     *
     * @param indexes - database indexes to a some collection.
     * @exception ChangeValueException when error of writing indexes in the message.
     */
    void setIndexes(Map<String, String> indexes) throws ChangeValueException;
}
