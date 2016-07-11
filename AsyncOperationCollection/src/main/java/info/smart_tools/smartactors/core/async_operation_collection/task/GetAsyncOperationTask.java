package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.AsyncOperationTaskQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.EQMessage;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.GetAsyncOperationQuery;
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
 * Task-facade for read task for async operations collection
 */
public class GetAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask getItemTask;

    private Field<Integer> pageNumberField;
    private Field<Integer> pageSizeField;
    private Field<IObject> queryField;
    private Field<String> eqField;
    private Field<String> tokenField;

    /**
     * Constructor
     * @param getItemTask nested task for read object
     */
    public GetAsyncOperationTask(final IDatabaseTask getItemTask) throws ResolutionException, InvalidArgumentException {
        this.getItemTask = getItemTask;

        pageNumberField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "pageNumber"));
        pageSizeField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "pageSize"));
        queryField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "query"));
        eqField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "$eq"));
        tokenField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "token"));
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

            IObject criteriaIObject = IOC.resolve(Keys.getOrAdd(IObject.class.toString()));

            eqField.in(criteriaIObject, tokenField.out(query));

            pageNumberField.in(query, 1);
            pageSizeField.in(query, 1);
            queryField.in(query, criteriaIObject);

            getItemTask.prepare(query);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create ISearchQuery from input query", e);
        } catch (ChangeValueException | ReadValueException | InvalidArgumentException e) {
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
