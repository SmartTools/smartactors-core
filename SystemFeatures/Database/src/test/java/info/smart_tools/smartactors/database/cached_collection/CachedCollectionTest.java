package info.smart_tools.smartactors.database.cached_collection;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.ipool.exception.GettingFromPoolException;
import info.smart_tools.smartactors.database.cached_collection.exception.DeleteCacheItemException;
import info.smart_tools.smartactors.database.cached_collection.exception.GetCacheItemException;
import info.smart_tools.smartactors.database.cached_collection.exception.UpsertCacheItemException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, CachedCollection.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CachedCollectionTest {

    private ICachedCollection collection;
    private IStorageConnection connection;
    private String collectionName;

    private IField collectionNameField;
    private IField keyNameField;
    private IField keyValueField;
    private IField specificKeyNameField;
    private IField documentField;
    private IField idField;
    private IField isActiveField;
    private IField searchResultField;

    @Before
    public void setUp() throws ReadValueException, ChangeValueException, InvalidArgumentException, GettingFromPoolException, ResolutionException {

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
        when(Keys.getKeyByName(IField.class.getCanonicalName())).thenReturn(mockKeyField);
        when(IOC.resolve(mockKeyField, "collectionName")).thenReturn(collectionNameField);
        when(IOC.resolve(mockKeyField, "connectionPool")).thenReturn(connectionPoolField);
        when(IOC.resolve(mockKeyField, "keyName")).thenReturn(keyNameField);
        when(IOC.resolve(mockKeyField, "searchResult")).thenReturn(searchResultField);
        when(IOC.resolve(mockKeyField, "key")).thenReturn(keyValueField);
        when(IOC.resolve(mockKeyField, "document")).thenReturn(documentField);
        when(IOC.resolve(mockKeyField, "id")).thenReturn(idField);
        when(IOC.resolve(mockKeyField, "isActive")).thenReturn(isActiveField);

        String keyName = "customKeyName";
        when(keyNameField.in(config)).thenReturn(keyName);
        when(IOC.resolve(mockKeyField, keyName)).thenReturn(specificKeyNameField);

        IPool connectionPool = mock(IPool.class);
        connection = mock(IStorageConnection.class);
        collectionName = mock(String.class);
        when(connectionPool.get()).thenReturn(connection);
        when(connectionPoolField.in(config)).thenReturn(connectionPool);
        when(collectionNameField.in(config)).thenReturn(collectionName);
        collection = new CachedCollection(config);

        IKey keyConnection = mock(IKey.class);
        when(IOC.resolve(keyConnection, connection)).thenReturn(connection);
    }

    @Test
    public void ShouldDeleteObject() throws Exception {

        IObject query = mock(IObject.class);
        IObject deleteQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(deleteQuery);

        IDatabaseTask deleteTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getKeyByName("db.cached_collection.delete")).thenReturn(keyTask);
        when(IOC.resolve(eq(keyTask), any(), eq(collectionName), eq(query))).thenReturn(deleteTask);

        when(specificKeyNameField.in(query)).thenReturn("key");

        collection.delete(query);

        verify(deleteTask).execute();
    }

    @Test(expected = DeleteCacheItemException.class)
    public void ShouldThrowDeleteItemException_When_NestedExceptionIsOccurred() throws Exception {

        IObject query = mock(IObject.class);
        when(IOC.resolve(any(), any(), any(), any())).thenThrow(new ResolutionException(""));
        collection.delete(query);
    }

    @Test
    public void ShouldUpsertObject() throws Exception {

        IObject query = mock(IObject.class);

        IObject upsertQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(upsertQuery);
        when(specificKeyNameField.in(query)).thenReturn("key");

        IDatabaseTask upsertTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getKeyByName("db.cached_collection.upsert")).thenReturn(keyTask);
        when(IOC.resolve(eq(keyTask), any(), eq(collectionName), eq(query))).thenReturn(upsertTask);

        collection.upsert(query);

        verify(upsertTask).execute();
        verify(specificKeyNameField).in(eq(query));
        verify(isActiveField).out(eq(query), eq(true));
    }

    @Test(expected = UpsertCacheItemException.class)
    public void ShouldSetPreviousActiveValue_When_ExecuteExceptionIsThrown() throws Exception {

        IObject query = mock(IObject.class);

        IObject upsertQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(upsertQuery);
        when(specificKeyNameField.in(query)).thenReturn("key");

        IDatabaseTask upsertTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getKeyByName("db.cached_collection.upsert")).thenReturn(keyTask);
        when(IOC.resolve(eq(keyTask), any(), eq(collectionName), eq(query))).thenReturn(upsertTask);
        doThrow(new TaskExecutionException("")).when(upsertTask).execute();

        try {
            collection.upsert(query);
        } catch (UpsertCacheItemException e) {
            verify(upsertTask).execute();
            verify(specificKeyNameField, never()).in(eq(query));
            verify(isActiveField, times(1)).out(eq(query), eq(true));
            verify(isActiveField, times(1)).out(eq(query), eq(true));
            throw e;
        }
        fail();
    }

    @Test(expected = UpsertCacheItemException.class)
    public void ShouldThrowUpsertItemException_When_NestedErrorIsOccurred() throws Exception {

        IObject query = mock(IObject.class);
        when(IOC.resolve(any(), any(), any(), any())).thenThrow(new ResolutionException(""));
        collection.upsert(query);
    }

    @Test
    public void ShouldReadObject() throws Exception {

        IObject readQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(readQuery);

        final IObject searchResult = mock(IObject.class);

        IDatabaseTask readTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getKeyByName("db.cached_collection.get_item")).thenReturn(keyTask);
        final IAction[] callback = {mock(IAction.class)};
        doAnswer(invocation -> {
            callback[0] = (IAction) invocation.getArguments()[5];
            return readTask;
        }).when(IOC.class);
        IOC.resolve(eq(keyTask), any(), eq(collectionName), any(), any(), any(IAction.class));
        doAnswer(invocation -> {
            callback[0].execute(new IObject[] {searchResult});
            return null;
        }).when(readTask).execute();

        when(searchResultField.in(readQuery)).thenReturn(Collections.singletonList(searchResult));

        List<IObject> items = collection.getItems("key");

        verify(readTask).execute();
    }

    @Test
    public void ShouldClearCache() throws Exception {

        IObject readQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(readQuery);

        final IObject searchResult = mock(IObject.class);

        IDatabaseTask readTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getKeyByName("db.cached_collection.get_item")).thenReturn(keyTask);
        final IAction[] callback = {mock(IAction.class)};
        doAnswer(invocation -> {
            callback[0] = (IAction) invocation.getArguments()[5];
            return readTask;
        }).when(IOC.class);
        IOC.resolve(eq(keyTask), any(), eq(collectionName), any(), any(), any(IAction.class));
        doAnswer(invocation -> {
            callback[0].execute(new IObject[] {searchResult});
            return null;
        }).when(readTask).execute();

        when(searchResultField.in(readQuery)).thenReturn(Collections.singletonList(searchResult));

        collection.getItems("key");
        collection.getItems("key");

        verify(readTask, times(1)).execute();

        collection.clearCache();
        collection.getItems("key");

        verify(readTask, times(2)).execute();
    }

    @Test(expected = GetCacheItemException.class)
    public void ShouldThrowGetItemException_When_NestedTaskIsNull() throws Exception {

        when(IOC.resolve(any(), any(), any(), any(), any(), any())).thenThrow(new ResolutionException(""));
        collection.getItems("key");
    }
}
