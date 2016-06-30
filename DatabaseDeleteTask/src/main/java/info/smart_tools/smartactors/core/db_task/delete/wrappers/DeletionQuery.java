package info.smart_tools.smartactors.core.db_task.delete.wrappers;

/**
 * Message with parameters for deletion query.
 * Should contains a collection name and a list of documents ids.
 */
public interface DeletionQuery {
    /** The name of the collection to which the query is executed. */
    String getCollectionName();
    /** Number of documents ids. */
    int countDocumentIds();
    /** Gives document id from the list of documents ids by index.  */
    Long getDocumentIds(int index);
}
