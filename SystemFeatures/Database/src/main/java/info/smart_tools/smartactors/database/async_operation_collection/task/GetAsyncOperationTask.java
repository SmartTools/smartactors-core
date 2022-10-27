package info.smart_tools.smartactors.database.async_operation_collection.task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * Task-facade for read task for async operations collection
 */
public class GetAsyncOperationTask implements IDatabaseTask {


    private IDatabaseTask getItemTask;
    private IStorageConnection connection;

    private IField collectionNameField;
    private IField callbackField;
    private IField equalsField;
    private IField filterField;
    private IField tokenField;

    /**
     * Constructor
     * @param connection connection
     * @throws GetAsyncOperationException Throw when task can't be created (for example, when can't resolve some of field)
     */
    public GetAsyncOperationTask(final IStorageConnection connection) throws GetAsyncOperationException {
        this.connection = connection;

        try {
            this.callbackField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "callback");
            this.equalsField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "$eq");
            this.filterField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "filter");
            this.tokenField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "token");
            this.collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
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
            IObject queryForNestedTask  = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            IObject filterObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

            String token = tokenField.in(query);

            IObject eqKeyObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            equalsField.out(eqKeyObject, token);
            tokenField.out(filterObject, eqKeyObject);

            filterField.out(queryForNestedTask, filterObject);


            getItemTask = IOC.resolve(
                    Keys.getKeyByName("db.collection.search"),
                    connection,
                    collectionNameField.in(query),
                    queryForNestedTask,
                    callbackField.in(query)
            );
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create ISearchQuery from input query", e);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new TaskPrepareException("Can't change value in one of IObjects", e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        getItemTask.execute();
    }
}
