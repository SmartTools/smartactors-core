package info.smart_tools.smartactors.actors.create_user;

import info.smart_tools.smartactors.actors.create_user.wrapper.ActorParams;
import info.smart_tools.smartactors.actors.create_user.wrapper.MessageWrapper;
import info.smart_tools.smartactors.core.cached_collection.ICachedCollection;
import info.smart_tools.smartactors.core.cached_collection.exception.UpsertCacheItemException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class CreateUserActorTest {

    private CreateUserActor actor;
    private MessageWrapper message;
    private ICachedCollection collection;

    @Before
    public void setUp() throws Exception {

        collection = mock(ICachedCollection.class);
        IKey cachedCollectionKey = mock(IKey.class);

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        String collectionName = "user";
        String collectionKeyName = "key";

        ActorParams params = mock(ActorParams.class);
        when(params.getCollectionName()).thenReturn(collectionName);
        when(params.getCollectionKey()).thenReturn(collectionKeyName);

        when(Keys.getOrAdd(ICachedCollection.class.toString())).thenReturn(cachedCollectionKey);
        when(IOC.resolve(
                cachedCollectionKey,
                collectionName,
                collectionKeyName)
        ).thenReturn(collection);

        actor = new CreateUserActor(params);
        message = mock(MessageWrapper.class);
    }

    @Test
    public void ShouldCreateUser() throws Exception {
        IObject user = mock(IObject.class);
        when(message.getUser()).thenReturn(user);
        actor.create(message);
        verify(collection).upsert(user);
    }

    @Test(expected = TaskExecutionException.class)
    public void ShouldThrowException_When_InternalErrorIsOccured() throws Exception {
        IObject user = mock(IObject.class);
        when(message.getUser()).thenReturn(user);
        doThrow(new UpsertCacheItemException("exception")).when(collection).upsert(any());
        actor.create(message);
        fail();
    }

}
