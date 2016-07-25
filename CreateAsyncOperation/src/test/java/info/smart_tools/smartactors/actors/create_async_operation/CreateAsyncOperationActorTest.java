package info.smart_tools.smartactors.actors.create_async_operation;

import info.smart_tools.smartactors.actors.create_async_operation.exception.CreateAsyncOperationActorException;
import info.smart_tools.smartactors.actors.create_async_operation.wrapper.AuthOperationData;
import info.smart_tools.smartactors.actors.create_async_operation.wrapper.CreateAsyncOperationMessage;
import info.smart_tools.smartactors.core.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.core.async_operation_collection.exception.CreateAsyncOperationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class CreateAsyncOperationActorTest {

    private CreateAsyncOperationActor actor;
    private CreateAsyncOperationMessage message;
    private IAsyncOperationCollection collection;
    private AuthOperationData data;

    @Before
    public void setUp() throws ResolutionException {

        collection = mock(IAsyncOperationCollection.class);
        IKey collectionKey = mock(IKey.class);

        data = mock(AuthOperationData.class);
        IKey dataKey = mock(IKey.class);

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        when(Keys.getOrAdd(IAsyncOperationCollection.class.getCanonicalName())).thenReturn(collectionKey);
        when(IOC.resolve(collectionKey)).thenReturn(collection);

        when(Keys.getOrAdd(AuthOperationData.class.getCanonicalName())).thenReturn(dataKey);
        when(IOC.resolve(dataKey)).thenReturn(data);

        actor = new CreateAsyncOperationActor(mock(IObject.class));
        message = mock(CreateAsyncOperationMessage.class);
    }

    @Test
    public void ShouldCreateOperationAndSetToken()
        throws ReadValueException, CreateAsyncOperationActorException, ChangeValueException, ResolutionException,
                CreateAsyncOperationException {

        String sessionId = "sessionId";
        when(message.getSessionId()).thenReturn(sessionId);
        when(message.getExpiredTime()).thenReturn(4L);

        IObject asyncDataObj = mock(IObject.class);
        IKey dataKey = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(dataKey);
        when(IOC.resolve(dataKey, data)).thenReturn(asyncDataObj);

        actor.create(message);

        verify(data).setSessionId(sessionId);
        verify(collection).createAsyncOperation(eq(asyncDataObj), anyString(), anyString());
        verify(message).setAsyncOperationToken(anyString());
    }

    @Test(expected = CreateAsyncOperationActorException.class)
    public void ShouldThrowException_When_InternalErrorIsOccured()
        throws ReadValueException, CreateAsyncOperationActorException, ChangeValueException, ResolutionException,
        CreateAsyncOperationException {

        IObject asyncDataObj = mock(IObject.class);
        IKey dataKey = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(dataKey);
        when(IOC.resolve(dataKey, data)).thenReturn(asyncDataObj);
        doThrow(new CreateAsyncOperationException("exception")).when(collection).createAsyncOperation(any(), any(), any());

        actor.create(message);
        fail();
    }


}
