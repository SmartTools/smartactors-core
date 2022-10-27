package info.smart_tools.smartactors.database.async_operation_collection.task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.CreateAsyncOperationException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * DB task for creating operations in db
 */
public class CreateAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask task;
    private IStorageConnection connection;

    private IField documentField;
    private IField collectionNameField;
    /**
     * Constructor
     * @param connection
     * @throws CreateAsyncOperationTask Throw when task can't be created (for example, when can't resolve some of field)
     */
    public CreateAsyncOperationTask(final IStorageConnection connection) throws CreateAsyncOperationException {
        this.connection = connection;

        try {
            documentField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "document");
            collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
        } catch (ResolutionException e) {
            throw new CreateAsyncOperationException("Can't resolve one of fields", e);
        }
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
            task = IOC.resolve(
                    Keys.getKeyByName("db.collection.upsert"),
                    connection,
                    collectionNameField.in(query),
                    documentField.in(query)
            );
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create ISearchQuery from input query", e);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new TaskPrepareException("Can't change value in one of IObjects", e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        this.task.execute();
    }
}
