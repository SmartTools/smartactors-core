package info.smart_tools.smartactors.database.async_operation_collection;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.pool_guard.IPoolGuard;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.base.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.CompleteAsyncOperationException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.CreateAsyncOperationException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.DeleteAsyncOperationException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * Implementation of collection for asynchronous operations
 * TODO:: realize cache
 */
public class AsyncOperationCollection implements IAsyncOperationCollection {

    private IPool connectionPool;
    private String collectionName;
    private IField idField;

    /**
     * Constructor for implementation
     * @param connectionPool connection pool
     * @param collectionName string with name
     * @throws InvalidArgumentException if we can't create collection name or field
     */
    public AsyncOperationCollection(final IPool connectionPool, final String collectionName) throws InvalidArgumentException {
        this.connectionPool = connectionPool;
        try {
            this.idField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "id");
            this.collectionName = collectionName;
        } catch (ResolutionException e) {
            throw new InvalidArgumentException("Can't create field", e);
        }
    }

    @Override
    public IObject getAsyncOperation(final String token) throws GetAsyncOperationException {
        try (IPoolGuard guard = new PoolGuard(connectionPool)) {
            IObject result = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

            IDatabaseTask getItemTask = IOC.resolve(
                Keys.getKeyByName("db.async_ops_collection.get"),
                guard.getObject(),
                collectionName,
                token,
                (IAction<IObject[]>) docs -> {
                    try {
                        result.setValue(new FieldName("result"), docs[0]);
                    } catch (ChangeValueException e) {
                        throw new ActionExecutionException(e);
                    }
                }
            );
            getItemTask.execute();

            IObject searchResult = (IObject) result.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "result"));
            if (searchResult == null) {
                throw new GetAsyncOperationException("Can't find operation.");
            }

            return searchResult;
        } catch (ResolutionException e) {
            throw new GetAsyncOperationException("Can't resolve object during get operation.", e);
        } catch (InvalidArgumentException e) {
            throw new GetAsyncOperationException("Can't register strategy for getItem task.", e);
        } catch (PoolGuardException e) {
            throw new GetAsyncOperationException("Can't get connection from pool.", e);
        } catch (ReadValueException e) {
            throw new GetAsyncOperationException("Error during preparing read task.", e);
        } catch (TaskExecutionException e) {
            throw new GetAsyncOperationException("Error during execution read task.", e);
        }
    }

    @Override
    public void createAsyncOperation(final IObject data, final String token, final String expiredTime) throws CreateAsyncOperationException {
        try {
            try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {


                IDatabaseTask createItemTask = IOC.resolve(
                        Keys.getKeyByName("db.async_ops_collection.create"),
                        poolGuard.getObject(),
                        collectionName,
                        data,
                        token,
                        expiredTime
                );
                createItemTask.execute();
            } catch (Exception e) {
                throw new CreateAsyncOperationException("Failed to create async operation.");
            }
        } catch (Exception e) {
            throw new CreateAsyncOperationException("Failed to create async operation.", e);
        }
    }

    @Override
    public void delete(final String token) throws DeleteAsyncOperationException {

        try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {

            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.async_ops_collection.delete"),
                    poolGuard.getObject(),
                    collectionName,
                    token
            );
            task.execute();
        } catch (TaskExecutionException e) {
            throw new DeleteAsyncOperationException("Error during execution complete.", e);
        } catch (PoolGuardException e) {
            throw new DeleteAsyncOperationException("Can't get connection from pool.", e);
        } catch (ResolutionException e) {
            throw new DeleteAsyncOperationException("Can't get Task from IOC", e);
        }
    }


    @Override
    public void complete(final IObject document) throws CompleteAsyncOperationException {

        try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {

            ITask updateTask = IOC.resolve(Keys.getKeyByName("db.async_ops_collection.complete"),
                    poolGuard.getObject(),
                    collectionName,
                    document
            );
            updateTask.execute();

        } catch (TaskExecutionException e) {
            throw new CompleteAsyncOperationException("Error during execution complete.", e);
        } catch (PoolGuardException e) {
            throw new CompleteAsyncOperationException("Can't get connection from pool.", e);
        } catch (ResolutionException e) {
            throw new CompleteAsyncOperationException("Can't resolve async operation object.", e);
        }
    }
}
