package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.exception.UpdateAsyncOperationException;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Task for mark async operation as done
 */
public class UpdateAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask upsertTask;

    private IField doneFlagField;

    /**
     * Constructor
     * @param upsertTask nested task for update operation
     * @throws ResolutionException Throw when task can't be created (for example, when can't resolve some of field)
     */
    public UpdateAsyncOperationTask(final IDatabaseTask upsertTask) throws UpdateAsyncOperationException {
        this.upsertTask = upsertTask;

        try {
            doneFlagField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "document/done");
        } catch (ResolutionException e) {
            throw new UpdateAsyncOperationException("Can't resolve one of fields", e);
        }
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            doneFlagField.out(query, true);
            upsertTask.prepare(query);
        } catch (ChangeValueException | InvalidArgumentException e) {
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
