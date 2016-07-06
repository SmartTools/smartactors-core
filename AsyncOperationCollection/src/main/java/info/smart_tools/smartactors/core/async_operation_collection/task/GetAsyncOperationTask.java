package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.AsyncOperationTaskQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.EQMessage;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.GetAsyncOperationQuery;
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
 * Task-facade for read task for async operations collection
 */
public class GetAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask getItemTask;

    /**
     * Constructor
     * @param getItemTask nested task for read object
     */
    public GetAsyncOperationTask(final IDatabaseTask getItemTask) {
        this.getItemTask = getItemTask;
    }

    /**
     * @param query query object
     *              {
     *                  "collectionName": "name of async operation collection",
     *                  "token": "unique token of async operation for search"
     *              }
     * @throws TaskPrepareException for prepare error
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            GetAsyncOperationQuery srcQueryObject = IOC.resolve(Keys.getOrAdd(GetAsyncOperationQuery.class.toString()), query);
            AsyncOperationTaskQuery criteriaQuery = IOC.resolve(Keys.getOrAdd(AsyncOperationTaskQuery.class.toString()));

            EQMessage tokenCondition = IOC.resolve(Keys.getOrAdd(EQMessage.class.toString()));
            tokenCondition.setEq(srcQueryObject.getToken());
            criteriaQuery.setToken(tokenCondition);

            srcQueryObject.setPageNumber(1);
            srcQueryObject.setPageSize(1);
            srcQueryObject.setQuery(criteriaQuery);

            getItemTask.prepare(query);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create ISearchQuery from input query", e);
        } catch (ChangeValueException | ReadValueException e) {
            throw new TaskPrepareException("Can't change value in one of IObjects", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        getItemTask.setConnection(connection);
    }

    @Override
    public void execute() throws TaskExecutionException {
        getItemTask.execute();
    }
}
