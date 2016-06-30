package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.wrapper.upsert.UpsertIntoCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.upsert.UpsertItem;
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

import java.time.LocalDateTime;

/**
 * Makes add or update operation for cached collection
 */
public class UpsertIntoCachedCollectionTask implements IDatabaseTask {

    private IDatabaseTask upsertTask;

    /**
     * @param upsertTask Target update task
     */
    public UpsertIntoCachedCollectionTask(final IDatabaseTask upsertTask) {
        this.upsertTask = upsertTask;
    }

    /**
     * Prepares database query
     *
     * @param query query object
     * @throws TaskPrepareException if error occurs in process of query preparing
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            UpsertIntoCachedCollectionQuery message = IOC.resolve(Keys.getOrAdd(UpsertIntoCachedCollectionQuery.class.toString()), query);
            UpsertItem upsertItem = message.getUpsertItem();
            if (upsertItem.getStartDateTime() == null) {
                upsertItem.setStartDateTime(LocalDateTime.now());
            }
            upsertTask.prepare(message.wrapped());
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't resolve objects during prepare upsert into cached collection", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Can't prepare query for upsert into cached collection", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        upsertTask.setConnection(connection);
    }

    /**
     * Execute the task.
     *
     * @throws TaskExecutionException if error occurs in process of task execution
     */
    @Override
    public void execute() throws TaskExecutionException {
        upsertTask.execute();
    }
}
