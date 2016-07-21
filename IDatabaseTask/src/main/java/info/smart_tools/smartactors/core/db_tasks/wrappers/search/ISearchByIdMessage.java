package info.smart_tools.smartactors.core.db_tasks.wrappers.search;

import info.smart_tools.smartactors.core.db_tasks.wrappers.IDBTaskMessage;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Message wrapper for search by id query to database.
 * Contains collection name, document's id and search result.
 */
public interface ISearchByIdMessage extends IDBTaskMessage {
    /**
     * @return the document's id which must be deleted.
     * @exception ReadValueException when error of reading document's id in the message.
     */
    Long getDocumentId() throws ReadValueException;

    /**
     * Sets the document's id in the message.
     * @param documentId - document's id which must be deleted.
     * @exception ChangeValueException when error of writing document's id in the message.
     * */
    void setDocumentId(Long documentId) throws ChangeValueException;

    /**
     * Sets the search result in the message.
     * @param object the found document
     * @exception ChangeValueException when error of writing search result in the message.
     */
    void setSearchResult(IObject object) throws ChangeValueException;

    /**
     * @return the search result.
     * @exception ReadValueException when error of reading search result in the message.
     */
    IObject getSearchResult() throws ReadValueException;
}
