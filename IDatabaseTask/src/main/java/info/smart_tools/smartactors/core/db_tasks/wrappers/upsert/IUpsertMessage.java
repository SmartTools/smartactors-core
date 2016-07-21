package info.smart_tools.smartactors.core.db_tasks.wrappers.upsert;

import info.smart_tools.smartactors.core.db_tasks.wrappers.IDBTaskMessage;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Message wrapper for insert/update document query.
 * Contains collection name and document for insert/update.
 * @see IDBTaskMessage
 */
public interface IUpsertMessage extends IDBTaskMessage {
    /**
     * Gives a document from the message.
     * @return {@link IObject} instance of the document.
     * @throws ReadValueException when the document value couldn't been read.
     */
    IObject getDocument() throws ReadValueException;

    /**
     * Sets document field in the message.
     * @param document - a some document for insert/update.
     * @throws ChangeValueException when the documents list field couldn't been added to the message.
     */
    void setDocument(IObject document) throws ChangeValueException;
}
