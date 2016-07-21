package info.smart_tools.smartactors.core.cached_collection;

import info.smart_tools.smartactors.core.cached_collection.exception.DeleteCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.GetCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.UpsertCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.task.DeleteFromCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.task.GetObjectFromCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.task.UpsertIntoCachedCollectionTask;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
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

import java.util.Collections;
import java.util.List;

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

    private IField collectionNameField;
    private IField keyNameField;
    private IField keyValueField;
    private IField specificKeyNameField;
    private IField documentField;
    private IField idField;
    private IField isActiveField;
    private IField searchResultField;

    @Before
    public void setUp() throws ReadValueException, ChangeValueException, InvalidArgumentException, PoolTakeException, ResolutionException {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IObject config = mock(IObject.class);

        collectionNameField = mock(IField.class);
        keyNameField = mock(IField.class);
        keyValueField = mock(IField.class);
        specificKeyNameField = mock(IField.class);
        documentField = mock(IField.class);
        idField = mock(IField.class);
        searchResultField = mock(IField.class);
        isActiveField = mock(IField.class);
        IField connectionPoolField = mock(IField.class);

        IKey mockKeyField = mock(IKey.class);
        when(Keys.getOrAdd(IField.class.toString())).thenReturn(mockKeyField);
        when(IOC.resolve(mockKeyField, "collectionName")).thenReturn(collectionNameField);
        when(IOC.resolve(mockKeyField, "connectionPool")).thenReturn(connectionPoolField);
        when(IOC.resolve(mockKeyField, "keyName")).thenReturn(keyNameField);
        when(IOC.resolve(mockKeyField, "searchResult")).thenReturn(searchResultField);
        when(IOC.resolve(mockKeyField, "keyValue")).thenReturn(keyValueField);
        when(IOC.resolve(mockKeyField, "document")).thenReturn(documentField);
        when(IOC.resolve(mockKeyField, "id")).thenReturn(idField);
        when(IOC.resolve(mockKeyField, "isActive")).thenReturn(isActiveField);

        String keyName = "customKeyName";
        when(keyNameField.in(config)).thenReturn(keyName);
        when(IOC.resolve(mockKeyField, keyName)).thenReturn(specificKeyNameField);

        IPool connectionPool = mock(IPool.class);
        connection = mock(StorageConnection.class);
        collectionName = mock(CollectionName.class);
        when(connectionPool.take()).thenReturn(connection);
        when(connectionPoolField.in(config)).thenReturn(connectionPool);
        when(collectionNameField.in(config)).thenReturn(collectionName);
        collection = new CachedCollection(config);

        IKey keyConnection = mock(IKey.class);
        when(Keys.getOrAdd(StorageConnection.class.toString())).thenReturn(keyConnection);
        when(IOC.resolve(keyConnection, connection)).thenReturn(connection);
    }

    @Test
    public void ShouldDeleteObject()
        throws DeleteCacheItemException, ResolutionException, TaskSetConnectionException, IllegalAccessException, ReadValueException,
        ChangeValueException, TaskPrepareException, TaskExecutionException, InvalidArgumentException {

        IObject query = mock(IObject.class);
        IObject deleteQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(deleteQuery);

        IDatabaseTask deleteTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(DeleteFromCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyTask)).thenReturn(deleteTask);

        when(specificKeyNameField.in(query)).thenReturn("key");

        collection.delete(query);

        verify(deleteTask).setConnection(eq(connection));
        verify(deleteTask).prepare(eq(deleteQuery));
        verify(deleteTask).execute();
        verify(collectionNameField).out(eq(deleteQuery), eq(collectionName));
        verify(documentField).out(eq(deleteQuery), eq(query));
    }

    @Test(expected = DeleteCacheItemException.class)
    public void ShouldThrowDeleteItemException_When_NestedTaskIsNull() throws DeleteCacheItemException {

        IObject query = mock(IObject.class);
        collection.delete(query);
    }

    @Test
    public void ShouldRegisterStrategyForDelete_When_TaskFacadeIsNull() throws Exception {

        IObject query = mock(IObject.class);

        IObject deleteQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(deleteQuery);
        when(specificKeyNameField.in(query)).thenReturn("key");

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        DeleteFromCachedCollectionTask deleteTask = mock(DeleteFromCachedCollectionTask.class);
        IKey keyIDBTask = mock(IKey.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(IDatabaseTask.class.toString())).thenReturn(keyIDBTask);
        when(Keys.getOrAdd(DeleteFromCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyIDBTask, DeleteFromCachedCollectionTask.class.toString())).thenReturn(nestedTask);
        whenNew(DeleteFromCachedCollectionTask.class).withArguments(nestedTask).thenReturn(deleteTask);

        collection.delete(query);

        verifyStatic();
        IOC.register(eq(keyTask), any(SingletonStrategy.class));

        verify(deleteTask).setConnection(eq(connection));
        verify(deleteTask).prepare(eq(deleteQuery));
        verify(deleteTask).execute();
        verify(specificKeyNameField).in(eq(query));
    }

    @Test
    public void ShouldUpsertObject() throws Exception {

        IObject query = mock(IObject.class);

        IObject upsertQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(upsertQuery);
        when(specificKeyNameField.in(query)).thenReturn("key");

        IDatabaseTask upsertTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyTask)).thenReturn(upsertTask);

        collection.upsert(query);

        verify(upsertTask).setConnection(eq(connection));
        verify(upsertTask).prepare(eq(upsertQuery));
        verify(upsertTask).execute();
        verify(specificKeyNameField).in(eq(query));
        verify(isActiveField).out(eq(query), eq(true));
        verify(collectionNameField).out(eq(upsertQuery), eq(collectionName));
        verify(documentField).out(eq(upsertQuery), eq(query));
    }

    @Test(expected = UpsertCacheItemException.class)
    public void ShouldSetPreviousActiveValue_When_ExecuteExceptionIsThrown() throws Exception {

        IObject query = mock(IObject.class);

        IObject upsertQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(upsertQuery);
        when(specificKeyNameField.in(query)).thenReturn("key");

        IDatabaseTask upsertTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyTask)).thenReturn(upsertTask);
        doThrow(new TaskExecutionException("")).when(upsertTask).execute();

        try {
            collection.upsert(query);
        } catch (UpsertCacheItemException e) {
            verify(upsertTask).setConnection(eq(connection));
            verify(upsertTask).prepare(upsertQuery);
            verify(upsertTask).execute();
            verify(specificKeyNameField, never()).in(eq(query));
            verify(isActiveField, times(1)).out(eq(query), eq(true));
            verify(isActiveField, times(1)).out(eq(query), eq(true));
            verify(collectionNameField).out(eq(upsertQuery), eq(collectionName));
            verify(documentField).out(eq(upsertQuery), eq(query));
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

        IObject upsertQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(upsertQuery);
        when(specificKeyNameField.in(query)).thenReturn("key");

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        UpsertIntoCachedCollectionTask upsertTask = mock(UpsertIntoCachedCollectionTask.class);
        IKey keyIDBTask = mock(IKey.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(IDatabaseTask.class.toString())).thenReturn(keyIDBTask);
        when(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyIDBTask, UpsertIntoCachedCollectionTask.class.toString())).thenReturn(nestedTask);
        whenNew(UpsertIntoCachedCollectionTask.class).withArguments(nestedTask).thenReturn(upsertTask);

        collection.upsert(query);

        verifyStatic();
        IOC.register(eq(keyTask), any(SingletonStrategy.class));

        verify(upsertTask).setConnection(eq(connection));
        verify(upsertTask).prepare(eq(upsertQuery));
        verify(upsertTask).execute();
        verify(specificKeyNameField).in(eq(query));
        verify(isActiveField).out(eq(query), eq(true));
        verify(collectionNameField).out(eq(upsertQuery), eq(collectionName));
        verify(documentField).out(eq(upsertQuery), eq(query));
    }

    @Test
    public void ShouldReadObject() throws Exception {

        IObject readQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(readQuery);

        IObject searchResult = mock(IObject.class);

        IDatabaseTask readTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(GetObjectFromCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyTask)).thenReturn(readTask);

        when(searchResultField.in(readQuery)).thenReturn(Collections.singletonList(searchResult));

        List<IObject> items = collection.getItems("key");

        verify(readTask).setConnection(eq(connection));
        verify(readTask).prepare(eq(readQuery));
        verify(readTask).execute();
        assertEquals(items.get(0), searchResult);
    }

    @Test(expected = GetCacheItemException.class)
    public void ShouldThrowGetItemException_When_NestedTaskIsNull() throws GetCacheItemException {

        collection.getItems("key");
    }

    @Test
    public void ShouldRegisterStrategyForGetItem_When_TaskFacadeIsNull() throws Exception {

        IObject readQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(readQuery);

        IDatabaseTask nestedTask = mock(IDatabaseTask.class);
        GetObjectFromCachedCollectionTask readTask = mock(GetObjectFromCachedCollectionTask.class);
        IKey keyIDBTask = mock(IKey.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getOrAdd(IDatabaseTask.class.toString())).thenReturn(keyIDBTask);
        when(Keys.getOrAdd(GetObjectFromCachedCollectionTask.class.toString())).thenReturn(keyTask);
        when(IOC.resolve(keyIDBTask, GetObjectFromCachedCollectionTask.class.toString())).thenReturn(nestedTask);
        whenNew(GetObjectFromCachedCollectionTask.class).withArguments(nestedTask).thenReturn(readTask);

        when(searchResultField.in(readQuery)).thenReturn(Collections.EMPTY_LIST);

        collection.getItems("key");

        verifyStatic();
        IOC.register(eq(keyTask), any(SingletonStrategy.class));

        verify(readTask).setConnection(eq(connection));
        verify(readTask).prepare(readQuery);
        verify(readTask).execute();
    }
}
