package info.smart_tools.smartactors.core.cached_collection;

import info.smart_tools.smartactors.core.cached_collection.exception.DeleteCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.GetCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.UpsertCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.task.DeleteFromCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.task.GetObjectFromCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.task.UpsertIntoCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.wrapper.CachedCollectionConfig;
import info.smart_tools.smartactors.core.cached_collection.wrapper.GetObjectFromCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.delete.DeleteFromCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.delete.DeleteItem;
import info.smart_tools.smartactors.core.cached_collection.wrapper.upsert.UpsertIntoCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.upsert.UpsertItem;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.ipool.exception.PoolTakeException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, CachedCollection.class})
public class CachedCollectionTest {

    private ICachedCollection collection;
    private StorageConnection connection;
    private CollectionName collectionName;

    @Before
    public void setUp() throws ReadValueException, ChangeValueException, InvalidArgumentException, PoolTakeException, ResolutionException {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IPool connectionPool = mock(IPool.class);
        connection = mock(StorageConnection.class);
        collectionName = mock(CollectionName.class);
        when(connectionPool.take()).thenReturn(connection);
        CachedCollectionConfig config = mock(CachedCollectionConfig.class);
        when(config.getConnectionPool()).thenReturn(connectionPool);
        when(config.getCollectionName()).thenReturn(collectionName);
        collection = new CachedCollection(config);

        IKey keyConnection = mock(IKey.class);
        when(Keys.getOrAdd(StorageConnection.class.toString())).thenReturn(keyConnection);
        when(IOC.resolve(keyConnection, connection)).thenReturn(connection);

    }

    @Test
    public void ShouldDeleteObject()
        throws DeleteCacheItemException, ResolutionException, TaskSetConnectionException, IllegalAccessException, ReadValueException,
                ChangeValueException, TaskPrepareException, TaskExecutionException {

        IObject query = mock(IObject.class);
        DeleteFromCachedCollectionQuery message = mock(DeleteFromCachedCollectionQuery.class);
        IObject wrapped = mock(IObject.class);
        when(message.wrapped()).thenReturn(wrapped);
        DeleteItem deleteItem = mock(DeleteItem.class);
        when(deleteItem.getKey()).thenReturn("key");
        when(deleteItem.getId()).thenReturn("id");
        when(message.getDeleteItem()).thenReturn(deleteItem);
        IKey keyMessage = mock(IKey.class);
        when(Keys.getOrAdd(DeleteFromCachedCollectionQuery.class.toString())).thenReturn(keyMessage);
        when(IOC.resolve(keyMessage)).thenReturn(message);

        IDatabaseTask deleteTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(DeleteFromCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyTask)).thenReturn(deleteTask);

        IKey keyItem = mock(IKey.class);
        when(Keys.getOrAdd(DeleteItem.class.toString())).thenReturn(keyItem);
        when(IOC.resolve(keyItem, query)).thenReturn(deleteItem);

        collection.delete(query);

