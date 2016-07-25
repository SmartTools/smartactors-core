package info.smart_tools.smartactors.core.async_operation_collection;

import info.smart_tools.smartactors.core.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.task.GetAsyncOperationTask;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.GetAsyncOperationQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
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
import info.smart_tools.smartactors.core.pool_guard.PoolGuard;
import info.smart_tools.smartactors.core.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, CollectionName.class, AsyncOperationCollection.class})
public class AsyncOperationCollectionTest {
    private AsyncOperationCollection testCollection;
    private IPool pool;
    private CollectionName collectionName;
    private IField idField;

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);
        mockStatic(CollectionName.class);

        pool = mock(IPool.class);

        collectionName = mock(CollectionName.class);

        when(CollectionName.fromString("async_operation")).thenReturn(collectionName);

        Key fieldNameKey = mock(Key.class);
        when(Keys.getOrAdd(IFieldName.class.getCanonicalName())).thenReturn(fieldNameKey);
        String idBindingPath = "idBindingPath";
        when(IOC.resolve(fieldNameKey, "id")).thenReturn(idBindingPath);

        idField = mock(IField.class);
        Key idKey = mock(Key.class);
        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(idKey);
        when(IOC.resolve(idKey, "id")).thenReturn(idField);

        testCollection = new AsyncOperationCollection(pool, "async_operation");
    }

    @Test
    public void MustCorrectGetAsyncOperationWhenFirstGetItemNotNull() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(getAsyncOperationTask);

        GetAsyncOperationQuery getAsyncOperationQuery = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.getCanonicalName())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey)).thenReturn(getAsyncOperationQuery);

        IObject wrapped = mock(IObject.class);
        when(getAsyncOperationQuery.wrapped()).thenReturn(wrapped);

        StorageConnection connection = mock(StorageConnection.class);
        when(poolGuard.getObject()).thenReturn(connection);
        Key connectionKey = mock(Key.class);
        when(Keys.getOrAdd(StorageConnection.class.getCanonicalName())).thenReturn(connectionKey);
        when(IOC.resolve(connectionKey, connection)).thenReturn(connection);

        List<IObject> results = new ArrayList<>();
        results.add(mock(IObject.class));
        when(getAsyncOperationQuery.getSearchResult()).thenReturn(results);

        assertTrue("Must return true value", testCollection.getAsyncOperation(token) == results.get(0));

        verifyNew(PoolGuard.class).withArguments(pool);

        verifyStatic();
        Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
        verifyStatic();
        IOC.resolve(getAsyncOperationTaskKey);

        verify(getAsyncOperationQuery).setCollectionName(collectionName);
        verify(getAsyncOperationQuery).setToken(token);
        verify(poolGuard).getObject();
        verify(getAsyncOperationTask).setConnection(connection);
        verify(getAsyncOperationTask).prepare(wrapped);
        verify(getAsyncOperationTask).execute();

        verify(getAsyncOperationQuery).getSearchResult();
        verify(poolGuard).close();
    }

    @Test
    public void MustCorrectGetAsyncOperationWhenFirstGetItemNull() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(null);

        Key nestedTaskKey = mock(Key.class);
        when(Keys.getOrAdd(IDatabaseTask.class.getCanonicalName())).thenReturn(nestedTaskKey);

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        when(IOC.resolve(nestedTaskKey, GetAsyncOperationTask.class.getCanonicalName())).thenReturn(nestedTask);

        whenNew(GetAsyncOperationTask.class).withArguments(nestedTask).thenReturn(getAsyncOperationTask);

        SingletonStrategy singletonStrategy = mock(SingletonStrategy.class);
        whenNew(SingletonStrategy.class).withArguments(getAsyncOperationTask).thenReturn(singletonStrategy);

        GetAsyncOperationQuery getAsyncOperationQuery = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.getCanonicalName())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey)).thenReturn(getAsyncOperationQuery);

        IObject wrapped = mock(IObject.class);
        when(getAsyncOperationQuery.wrapped()).thenReturn(wrapped);

        StorageConnection connection = mock(StorageConnection.class);
        when(poolGuard.getObject()).thenReturn(connection);
        Key connectionKey = mock(Key.class);
        when(Keys.getOrAdd(StorageConnection.class.getCanonicalName())).thenReturn(connectionKey);
        when(IOC.resolve(connectionKey, connection)).thenReturn(connection);

        List<IObject> results = new ArrayList<>();
        results.add(mock(IObject.class));
        when(getAsyncOperationQuery.getSearchResult()).thenReturn(results);

        assertTrue("Must return true value", testCollection.getAsyncOperation(token) == results.get(0));

        verifyNew(PoolGuard.class).withArguments(pool);

        verifyStatic(times(2));
        Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
        verifyStatic();
        IOC.resolve(getAsyncOperationTaskKey);

        verifyStatic();
        IOC.register(getAsyncOperationTaskKey, singletonStrategy);

        verify(getAsyncOperationQuery).setCollectionName(collectionName);
        verify(getAsyncOperationQuery).setToken(token);
        verify(poolGuard).getObject();
        verify(getAsyncOperationTask).setConnection(connection);
        verify(getAsyncOperationTask).prepare(wrapped);
        verify(getAsyncOperationTask).execute();

        verify(getAsyncOperationQuery).getSearchResult();
        verify(poolGuard).close();
    }

    @Test(expected = GetAsyncOperationException.class)
    public void MustInCorrectGetAsyncOperationWhenCantCreatePoolGuard() throws Exception {

        String token = "token";

        whenNew(PoolGuard.class).withArguments(pool).thenThrow(new PoolGuardException(""));

        testCollection.getAsyncOperation(token);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenKeysThrowException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {
            verifyNew(PoolGuard.class).withArguments(pool);
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenIOCResolveThrowException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenThrow(new ResolutionException(""));

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {
            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic();
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenFirstGetItemNotNullAndGetItemQueryThrowChangeValueException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(getAsyncOperationTask);

        GetAsyncOperationQuery getAsyncOperationQuery = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.getCanonicalName())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey)).thenReturn(getAsyncOperationQuery);

        doThrow(new ChangeValueException()).when(getAsyncOperationQuery).setCollectionName(collectionName);

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {

            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic();
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verify(getAsyncOperationQuery).setCollectionName(collectionName);

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenFirstGetItemNotNullAndGetItemsTaskThrowSetConnectionException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(getAsyncOperationTask);

        GetAsyncOperationQuery getAsyncOperationQuery = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.getCanonicalName())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey)).thenReturn(getAsyncOperationQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(poolGuard.getObject()).thenReturn(connection);
        Key connectionKey = mock(Key.class);
        when(Keys.getOrAdd(StorageConnection.class.getCanonicalName())).thenReturn(connectionKey);
        when(IOC.resolve(connectionKey, connection)).thenReturn(connection);

        doThrow(new TaskSetConnectionException("")).when(getAsyncOperationTask).setConnection(connection);

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {

            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic();
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verify(getAsyncOperationQuery).setCollectionName(collectionName);
            verify(getAsyncOperationQuery).setToken(token);
            verify(poolGuard).getObject();
            verify(getAsyncOperationTask).setConnection(connection);

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenFirstGetItemNotNullAndQueryThrowReadValueException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(getAsyncOperationTask);

        GetAsyncOperationQuery getAsyncOperationQuery = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.getCanonicalName())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey)).thenReturn(getAsyncOperationQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(poolGuard.getObject()).thenReturn(connection);
        Key connectionKey = mock(Key.class);
        when(Keys.getOrAdd(StorageConnection.class.getCanonicalName())).thenReturn(connectionKey);
        when(IOC.resolve(connectionKey, connection)).thenReturn(connection);

        when(getAsyncOperationQuery.wrapped()).thenThrow(new ReadValueException());

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {

            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic();
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verify(getAsyncOperationQuery).setCollectionName(collectionName);
            verify(getAsyncOperationQuery).setToken(token);
            verify(poolGuard).getObject();
            verify(getAsyncOperationTask).setConnection(connection);
            verify(getAsyncOperationQuery).wrapped();

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenFirstGetItemNotNullAndTaskThrowPrepareException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(getAsyncOperationTask);

        GetAsyncOperationQuery getAsyncOperationQuery = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.getCanonicalName())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey)).thenReturn(getAsyncOperationQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(poolGuard.getObject()).thenReturn(connection);
        Key connectionKey = mock(Key.class);
        when(Keys.getOrAdd(StorageConnection.class.getCanonicalName())).thenReturn(connectionKey);
        when(IOC.resolve(connectionKey, connection)).thenReturn(connection);

        IObject wrapped = mock(IObject.class);
        when(getAsyncOperationQuery.wrapped()).thenReturn(wrapped);

        doThrow(new TaskPrepareException("")).when(getAsyncOperationTask).prepare(wrapped);

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {

            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic();
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verify(getAsyncOperationQuery).setCollectionName(collectionName);
            verify(getAsyncOperationQuery).setToken(token);
            verify(poolGuard).getObject();
            verify(getAsyncOperationTask).setConnection(connection);
            verify(getAsyncOperationQuery).wrapped();
            verify(getAsyncOperationTask).prepare(wrapped);

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenFirstGetItemNotNullAndTaskThrowExecuteException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(getAsyncOperationTask);

        GetAsyncOperationQuery getAsyncOperationQuery = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.getCanonicalName())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey)).thenReturn(getAsyncOperationQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(poolGuard.getObject()).thenReturn(connection);
        Key connectionKey = mock(Key.class);
        when(Keys.getOrAdd(StorageConnection.class.getCanonicalName())).thenReturn(connectionKey);
        when(IOC.resolve(connectionKey, connection)).thenReturn(connection);

        IObject wrapped = mock(IObject.class);
        when(getAsyncOperationQuery.wrapped()).thenReturn(wrapped);

        doThrow(new TaskExecutionException("")).when(getAsyncOperationTask).execute();

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {

            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic();
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verify(getAsyncOperationQuery).setCollectionName(collectionName);
            verify(getAsyncOperationQuery).setToken(token);
            verify(poolGuard).getObject();
            verify(getAsyncOperationTask).setConnection(connection);
            verify(getAsyncOperationQuery).wrapped();
            verify(getAsyncOperationTask).prepare(wrapped);
            verify(getAsyncOperationTask).execute();

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenFirstGetItemNullAndNewSingleTonThrowException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(null);

        Key nestedTaskKey = mock(Key.class);
        when(Keys.getOrAdd(IDatabaseTask.class.getCanonicalName())).thenReturn(nestedTaskKey);

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        when(IOC.resolve(nestedTaskKey, GetAsyncOperationTask.class.getCanonicalName())).thenReturn(nestedTask);

        whenNew(GetAsyncOperationTask.class).withArguments(nestedTask).thenReturn(getAsyncOperationTask);

        whenNew(SingletonStrategy.class).withArguments(getAsyncOperationTask).thenThrow(new InvalidArgumentException(""));

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {

            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic(times(2));
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verifyNew(SingletonStrategy.class).withArguments(getAsyncOperationTask);

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenFirstGetItemNullAndIOCRegisterThrowException() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(null);

        Key nestedTaskKey = mock(Key.class);
        when(Keys.getOrAdd(IDatabaseTask.class.getCanonicalName())).thenReturn(nestedTaskKey);

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        when(IOC.resolve(nestedTaskKey, GetAsyncOperationTask.class.getCanonicalName())).thenReturn(nestedTask);

        whenNew(GetAsyncOperationTask.class).withArguments(nestedTask).thenReturn(getAsyncOperationTask);

        SingletonStrategy singletonStrategy = mock(SingletonStrategy.class);
        whenNew(SingletonStrategy.class).withArguments(getAsyncOperationTask).thenReturn(singletonStrategy);

        doThrow(new RegistrationException("")).when(IOC.class, "register", getAsyncOperationTaskKey, singletonStrategy);

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {

            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic(times(2));
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verifyNew(SingletonStrategy.class).withArguments(getAsyncOperationTask);

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectGetAsyncOperationWhenFirstGetItemNullAndNestedTaskNull() throws
            Exception {

        String token = "token";

        PoolGuard poolGuard = mock(PoolGuard.class);
        whenNew(PoolGuard.class).withArguments(pool).thenReturn(poolGuard);

        GetAsyncOperationTask getAsyncOperationTask = mock(GetAsyncOperationTask.class);
        Key getAsyncOperationTaskKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName())).thenReturn(getAsyncOperationTaskKey);
        when(IOC.resolve(getAsyncOperationTaskKey)).thenReturn(null);

        Key nestedTaskKey = mock(Key.class);
        when(Keys.getOrAdd(IDatabaseTask.class.getCanonicalName())).thenReturn(nestedTaskKey);

        when(IOC.resolve(nestedTaskKey, GetAsyncOperationTask.class.getCanonicalName())).thenReturn(null);

        try {
            testCollection.getAsyncOperation(token);
        } catch (GetAsyncOperationException e) {

            verifyNew(PoolGuard.class).withArguments(pool);

            verifyStatic();
            Keys.getOrAdd(GetAsyncOperationTask.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(getAsyncOperationTaskKey);

            verify(poolGuard).close();
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }
}