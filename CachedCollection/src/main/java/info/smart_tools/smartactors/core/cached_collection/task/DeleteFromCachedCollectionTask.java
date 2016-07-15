package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Deletes object from cached collection (sets isActive flag to false)
 */
public class DeleteFromCachedCollectionTask implements IDatabaseTask {
    private IStorageConnection connection;

    private IDatabaseTask updateTask;

    private IField isActiveField;

    /**
     * @param updateTask Target update task
     * @throws CreateCachedCollectionTaskException for create task error
     */
    public DeleteFromCachedCollectionTask(final IDatabaseTask updateTask) throws CreateCachedCollectionTaskException {
        this.updateTask = updateTask;
        try {
            this.isActiveField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "document/isActive");
        } catch (ResolutionException e) {
            throw new CreateCachedCollectionTaskException("Can't create GetObjectFromCachedCollectionTask.", e);
        }
    }

    /**
     * Prepares database query
     * @param query query object
     *              <pre>
     *              {
     *                  "document" : {CACHED ITEM},
     *                  "collectionName" : "COLLECTION_NAME"
     *              }
     *              </pre>
     * The same query would be passed to the nested task's prepare method,
     * but isActive field in document would be set to false.
     * @throws TaskPrepareException if error occurs in process of query preparing
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            isActiveField.out(query, false);
            updateTask.prepare(query);
        } catch (InvalidArgumentException | ChangeValueException e) {
            throw new TaskPrepareException("Can't prepare query for delete from cached collection", e);
        }

    }

    @Override
    public void setConnection(final IStorageConnection connection) {
        this.connection = connection;
         updateTask.setConnection(connection);
    }

    @Override
    public IStorageConnection getConnection() {
        return connection;
    }

    /**
     * Execute the task.
     *
     * @throws TaskExecutionException if error occurs in process of task execution
     */
    @Override
    public void execute() throws TaskExecutionException {
        updateTask.execute();
    }
}
