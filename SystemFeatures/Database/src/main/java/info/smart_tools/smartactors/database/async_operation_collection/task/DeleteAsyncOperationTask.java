package info.smart_tools.smartactors.database.async_operation_collection.task;

import info.smart_tools.smartactors.database.async_operation_collection.exception.DeleteAsyncOperationException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * Task-facade for delete task for async operations collection
 */
public class DeleteAsyncOperationTask implements IDatabaseTask {

    private IStorageConnection connection;
    private IDatabaseTask deleteTask;

    private IField collectionNameField;
    private IField documentField;

    /**
     * Constructor
     * @param connection the storage connection
     * @throws Exception if any errors occurred
     */
    public DeleteAsyncOperationTask(final IStorageConnection connection) throws Exception {
        this.connection = connection;
        try {
            collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
            documentField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "document");
        } catch (Exception e) {
            throw new DeleteAsyncOperationException("Failed to create task", e);
        }
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            deleteTask = IOC.resolve(
                    Keys.getKeyByName("db.collection.delete"),
                    connection,
                    collectionNameField.in(query),
                    documentField.in(query)
                    );
        } catch (Exception e) {
            throw new TaskPrepareException("Failed to delete async operation", e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        deleteTask.execute();
    }
}
