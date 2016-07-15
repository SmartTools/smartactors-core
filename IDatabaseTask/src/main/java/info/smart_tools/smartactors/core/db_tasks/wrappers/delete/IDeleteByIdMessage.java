package info.smart_tools.smartactors.core.db_tasks.wrappers.delete;

import info.smart_tools.smartactors.core.db_tasks.wrappers.IDBTaskMessage;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Message wrapper for delete document by id query to database.
 * Contains collection name and document's id.
 * @see IDBTaskMessage
 */
public interface IDeleteByIdMessage extends IDBTaskMessage {
    /**
     * @return the document's id which must be deleted.
     * @exception ReadValueException when error of reading document's id in the message.
     */
    Long getDocumentId() throws ReadValueException;

    /**
     * Sets the document's id in the message.
     * @param documentId - document's id which must be deleted.
     * @exception ChangeValueException when error of writing document's id in the message.
     */
    void setDocumentId(Long documentId) throws ChangeValueException;
}
