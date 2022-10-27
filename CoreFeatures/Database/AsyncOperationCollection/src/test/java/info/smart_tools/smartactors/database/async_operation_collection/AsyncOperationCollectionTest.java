package info.smart_tools.smartactors.database.async_operation_collection;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.ipool.exception.GettingFromPoolException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.CompleteAsyncOperationException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.DeleteAsyncOperationException;
import info.smart_tools.smartactors.database.async_operation_collection.exception.GetAsyncOperationException;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, AsyncOperationCollection.class})
public class AsyncOperationCollectionTest {

    private IAsyncOperationCollection collection;
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
        when(IOC.resolve(mockKeyField, "keyValue")).thenReturn(keyValueField);
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
        collection = new AsyncOperationCollection(connectionPool, "async_operation");

        IKey keyConnection = mock(IKey.class);
        when(IOC.resolve(keyConnection, connection)).thenReturn(connection);
    }

    @Test
    public void ShouldDeleteObject() throws Exception {

        String token = mock(String.class);
        IObject deleteQuery = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(deleteQuery);

        IDatabaseTask deleteTask = mock(IDatabaseTask.class);
        IKey keyTask = mock(IKey.class);
        when(Keys.getKeyByName("db.async_ops_collection.delete")).thenReturn(keyTask);
        when(IOC.resolve(eq(keyTask), any(), any(), eq(token))).thenReturn(deleteTask);
        collection.delete(token);

        verify(deleteTask).execute();
    }

    @Test(expected = DeleteAsyncOperationException.class)
    public void ShouldThrowDeleteItemException_When_NestedExceptionIsOccurred() throws Exception {

        String query = mock(String.class);
        when(IOC.resolve(any(), any(), any(), any())).thenThrow(new ResolutionException(""));
        collection.delete(query);
    }


    @Test(expected = CompleteAsyncOperationException.class)
    public void ShouldThrowUpsertItemException_When_NestedErrorIsOccurred() throws Exception {

        IObject query = mock(IObject.class);
        when(IOC.resolve(any(), any(), any(), any())).thenThrow(new ResolutionException(""));
        collection.complete(query);
    }

    @Test(expected = GetAsyncOperationException.class)
    public void ShouldThrowGetItemException_When_NestedTaskIsNull() throws Exception {

        when(IOC.resolve(any(), any(), any(), any(), any())).thenThrow(new ResolutionException(""));
        collection.getAsyncOperation("key");
    }
}
