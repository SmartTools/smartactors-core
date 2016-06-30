package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.wrapper.delete.DeleteFromCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.delete.DeleteItem;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Deletes object from cached collection (sets isActive flag to false)
 */
public class DeleteFromCachedCollectionTask implements IDatabaseTask {

    private IDatabaseTask updateTask;

    /**
     * @param updateTask Target update task
     */
    public DeleteFromCachedCollectionTask(final IDatabaseTask updateTask) {
        this.updateTask = updateTask;
    }

    /**
     * Prepares database query
     * @param query query object
     * @throws TaskPrepareException if error occurs in process of query preparing
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            DeleteFromCachedCollectionQuery message = IOC.resolve(Keys.getOrAdd(DeleteFromCachedCollectionQuery.class.toString()), query);
            DeleteItem deleteItem = message.getDeleteItem();
            deleteItem.setIsActive(false);
            updateTask.prepare(message.wrapped());
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't resolve message during prepare delete from cached collection", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Can't prepare query for delete from cached collection", e);
        }

    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
         updateTask.setConnection(connection);
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
