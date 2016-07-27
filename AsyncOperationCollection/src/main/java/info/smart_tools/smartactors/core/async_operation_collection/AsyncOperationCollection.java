package info.smart_tools.smartactors.core.async_operation_collection;

import info.smart_tools.smartactors.core.async_operation_collection.exception.CompleteAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.exception.CreateAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.exception.DeleteAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.exception.UpdateAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.task.CreateAsyncOperationTask;
import info.smart_tools.smartactors.core.async_operation_collection.task.DeleteAsyncOperationTask;
import info.smart_tools.smartactors.core.async_operation_collection.task.GetAsyncOperationTask;
import info.smart_tools.smartactors.core.async_operation_collection.task.UpdateAsyncOperationTask;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.create_item.CreateOperationQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.delete.DeleteAsyncOperationQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.GetAsyncOperationQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.update.UpdateAsyncOperationQuery;
import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.pool_guard.IPoolGuard;
import info.smart_tools.smartactors.core.pool_guard.PoolGuard;
import info.smart_tools.smartactors.core.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of collection for asynchronous operations
 * TODO:: realize cache
 */
public class AsyncOperationCollection implements IAsyncOperationCollection {

    private IPool connectionPool;
    private CollectionName collectionName;
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
            this.idField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "id");
            this.collectionName = CollectionName.fromString(collectionName);
        } catch (QueryBuildException e) {
            throw new InvalidArgumentException("Can't create async operations collection.", e);
        } catch (ResolutionException e) {
            throw new InvalidArgumentException("Can't create field", e);
        }
    }

    @Override
    public IObject getAsyncOperation(final String token) throws GetAsyncOperationException {

        try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
            IDatabaseTask getItemTask = IOC.resolve(Keys.getOrAdd(GetAsyncOperationTask.class.toString()));
            if (getItemTask == null) {
                IDatabaseTask nestedTask = IOC.resolve(
                    Keys.getOrAdd(IDatabaseTask.class.toString()), GetAsyncOperationTask.class.toString()
                );
                if (nestedTask == null) {
                    throw new GetAsyncOperationException("Can't create nested task for getItem task.");
                }
                getItemTask = new GetAsyncOperationTask(nestedTask);
                IOC.register(Keys.getOrAdd(GetAsyncOperationTask.class.toString()), new SingletonStrategy(getItemTask));
            }
            GetAsyncOperationQuery getItemQuery = IOC.resolve(Keys.getOrAdd(GetAsyncOperationQuery.class.toString()));
            getItemQuery.setCollectionName(collectionName);
            getItemQuery.setToken(token);
            getItemTask.setConnection(IOC.resolve(Keys.getOrAdd(StorageConnection.class.toString()), poolGuard.getObject()));
            getItemTask.prepare(getItemQuery.wrapped());
            getItemTask.execute();
            List<IObject> searchResult = getItemQuery.getSearchResult();
            if (searchResult == null || searchResult.isEmpty()) {
                throw new GetAsyncOperationException("Can't find operation.");
            }

            return searchResult.get(0);
        } catch (ResolutionException e) {
            throw new GetAsyncOperationException("Can't resolve object during get operation.", e);
        } catch (InvalidArgumentException | RegistrationException e) {
            throw new GetAsyncOperationException("Can't register strategy for getItem task.", e);
        } catch (PoolGuardException e) {
            throw new GetAsyncOperationException("Can't get connection from pool.", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new GetAsyncOperationException("Can't read asynchronous operation.", e);
        } catch (TaskSetConnectionException e) {
            throw new GetAsyncOperationException("Can't set connection to read task.", e);
        } catch (TaskPrepareException e) {
            throw new GetAsyncOperationException("Error during preparing read task.", e);
        } catch (TaskExecutionException e) {
            throw new GetAsyncOperationException("Error during execution read task.", e);
        }
    }

    @Override
    public void createAsyncOperation(final IObject data, final String token, final String expiredTime)
        throws CreateAsyncOperationException {

        try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
            IDatabaseTask task = IOC.resolve(Keys.getOrAdd(CreateAsyncOperationTask.class.toString()));
            if (task == null) {
                IDatabaseTask nestedTask = IOC.resolve(
                        Keys.getOrAdd(IDatabaseTask.class.toString()), CreateAsyncOperationTask.class.toString()
                );
                if (nestedTask == null) {
                    throw new CreateAsyncOperationException("Can't create nested task for createtItem task.");
                }
                task = new CreateAsyncOperationTask(nestedTask);
                IOC.register(Keys.getOrAdd(GetAsyncOperationTask.class.toString()), new SingletonStrategy(task));
            }
            CreateOperationQuery query = IOC.resolve(Keys.getOrAdd(CreateOperationQuery.class.toString()));
            query.setCollectionName(collectionName);
            query.setAsyncData(data);
            query.setExpiredTime(expiredTime);
            query.setToken(token);
            task.setConnection((StorageConnection) poolGuard.getObject());
            task.prepare(query.getIObject());
            task.execute();
        } catch (Exception e) {
            throw new CreateAsyncOperationException("Failed to create async operation.");
        }
    }

    @Override
    public void complete(final IObject asyncOperation) throws CompleteAsyncOperationException {

        try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
            IDatabaseTask updateTask = IOC.resolve(Keys.getOrAdd(UpdateAsyncOperationTask.class.toString()));
            if (updateTask == null) {
                IDatabaseTask nestedTask = IOC.resolve(
                    Keys.getOrAdd(IDatabaseTask.class.toString()), UpdateAsyncOperationTask.class.toString()
                );
                if (nestedTask == null) {
                    throw new CompleteAsyncOperationException("Can't create nested task for update task.");
                }
                updateTask = new UpdateAsyncOperationTask(nestedTask);
                IOC.register(Keys.getOrAdd(UpdateAsyncOperationTask.class.toString()), new SingletonStrategy(updateTask));
            }
            UpdateAsyncOperationQuery upsertQuery = IOC.resolve(Keys.getOrAdd(UpdateAsyncOperationQuery.class.toString()));
            upsertQuery.setCollectionName(collectionName);
            upsertQuery.setUpdateItem(asyncOperation);

            updateTask.setConnection(IOC.resolve(Keys.getOrAdd(StorageConnection.class.toString()), poolGuard.getObject()));
            updateTask.prepare(IOC.resolve(Keys.getOrAdd(IObject.class.toString()), upsertQuery));
            updateTask.execute();
        } catch (TaskExecutionException e) {
            throw new CompleteAsyncOperationException("Error during execution complete.", e);
        } catch (PoolGuardException e) {
            throw new CompleteAsyncOperationException("Can't get connection from pool.", e);
        } catch (TaskSetConnectionException e) {
            throw new CompleteAsyncOperationException("Can't set connection to update task.", e);
        } catch (TaskPrepareException e) {
            throw new CompleteAsyncOperationException("Error during preparing update task.", e);
        } catch (InvalidArgumentException | RegistrationException e) {
            throw new CompleteAsyncOperationException("Can't register strategy for update task.", e);
        } catch (ChangeValueException e) {
            throw new CompleteAsyncOperationException("Can't complete async operation.", e);
        } catch (ResolutionException e) {
            throw new CompleteAsyncOperationException("Can't resolve async operation object.", e);
        } catch (UpdateAsyncOperationException e) {
            throw new CompleteAsyncOperationException("Can't create new UpdateAsyncOperationTask.", e);
        }
    }

    @Override
    public void delete(final String token) throws DeleteAsyncOperationException {

        try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
            IDatabaseTask deleteTask = IOC.resolve(Keys.getOrAdd(DeleteAsyncOperationTask.class.toString()));
            if (deleteTask == null) {
                IDatabaseTask nestedTask = IOC.resolve(
                    Keys.getOrAdd(IDatabaseTask.class.toString()), DeleteAsyncOperationTask.class.toString()
                );
                if (nestedTask == null) {
                    throw new DeleteAsyncOperationException("Can't create nested task for update task.");
                }
                deleteTask = new DeleteAsyncOperationTask(nestedTask);
                IOC.register(Keys.getOrAdd(DeleteAsyncOperationTask.class.toString()), new SingletonStrategy(deleteTask));
            }
            DeleteAsyncOperationQuery deleteQuery = IOC.resolve(Keys.getOrAdd(DeleteAsyncOperationQuery.class.toString()));
            deleteQuery.setCollectionName(collectionName);
            IObject deleteItem = getAsyncOperation(token);
            deleteQuery.setDocumentIds(Collections.singletonList(idField.in(deleteItem)));

            deleteTask.setConnection(IOC.resolve(Keys.getOrAdd(StorageConnection.class.toString()), poolGuard.getObject()));
            deleteTask.prepare(IOC.resolve(Keys.getOrAdd(IObject.class.toString()), deleteQuery));
            deleteTask.execute();
        } catch (TaskExecutionException e) {
            throw new DeleteAsyncOperationException("Error during execution complete.", e);
        } catch (PoolGuardException e) {
            throw new DeleteAsyncOperationException("Can't get connection from pool.", e);
        } catch (TaskSetConnectionException e) {
            throw new DeleteAsyncOperationException("Can't set connection to update task.", e);
        } catch (TaskPrepareException e) {
            throw new DeleteAsyncOperationException("Error during preparing update task.", e);
        } catch (InvalidArgumentException | RegistrationException e) {
            throw new DeleteAsyncOperationException("Can't register strategy for update task.", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new DeleteAsyncOperationException("Can't complete async operation.", e);
        } catch (ResolutionException e) {
            throw new DeleteAsyncOperationException("Can't resolve async operation object.", e);
        } catch (GetAsyncOperationException e) {
            throw new DeleteAsyncOperationException("Can't get async operation by token.", e);
        }
    }
}
