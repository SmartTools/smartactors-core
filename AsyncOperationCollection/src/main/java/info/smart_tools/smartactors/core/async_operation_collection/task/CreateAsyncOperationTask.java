package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.create_item.AsyncDocument;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.create_item.CreateOperationQuery;
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
 * DB task for creating operations in db
 */
public class CreateAsyncOperationTask implements IDatabaseTask {

    private IDatabaseTask task;

    private Field<IObject> asyncDataField;
    private Field<Boolean> doneFlagField;
    private Field<String> tokenField;
    private Field<String> expiredTimeField;
    private Field<IObject> documentField;
    /**
     * Constructor
     * @param task the insert DB task
     */
    public CreateAsyncOperationTask(final IDatabaseTask task) throws ResolutionException, InvalidArgumentException {
        this.task = task;

        asyncDataField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "asyncData"));
        doneFlagField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "done"));
        tokenField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "token"));
        expiredTimeField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "expiredTime"));
        documentField = new Field<>(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), "document"));
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

            asyncDataField.in(document, asyncDataField.out(query));
            doneFlagField.in(document, false);
            tokenField.in(document, tokenField.out(query));
            expiredTimeField.in(document, expiredTimeField.out(query));
            documentField.in(query, document);

            task.prepare(query);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't resolve objects during prepare create async operation", e);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new TaskPrepareException("Can't prepare query for create async operation cause one of Field.out operation down", e);
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
