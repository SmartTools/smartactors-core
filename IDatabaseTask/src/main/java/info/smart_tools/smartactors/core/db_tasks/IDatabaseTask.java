package info.smart_tools.smartactors.core.db_tasks;

import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.db_tasks.wrappers.IDBTaskMessage;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.ITask;

/**
 * Database thread-unsafe oriented task without internal transactions.
 * Used for preparation and execution a some query to database.
 * @see ITask
 * For create transaction for execution query to database may used special transaction executor.
 * @see IDBTransactionExecutor
 */
public interface IDatabaseTask extends ITask {
    /**
     * Prepare a some database oriented task to execute.
     *
     * @param message - contains parameters for preparation task.
     * @see IDBTaskMessage
     *
     * @exception TaskPrepareException when error of preparation task process.
     */
    void prepare(final IObject message) throws TaskPrepareException;

    /**
     * Setter for storage connection field.
     *
     * @param storageConnection - database connection.
     * @see IStorageConnection
     */
    void setConnection(final IStorageConnection storageConnection);

    /**
     * @return used storage connection.
     * @see IStorageConnection
     */
    IStorageConnection getConnection();
}
