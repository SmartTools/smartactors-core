package info.smart_tools.smartactors.actors.get_async_operation;

import info.smart_tools.smartactors.actors.get_async_operation.exception.GetAsyncOperationActorException;
import info.smart_tools.smartactors.actors.get_async_operation.wrapper.GetAsyncOperationMessage;
import info.smart_tools.smartactors.core.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.core.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.core.ifield.IField;
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
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
    public void setUp() throws ResolutionException, GetAsyncOperationActorException {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        collection = Mockito.mock(IAsyncOperationCollection.class);
        IKey collectionKey = Mockito.mock(IKey.class);

        when(Keys.getOrAdd(IAsyncOperationCollection.class.getCanonicalName())).thenReturn(collectionKey);
        when(IOC.resolve(eq(collectionKey), any())).thenReturn(collection);

        IField collectionNameField = Mockito.mock(IField.class);
        IKey collectionNameFieldKey = Mockito.mock(IKey.class);

        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(collectionNameFieldKey);
        when(IOC.resolve(collectionNameFieldKey, "collectionName")).thenReturn(collectionNameField);

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

    @Test(expected = GetAsyncOperationActorException.class)
    public void Should_ThrowException_When_OperationIdIsNull() throws ReadValueException, GetAsyncOperationActorException {
        when(message.getToken()).thenReturn(null);
        actor.getOperation(message);
    }

    @Test(expected = GetAsyncOperationActorException.class)
    public void Should_ThrowException_When_OperationIdIsEmpty() throws ReadValueException, GetAsyncOperationActorException {
        when(message.getToken()).thenReturn("");
        actor.getOperation(message);
    }
}
