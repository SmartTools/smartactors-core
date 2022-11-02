package info.smart_tools.smartactors.async_operations_plugins.close_async_operation_actor_plugin;

import info.smart_tools.smartactors.async_operations.close_async_operation.CloseAsyncOperationActor;
import info.smart_tools.smartactors.async_operations.close_async_operation.wrapper.ActorParams;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, CloseAsyncOperationActorPlugin.class, CreateNewInstanceStrategy.class})
@RunWith(PowerMockRunner.class)
public class CloseAsyncOperationActorPluginTest {
    private CloseAsyncOperationActorPlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new CloseAsyncOperationActorPlugin(bootstrap);
    }

    @Test
    public void MustCorrectLoadPlugin() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

       // verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey checkUserByEmailActorKey = mock(IKey.class);
        when(Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName())).thenReturn(checkUserByEmailActorKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName());

        ArgumentCaptor<CreateNewInstanceStrategy> createNewInstanceStrategyArgumentCaptor = ArgumentCaptor.forClass(CreateNewInstanceStrategy.class);

        verifyStatic();
        IOC.register(eq(checkUserByEmailActorKey), createNewInstanceStrategyArgumentCaptor.capture());

        CloseAsyncOperationActor actor = mock(CloseAsyncOperationActor.class);
        IObject actorParams = mock(IObject.class);
        whenNew(CloseAsyncOperationActor.class).withArguments(actorParams).thenReturn(actor);

        assertEquals("Objects must return correct object", createNewInstanceStrategyArgumentCaptor.getValue().resolve(actorParams), actor);

        verifyNew(CloseAsyncOperationActor.class).withArguments(actorParams);
    }

    @Test
    public void MustInCorrectLoadNewIBootstrapItemThrowException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin").thenThrow(new InvalidArgumentException(""));

        try {
            plugin.load();
        } catch (PluginException e) {

            verifyNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin");
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenKeysThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        when(Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {
            verifyStatic();
            Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName());
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenIOCRegisterThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

       // verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey createAsyncOpKey = mock(IKey.class);
        when(Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName())).thenReturn(createAsyncOpKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        doThrow(new RegistrationException("")).when(IOC.class);
        IOC.register(eq(createAsyncOpKey), any());

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(mock(CreateNewInstanceStrategy.class));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {

            verifyStatic();
            Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName());

            verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

            verifyStatic();
            IOC.register(eq(createAsyncOpKey), any(CreateNewInstanceStrategy.class));

            CloseAsyncOperationActor actor = mock(CloseAsyncOperationActor.class);
            IObject actorParams = mock(IObject.class);
            whenNew(CloseAsyncOperationActor.class).withArguments(actorParams).thenReturn(actor);

            assertEquals("Objects must return correct object", targetFuncArgumentCaptor.getValue().apply(new Object[]{actorParams}), actor);

            verifyNew(CloseAsyncOperationActor.class).withArguments(actorParams);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenNewCreateInstanceThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

       // verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey createAsyncOpKey = mock(IKey.class);
        when(Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName())).thenReturn(createAsyncOpKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenThrow(new RegistrationException(""));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {

            verifyStatic();
            Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName());

            verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

            CloseAsyncOperationActor actor = mock(CloseAsyncOperationActor.class);
            IObject actorParams = mock(IObject.class);
            whenNew(CloseAsyncOperationActor.class).withArguments(actorParams).thenReturn(actor);

            assertTrue("Objects must return correct object", targetFuncArgumentCaptor.getValue().apply(new Object[]{actorParams}) == actor);

            verifyNew(CloseAsyncOperationActor.class).withArguments(actorParams);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test(expected = RuntimeException.class)
    public void MustInCorrectResolveWhenKeysGetKeyByNameActorParamKey() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey checkUserByEmailActorKey = mock(IKey.class);
        when(Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName())).thenReturn(checkUserByEmailActorKey);

        ArgumentCaptor<Function> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        CreateNewInstanceStrategy createNewInstanceStrategy = mock(CreateNewInstanceStrategy.class);
        whenNew(CreateNewInstanceStrategy.class)
                .withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(createNewInstanceStrategy);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName());

        verifyStatic();
        IOC.register(checkUserByEmailActorKey, createNewInstanceStrategy);

        IObject arg = mock(IObject.class);

        when(Keys.getKeyByName(ActorParams.class.toString())).thenThrow(new ResolutionException(""));

        targetFuncArgumentCaptor.getValue().apply(new Object[]{arg});
    }

    @Test(expected = RuntimeException.class)
    public void MustInCorrectResolveWhenIOCResolveActorParamsThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey checkUserByEmailActorKey = mock(IKey.class);
        when(Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName())).thenReturn(checkUserByEmailActorKey);

        ArgumentCaptor<Function> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        CreateNewInstanceStrategy createNewInstanceStrategy = mock(CreateNewInstanceStrategy.class);
        whenNew(CreateNewInstanceStrategy.class)
                .withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(createNewInstanceStrategy);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName());

        verifyStatic();
        IOC.register(checkUserByEmailActorKey, createNewInstanceStrategy);

        IObject arg = mock(IObject.class);

        IKey actorParamsIKey = mock(IKey.class);
        when(Keys.getKeyByName(ActorParams.class.toString())).thenReturn(actorParamsIKey);
        when(IOC.resolve(actorParamsIKey, arg)).thenThrow(new ResolutionException(""));

        targetFuncArgumentCaptor.getValue().apply(new Object[]{arg});
    }

    @Test
    public void MustInCorrectResolveWhenNewCloseAsyncOperationActorThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CloseAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey checkUserByEmailActorKey = mock(IKey.class);
        when(Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName())).thenReturn(checkUserByEmailActorKey);

        ArgumentCaptor<Function> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        CreateNewInstanceStrategy createNewInstanceStrategy = mock(CreateNewInstanceStrategy.class);
        whenNew(CreateNewInstanceStrategy.class)
                .withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(createNewInstanceStrategy);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName());

        verifyStatic();
        IOC.register(checkUserByEmailActorKey, createNewInstanceStrategy);

        IObject actorParams = mock(IObject.class);
        whenNew(CloseAsyncOperationActor.class).withArguments(actorParams).thenThrow(new InvalidArgumentException(""));

        try {
            targetFuncArgumentCaptor.getValue().apply(new Object[]{actorParams});
        } catch (RuntimeException e) {

            verifyNew(CloseAsyncOperationActor.class).withArguments(actorParams);
            return;
        }
        assertTrue("Must throw exception", false);
    }
}