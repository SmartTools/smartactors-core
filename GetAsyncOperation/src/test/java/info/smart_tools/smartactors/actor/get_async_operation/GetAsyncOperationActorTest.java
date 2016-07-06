package info.smart_tools.smartactors.actor.get_async_operation;

import info.smart_tools.smartactors.actor.get_async_operation.exception.GetAsyncOperationActorException;
import info.smart_tools.smartactors.actor.get_async_operation.wrapper.GetAsyncOperationMessage;
import info.smart_tools.smartactors.core.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.core.async_operation_collection.exception.GetAsyncOperationException;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class GetAsyncOperationActorTest {

    private GetAsyncOperationActor actor;
    private GetAsyncOperationMessage message;
    private IAsyncOperationCollection collection;

    @Before
    public void setUp() throws ResolutionException {

        collection = mock(IAsyncOperationCollection.class);
        IKey collectionKey = mock(IKey.class);

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        when(Keys.getOrAdd(IAsyncOperationCollection.class.toString())).thenReturn(collectionKey);
        when(IOC.resolve(collectionKey)).thenReturn(collection);

        message = mock(GetAsyncOperationMessage.class);
        actor = new GetAsyncOperationActor(mock(IObject.class));
    }

    @Test
    public void ShouldReadOperationByToken()
        throws ReadValueException, GetAsyncOperationActorException, GetAsyncOperationException, ChangeValueException {

        String token = "token";
        when(message.getToken()).thenReturn(token);
        IObject asyncOperation = mock(IObject.class);
        when(collection.getAsyncOperation(token)).thenReturn(asyncOperation);

        actor.getOperation(message);

        verify(message).setAsyncOperation(asyncOperation);
    }

    @Test(expected = GetAsyncOperationActorException.class)
    public void ShouldThrowException_When_AsyncOperationIsNull()
        throws ReadValueException, GetAsyncOperationActorException, GetAsyncOperationException, ChangeValueException {

        when(collection.getAsyncOperation(anyString())).thenReturn(null);
        actor.getOperation(message);

        fail();
    }
}
