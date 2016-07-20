package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.exception.CreateAsyncOperationException;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
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
 * DB task for creating operations in db
 */
public class CreateAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask task;

    private IField asyncDataField;
    private IField doneFlagField;
    private IField tokenField;
    private IField expiredTimeField;
    private IField documentField;
    /**
     * Constructor
     * @param task the insert DB task
     * @throws CreateAsyncOperationTask Throw when task can't be created (for example, when can't resolve some of field)
     */
    public CreateAsyncOperationTask(final IDatabaseTask task) throws CreateAsyncOperationException {
        this.task = task;

        try {
            asyncDataField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "asyncData");
            doneFlagField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "done");
            tokenField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "token");
            expiredTimeField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "expiredTime");
            documentField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "document");
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

            IObject document = IOC.resolve(Keys.getOrAdd(IObject.class.toString()));

            asyncDataField.out(document, asyncDataField.in(query));
            doneFlagField.out(document, false);
            tokenField.out(document, tokenField.in(query));
            expiredTimeField.out(document, expiredTimeField.in(query));
            documentField.out(query, document);

            task.prepare(query);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't resolve objects during prepare create async operation", e);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new TaskPrepareException("Can't prepare query for create async operation cause one of IField.out operation down", e);
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