        verify(deleteTask).setConnection(eq(connection));
        verify(deleteTask).prepare(eq(wrapped));
        verify(deleteTask).execute();
        verify(deleteItem).getKey();
    }

    @Test(expected = DeleteCacheItemException.class)
    public void ShouldThrowDeleteItemException_When_NestedTaskIsNull() throws DeleteCacheItemException {

        IObject query = mock(IObject.class);
        collection.delete(query);
    }

    @Test
    public void ShouldRegisterStrategyForDelete_When_TaskFacadeIsNull() throws Exception {

        IObject query = mock(IObject.class);

        DeleteFromCachedCollectionQuery message = mock(DeleteFromCachedCollectionQuery.class);
        IObject wrapped = mock(IObject.class);
        when(message.wrapped()).thenReturn(wrapped);
        DeleteItem deleteItem = mock(DeleteItem.class);
        when(deleteItem.getKey()).thenReturn("key");
        when(deleteItem.getId()).thenReturn("id");
        when(message.getDeleteItem()).thenReturn(deleteItem);
        IKey keyMessage = mock(IKey.class);
        when(Keys.getOrAdd(DeleteFromCachedCollectionQuery.class.toString())).thenReturn(keyMessage);
        when(IOC.resolve(keyMessage)).thenReturn(message);

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        DeleteFromCachedCollectionTask deleteTask = mock(DeleteFromCachedCollectionTask.class);
        IKey keyIDBTask = mock(IKey.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(IDatabaseTask.class.toString())).thenReturn(keyIDBTask);
        when(Keys.getOrAdd(DeleteFromCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyIDBTask, DeleteFromCachedCollectionTask.class.toString())).thenReturn(nestedTask);
        whenNew(DeleteFromCachedCollectionTask.class).withArguments(nestedTask).thenReturn(deleteTask);

        IKey keyItem = mock(IKey.class);
        when(Keys.getOrAdd(DeleteItem.class.toString())).thenReturn(keyItem);
        when(IOC.resolve(keyItem, query)).thenReturn(deleteItem);

        collection.delete(query);

        verifyStatic();
        IOC.register(eq(keyTask), any(SingletonStrategy.class));


        verify(deleteTask).setConnection(eq(connection));
        verify(deleteTask).prepare(eq(wrapped));
        verify(deleteTask).execute();
        verify(deleteItem).getKey();
    }

    @Test
    public void ShouldUpsertObject()
        throws ResolutionException, TaskSetConnectionException, IllegalAccessException, ReadValueException,
        ChangeValueException, TaskPrepareException, TaskExecutionException, UpsertCacheItemException {

        IObject query = mock(IObject.class);

        UpsertIntoCachedCollectionQuery message = mock(UpsertIntoCachedCollectionQuery.class);
        IObject wrapped = mock(IObject.class);
        when(message.wrapped()).thenReturn(wrapped);
        UpsertItem upsertItem = mock(UpsertItem.class);
        when(upsertItem.getKey()).thenReturn("key");
        when(upsertItem.getId()).thenReturn("id");
        when(message.getUpsertItem()).thenReturn(upsertItem);
        IKey keyMessage = mock(IKey.class);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionQuery.class.toString())).thenReturn(keyMessage);
        when(IOC.resolve(keyMessage)).thenReturn(message);

        IDatabaseTask upsertTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyTask)).thenReturn(upsertTask);

        IKey keyItem = mock(IKey.class);
        when(Keys.getOrAdd(UpsertItem.class.toString())).thenReturn(keyItem);
        when(IOC.resolve(keyItem, query)).thenReturn(upsertItem);

        collection.upsert(query);

        verify(message).setCollectionName(eq(collectionName));
        verify(message).setUpsertItem(eq(upsertItem));
        verify(upsertTask).setConnection(eq(connection));
        verify(upsertTask).prepare(eq(wrapped));
        verify(upsertTask).execute();
        verify(upsertItem).getKey();
        verify(upsertItem).setIsActive(true);
    }

    @Test(expected = UpsertCacheItemException.class)
    public void ShouldSetPreviousActiveValue_When_ExecuteExceptionIsThrown()
        throws ResolutionException, TaskSetConnectionException, IllegalAccessException, ReadValueException,
        ChangeValueException, TaskPrepareException, TaskExecutionException, UpsertCacheItemException {

        IObject query = mock(IObject.class);

        UpsertIntoCachedCollectionQuery message = mock(UpsertIntoCachedCollectionQuery.class);
        IObject wrapped = mock(IObject.class);
        when(message.wrapped()).thenReturn(wrapped);
        UpsertItem upsertItem = mock(UpsertItem.class);
        when(upsertItem.getKey()).thenReturn("key");
        when(upsertItem.getId()).thenReturn("id");
        when(upsertItem.isActive()).thenReturn(false);
        when(message.getUpsertItem()).thenReturn(upsertItem);
        IKey keyMessage = mock(IKey.class);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionQuery.class.toString())).thenReturn(keyMessage);
        when(IOC.resolve(keyMessage)).thenReturn(message);

        IDatabaseTask upsertTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyTask)).thenReturn(upsertTask);
        doThrow(new TaskExecutionException("")).when(upsertTask).execute();

        IKey keyItem = mock(IKey.class);
        when(Keys.getOrAdd(UpsertItem.class.toString())).thenReturn(keyItem);
        when(IOC.resolve(keyItem, query)).thenReturn(upsertItem);

        try {
            collection.upsert(query);
        } catch (UpsertCacheItemException e) {
            verify(message).setCollectionName(eq(collectionName));
            verify(message).setUpsertItem(eq(upsertItem));
            verify(upsertTask).setConnection(eq(connection));
            verify(upsertTask).prepare(eq(wrapped));
            verify(upsertTask).execute();
            verify(upsertItem, never()).getKey();
            verify(upsertItem, times(1)).setIsActive(true);
            verify(upsertItem, times(1)).setIsActive(false);
            throw e;
        }
        fail();

    }

    @Test(expected = UpsertCacheItemException.class)
    public void ShouldThrowUpsertItemException_When_NestedTaskIsNull() throws UpsertCacheItemException {

        IObject query = mock(IObject.class);
        collection.upsert(query);
    }

    @Test
    public void ShouldRegisterStrategyForUpsert_When_TaskFacadeIsNull() throws Exception {

        IObject query = mock(IObject.class);

        UpsertIntoCachedCollectionQuery message = mock(UpsertIntoCachedCollectionQuery.class);
        IObject wrapped = mock(IObject.class);
        when(message.wrapped()).thenReturn(wrapped);
        UpsertItem upsertItem = mock(UpsertItem.class);
        when(upsertItem.getKey()).thenReturn("key");
        when(upsertItem.getId()).thenReturn("id");
        when(message.getUpsertItem()).thenReturn(upsertItem);
        IKey keyMessage = mock(IKey.class);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionQuery.class.toString())).thenReturn(keyMessage);
        when(IOC.resolve(keyMessage)).thenReturn(message);

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        UpsertIntoCachedCollectionTask upsertTask = mock(UpsertIntoCachedCollectionTask.class);
        IKey keyIDBTask = mock(IKey.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(IDatabaseTask.class.toString())).thenReturn(keyIDBTask);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyIDBTask, UpsertIntoCachedCollectionTask.class.toString())).thenReturn(nestedTask);
        whenNew(UpsertIntoCachedCollectionTask.class).withArguments(nestedTask).thenReturn(upsertTask);

        IKey keyItem = mock(IKey.class);
        when(Keys.getOrAdd(UpsertItem.class.toString())).thenReturn(keyItem);
        when(IOC.resolve(keyItem, query)).thenReturn(upsertItem);

        collection.upsert(query);

        verifyStatic();
        IOC.register(eq(keyTask), any(SingletonStrategy.class));

        verify(message).setCollectionName(eq(collectionName));
        verify(message).setUpsertItem(eq(upsertItem));
        verify(upsertTask).setConnection(eq(connection));
        verify(upsertTask).prepare(eq(wrapped));
        verify(upsertTask).execute();
        verify(upsertItem).getKey();
        verify(upsertItem).setIsActive(true);
    }

    @Test
    public void ShouldReadObject()
        throws ResolutionException, TaskSetConnectionException, IllegalAccessException, ReadValueException,
        ChangeValueException, TaskPrepareException, TaskExecutionException, GetCacheItemException {

        GetObjectFromCachedCollectionQuery message = mock(GetObjectFromCachedCollectionQuery.class);

        IObject wrapped = mock(IObject.class);
        when(message.wrapped()).thenReturn(wrapped);

        when(message.getKey()).thenReturn("key");
        IObject searchResult = mock(IObject.class);
        when(message.getSearchResult()).thenReturn(Stream.of(searchResult));
        IKey keyMessage = mock(IKey.class);
        when(Keys.getOrAdd(GetObjectFromCachedCollectionQuery.class.toString())).thenReturn(keyMessage);
        when(IOC.resolve(keyMessage)).thenReturn(message);

        IDatabaseTask readTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(GetObjectFromCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyTask)).thenReturn(readTask);

        List<IObject> items = collection.getItems("key");

        verify(readTask).setConnection(eq(connection));
        verify(readTask).prepare(eq(wrapped));
        verify(readTask).execute();
        assertEquals(items.get(0), searchResult);
    }

    @Test(expected = GetCacheItemException.class)
    public void ShouldThrowGetItemException_When_NestedTaskIsNull() throws GetCacheItemException {

        collection.getItems("key");
    }

    @Test
    public void ShouldRegisterStrategyForGetItem_When_TaskFacadeIsNull() throws Exception {

        GetObjectFromCachedCollectionQuery message = mock(GetObjectFromCachedCollectionQuery.class);

        IObject wrapped = mock(IObject.class);
        when(message.wrapped()).thenReturn(wrapped);

        when(message.getKey()).thenReturn("key");
        IObject searchResult = mock(IObject.class);
        when(message.getSearchResult()).thenReturn(Stream.of(searchResult));
        IKey keyMessage = mock(IKey.class);
        when(Keys.getOrAdd(GetObjectFromCachedCollectionQuery.class.toString())).thenReturn(keyMessage);
        when(IOC.resolve(keyMessage)).thenReturn(message);

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        GetObjectFromCachedCollectionTask readTask = mock(GetObjectFromCachedCollectionTask.class);
        IKey keyIDBTask = mock(IKey.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(IDatabaseTask.class.toString())).thenReturn(keyIDBTask);
        when(Keys.getOrAdd(GetObjectFromCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyIDBTask, GetObjectFromCachedCollectionTask.class.toString())).thenReturn(nestedTask);
        whenNew(GetObjectFromCachedCollectionTask.class).withArguments(nestedTask).thenReturn(readTask);

        collection.getItems("key");

        verifyStatic();
        IOC.register(eq(keyTask), any(SingletonStrategy.class));

        verify(readTask).setConnection(eq(connection));
        verify(readTask).prepare(eq(wrapped));
        verify(readTask).execute();
    }
}
