package info.smart_tools.smartactors.actors.get_form;

import info.smart_tools.smartactors.actors.get_form.exception.GetFormActorException;
import info.smart_tools.smartactors.actors.get_form.strategy.FirstItemStrategy;
import info.smart_tools.smartactors.actors.get_form.strategy.IFormsStrategy;
import info.smart_tools.smartactors.actors.get_form.wrapper.GetFormMessage;
import info.smart_tools.smartactors.core.cached_collection.CachedCollection;
import info.smart_tools.smartactors.core.cached_collection.ICachedCollection;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(IOC.class)
@RunWith(PowerMockRunner.class)
public class GetFormActorTest {
    @Before
    public void setUp() throws ResolutionException {
        mockStatic(IOC.class);
    }

    @Test
    public void shouldSetItemFromCollectionToMessage() throws Exception {
        ICachedCollection collection = mock(ICachedCollection.class);
        IFormsStrategy strategy = new FirstItemStrategy();
        GetFormActor actor;
        List<IObject> objects = Collections.singletonList(mock(IObject.class));
        String key = "123";
        IField field = mock(IField.class);

        IKey collectionKey = mock(IKey.class);
        when(Keys.getOrAdd(CachedCollection.class.getCanonicalName())).thenReturn(collectionKey);
        when(IOC.resolve(eq(collectionKey), anyObject(), anyObject())).thenReturn(collection);

        IKey strategyKey = mock(IKey.class);
        when(Keys.getOrAdd(IFormsStrategy.class.getCanonicalName())).thenReturn(strategyKey);
        when(IOC.resolve(eq(strategyKey), anyObject())).thenReturn(strategy);

        IKey fieldKey = mock(IKey.class);
        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), anyObject())).thenReturn(field);

        when(collection.getItems(key)).thenReturn(objects);

        actor = new GetFormActor(mock(IObject.class));
        GetFormMessage message = mock(GetFormMessage.class);
        when(message.getFormKey()).thenReturn(key);
        actor.getForm(message);

        verify(message).setForm(objects.get(0));
    }

    @Test(expected = GetFormActorException.class)
    public void shouldThrowExceptionToMessageProcessor() throws Exception {
        ICachedCollection collection = mock(ICachedCollection.class);
        IFormsStrategy strategy = new FirstItemStrategy();
        GetFormActor actor;
        String key = "123";

        IKey collectionKey = mock(IKey.class);
        when(Keys.getOrAdd(CachedCollection.class.getCanonicalName())).thenReturn(collectionKey);
        when(IOC.resolve(eq(collectionKey), anyObject(), anyObject())).thenReturn(collection);

        IKey strategyKey = mock(IKey.class);
        when(Keys.getOrAdd(IFormsStrategy.class.getCanonicalName())).thenReturn(strategyKey);
        when(IOC.resolve(eq(strategyKey), anyObject())).thenReturn(strategy);

        when(collection.getItems(key)).thenThrow(Exception.class);

        actor = new GetFormActor(mock(IObject.class));
        GetFormMessage message = mock(GetFormMessage.class);
        when(message.getFormKey()).thenReturn(key);
        actor.getForm(message);
    }
}