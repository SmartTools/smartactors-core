package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.create_item.AsyncDocument;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.create_item.CreateOperationQuery;
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
 * DB task for creating operations in db
 */
public class CreateAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask task;

    /**
     * Constructor
     * @param task the insert DB task
     */
    public CreateAsyncOperationTask(final IDatabaseTask task) {
        this.task = task;
    }

    /**
     * @param query query object
     *     {
     *          "collectionName": "name of async operations collection",
     *          "asyncData": {iobject with data of concrete operation},
     *          "token": "unique identifier of async operation",
     *          "expiredTime": "TTL of async operation"
     *     }
     * @throws TaskPrepareException
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            CreateOperationQuery srcQueryObject = IOC.resolve(Keys.getOrAdd(CreateOperationQuery.class.toString()), query);
            AsyncDocument document = IOC.resolve(Keys.getOrAdd(AsyncDocument.class.toString()));
            document.setAsyncData(srcQueryObject.getAsyncData());
            document.setDoneFlag(false);
            document.setToken(srcQueryObject.getToken());
            document.setExpiredTime(srcQueryObject.getExpiredTime());
            srcQueryObject.setDocument(document.getIObject());
            //TODO:: mb pass to prepare new object only with needed fields?
            task.prepare(srcQueryObject.getIObject());
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't resolve objects during prepare create async operation", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Can't prepare query for create async operation", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        this.task.setConnection(connection);
    }

    @Override
    public void execute() throws TaskExecutionException {
        this.task.execute();
    }
}
