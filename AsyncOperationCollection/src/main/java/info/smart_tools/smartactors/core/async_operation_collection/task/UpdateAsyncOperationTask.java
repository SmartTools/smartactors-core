package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.update.UpdateAsyncOperationQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.update.UpdateItem;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.wrapper_generator.Field;

/**
 * Task for mark async operation as done
 */
public class UpdateAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask upsertTask;

    private Field<IObject> updateIObjectField;
    private Field<Boolean> doneFlagField;

    /**
     * Constructor
     * @param upsertTask nested task for update operation
     */
    public UpdateAsyncOperationTask(final IDatabaseTask upsertTask) throws ResolutionException, InvalidArgumentException {
        this.upsertTask = upsertTask;

        updateIObjectField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "updateItem"));
        doneFlagField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "done"));
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            IObject updateItem = updateIObjectField.out(query);
            doneFlagField.in(updateItem, true);
            upsertTask.prepare(query);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
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
