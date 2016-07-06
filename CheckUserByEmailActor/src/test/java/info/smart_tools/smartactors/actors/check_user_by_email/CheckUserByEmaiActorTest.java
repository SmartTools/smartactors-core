package info.smart_tools.smartactors.actors.check_user_by_email;

import info.smart_tools.smartactors.actors.check_user_by_email.exception.NotFoundUserException;
import info.smart_tools.smartactors.actors.check_user_by_email.wrapper.ActorParams;
import info.smart_tools.smartactors.actors.check_user_by_email.wrapper.MessageWrapper;
import info.smart_tools.smartactors.core.cached_collection.CachedCollection;
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
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class CheckUserByEmaiActorTest {
    CheckUserByEmailActor actor;
    CachedCollection collection = mock(CachedCollection.class);

    @Before
    public void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey collectionKey = mock(IKey.class);
        when(Keys.getOrAdd(CachedCollection.class.toString())).thenReturn(collectionKey);
        when(IOC.resolve(collectionKey, "user")).thenReturn(collection);

        ActorParams params = mock(ActorParams.class);
        when(params.getCollectionName()).thenReturn("user");
        actor = new CheckUserByEmailActor(params);
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
