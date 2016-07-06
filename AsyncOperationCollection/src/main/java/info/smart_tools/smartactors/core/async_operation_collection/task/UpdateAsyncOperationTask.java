package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.update.UpdateAsyncOperationQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.update.UpdateItem;
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
 * Task for mark async operation as done
 */
public class UpdateAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask upsertTask;

    /**
     * Constructor
     * @param upsertTask nested task for update operation
     */
    public UpdateAsyncOperationTask(final IDatabaseTask upsertTask) {
        this.upsertTask = upsertTask;
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            UpdateAsyncOperationQuery message = IOC.resolve(Keys.getOrAdd(UpdateAsyncOperationQuery.class.toString()), query);
            //TODO:: mb use field?
            UpdateItem updateItem = message.getUpdateItem();
            updateItem.setIsDone(true);
            upsertTask.prepare(IOC.resolve(Keys.getOrAdd(IObject.class.toString()), message));
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't resolve objects during prepare update into async operation collection", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Can't prepare query for update into async operation collection", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        upsertTask.setConnection(connection);
    }

    @Override
    public void execute() throws TaskExecutionException {
        upsertTask.execute();
    }
}
