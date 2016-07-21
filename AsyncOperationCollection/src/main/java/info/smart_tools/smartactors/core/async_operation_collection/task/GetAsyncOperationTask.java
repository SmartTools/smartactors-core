package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
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
    private IStorageConnection connection;
    private IDatabaseTask getItemTask;

    private IField pageNumberField;
    private IField pageSizeField;
    private IField queryField;
    private IField eqField;
    private IField tokenField;

    /**
     * Constructor
     * @param getItemTask nested task for read object
     * @throws GetAsyncOperationException Throw when task can't be created (for example, when can't resolve some of field)
     */
    public GetAsyncOperationTask(final IDatabaseTask getItemTask) throws GetAsyncOperationException {
        this.getItemTask = getItemTask;

        try {
            pageNumberField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "pageNumber");
            pageSizeField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "pageSize");
            queryField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "query");
            eqField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "$eq");
            tokenField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "token");
        } catch (ResolutionException e) {
            throw new GetAsyncOperationException("Can't resolve one of fields", e);
        }
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

            eqField.out(criteriaIObject, tokenField.in(query));

            pageNumberField.out(query, 1);
            pageSizeField.out(query, 1);
            queryField.out(query, criteriaIObject);

            getItemTask.prepare(query);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create ISearchQuery from input query", e);
        } catch (ChangeValueException | ReadValueException | InvalidArgumentException e) {
            throw new TaskPrepareException("Can't change value in one of IObjects", e);
        }
    }

    @Override
    public void setConnection(final IStorageConnection connection) {
        this.connection = connection;
        getItemTask.setConnection(connection);
    }

    @Override
    public IStorageConnection getConnection() {
        return connection;
    }

    @Override
    public void execute() throws TaskExecutionException {
        getItemTask.execute();
    }
}
