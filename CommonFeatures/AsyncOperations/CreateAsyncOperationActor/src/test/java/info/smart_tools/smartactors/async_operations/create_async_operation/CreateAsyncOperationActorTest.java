package info.smart_tools.smartactors.async_operations.create_async_operation;

import info.smart_tools.smartactors.async_operations.create_async_operation.exception.CreateAsyncOperationActorException;
import info.smart_tools.smartactors.async_operations.create_async_operation.wrapper.CreateAsyncOperationMessage;
import info.smart_tools.smartactors.database.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.database.async_operation_collection.exception.CreateAsyncOperationException;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.format.DateTimeFormatter;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class CreateAsyncOperationActorTest {

    private CreateAsyncOperationActor actor;
    private CreateAsyncOperationMessage message;
    private IAsyncOperationCollection collection;

    @Before
    public void setUp() throws Exception {
        String databaseOptionsKey = "key";
        Object databaseOptions = PowerMockito.mock(Object.class);
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey formatterKey = mock(IKey.class);
        when(Keys.getKeyByName("datetime_formatter")).thenReturn(formatterKey);
        when(IOC.resolve(eq(formatterKey))).thenReturn(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"));

        collection = mock(IAsyncOperationCollection.class);
        IKey collectionKey = mock(IKey.class);

        when(Keys.getKeyByName(IAsyncOperationCollection.class.getCanonicalName())).thenReturn(collectionKey);
        when(IOC.resolve(eq(collectionKey), any(), any())).thenReturn(collection);

        IField collectionNameField = mock(IField.class);
        IField databaseOptionsF = PowerMockito.mock(IField.class);
        IKey collectionNameFieldKey = mock(IKey.class);

        when(Keys.getKeyByName(IField.class.getCanonicalName())).thenReturn(collectionNameFieldKey);
        when(IOC.resolve(collectionNameFieldKey, "collectionName")).thenReturn(collectionNameField);
        when(IOC.resolve(collectionNameFieldKey, "databaseOptions")).thenReturn(databaseOptionsF);
        when(databaseOptionsF.in(any())).thenReturn(databaseOptionsKey);
        when(IOC.resolve(Keys.getKeyByName(databaseOptionsKey))).thenReturn(databaseOptions);

        actor = new CreateAsyncOperationActor(mock(IObject.class));
        message = mock(CreateAsyncOperationMessage.class);
    }

    @Test
    public void ShouldCreateOperationAndSetToken()
        throws ReadValueException, CreateAsyncOperationActorException, ChangeValueException, ResolutionException,
                CreateAsyncOperationException {

        String sessionId = "sessionId";
        when(IOC.resolve(Keys.getKeyByName("db.collection.nextid"))).thenReturn("");
        when(message.getSessionId()).thenReturn(sessionId);
        when(message.getExpiredTime()).thenReturn(4);

        IObject asyncDataObj = mock(IObject.class);
        IKey dataKey = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(dataKey);
        when(message.getOperationData()).thenReturn(asyncDataObj);

        actor.create(message);

        verify(collection).createAsyncOperation(eq(asyncDataObj), anyString(), anyString());
        verify(message).setAsyncOperationToken(anyString());
    }

    @Test(expected = CreateAsyncOperationActorException.class)
    public void ShouldThrowException_When_InternalErrorIsOccured()
        throws ReadValueException, CreateAsyncOperationActorException, ChangeValueException, ResolutionException,
        CreateAsyncOperationException {

        String sessionId = "sessionId";
        when(IOC.resolve(Keys.getKeyByName("db.collection.nextid"))).thenReturn("");
        when(message.getSessionId()).thenReturn(sessionId);
        when(message.getExpiredTime()).thenReturn(4);

        IObject asyncDataObj = mock(IObject.class);
        IKey dataKey = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(dataKey);
        when(message.getOperationData()).thenReturn(asyncDataObj);

        doThrow(new CreateAsyncOperationException("exception")).when(collection).createAsyncOperation(any(), any(), any());

        actor.create(message);
        fail();
    }


}
