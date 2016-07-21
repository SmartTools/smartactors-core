package info.smart_tools.smartactors.actors.check_user_by_email;

import info.smart_tools.smartactors.actors.check_user_by_email.exception.NotFoundUserException;
import info.smart_tools.smartactors.actors.check_user_by_email.wrapper.ActorParams;
import info.smart_tools.smartactors.actors.check_user_by_email.wrapper.MessageWrapper;
import info.smart_tools.smartactors.core.cached_collection.ICachedCollection;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class CheckUserByEmaiActorTest {
    CheckUserByEmailActor actor;
    ICachedCollection collection = mock(ICachedCollection.class);

    @Before
    public void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        String collectionName = "name";
        String collectionKeyName = "key";

        ActorParams params = mock(ActorParams.class);
        when(params.getCollectionName()).thenReturn(collectionName);
        when(params.getCollectionKey()).thenReturn(collectionKeyName);

        IKey iCachedCollectionKey = mock(IKey.class);
        when(Keys.getOrAdd(ICachedCollection.class.toString())).thenReturn(iCachedCollectionKey);
        when(IOC.resolve(iCachedCollectionKey, collectionName, collectionKeyName)).thenReturn(collection);

        actor = new CheckUserByEmailActor(params);

        verifyStatic();
        Keys.getOrAdd(ICachedCollection.class.toString());

        verify(params).getCollectionName();
        verify(params).getCollectionKey();

        verifyStatic();
        IOC.resolve(iCachedCollectionKey, collectionName, collectionKeyName);

    }

    @Test
    public void shouldSetUserToMessage() throws Exception {
        IObject user = mock(IObject.class);
        MessageWrapper message = mock(MessageWrapper.class);
        when(message.getEmail()).thenReturn("email");
        when(collection.getItems("email")).thenReturn(Collections.singletonList(user));
        actor.checkUser(message);
        verify(message).setUser(eq(user));
    }

    @Test(expected = NotFoundUserException.class)
    public void shouldThrowExceptionWhenCountOfUsersIsNotOne() throws Exception {
        IObject user1 = mock(IObject.class);
        IObject user2 = mock(IObject.class);
        MessageWrapper message = mock(MessageWrapper.class);
        when(message.getEmail()).thenReturn("email");
        when(collection.getItems("email")).thenReturn(Arrays.asList(user1, user2));
        actor.checkUser(message);
    }
}
