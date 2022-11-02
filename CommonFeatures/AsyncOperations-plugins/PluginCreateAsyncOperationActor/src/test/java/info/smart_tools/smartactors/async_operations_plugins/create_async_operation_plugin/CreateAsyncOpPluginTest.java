package info.smart_tools.smartactors.async_operations_plugins.create_async_operation_plugin;

import info.smart_tools.smartactors.async_operations.create_async_operation.CreateAsyncOperationActor;
import info.smart_tools.smartactors.async_operations.create_async_operation.exception.CreateAsyncOperationActorException;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, CreateAsyncOpPlugin.class, CreateNewInstanceStrategy.class})
@RunWith(PowerMockRunner.class)
public class CreateAsyncOpPluginTest {
    private CreateAsyncOpPlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey key1 = mock(IKey.class);
        IKey keyPool = mock(IKey.class);
        when(IOC.getKeyForKeyByNameStrategy()).thenReturn(key1);
        when(IOC.resolve(eq(key1), eq("CreateAsyncOperationActorPlugin"))).thenReturn(keyPool);

        bootstrap = mock(IBootstrap.class);
        plugin = new CreateAsyncOpPlugin(bootstrap);
    }

    @Test
    public void MustCorrectLoadPlugin() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey createAsyncOpKey = mock(IKey.class);
        when(Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName())).thenReturn(createAsyncOpKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName());

        ArgumentCaptor<CreateNewInstanceStrategy> createNewInstanceStrategyArgumentCaptor = ArgumentCaptor.forClass(CreateNewInstanceStrategy.class);

        verifyStatic();
        IOC.register(eq(createAsyncOpKey), createNewInstanceStrategyArgumentCaptor.capture());

        IObject arg = mock(IObject.class);

        CreateAsyncOperationActor actor = mock(CreateAsyncOperationActor.class);
        whenNew(CreateAsyncOperationActor.class).withArguments(arg).thenReturn(actor);

        assertTrue("Objects must return correct object", createNewInstanceStrategyArgumentCaptor.getValue().resolve(arg) == actor);
        verifyNew(CreateAsyncOperationActor.class).withArguments(arg);
    }

    @Test
    public void MustInCorrectLoadNewIBootstrapItemThrowException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin").thenThrow(new InvalidArgumentException(""));

        try {
            plugin.load();
        } catch (PluginException e) {

            verifyNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin");
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenKeysThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        when(Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {
            verifyStatic();
            Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName());
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenIOCRegisterThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey createAsyncOpKey = mock(IKey.class);
        when(Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName())).thenReturn(createAsyncOpKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        doThrow(new RegistrationException("")).when(IOC.class);
        IOC.register(eq(createAsyncOpKey), any());

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(mock(CreateNewInstanceStrategy.class));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {

            verifyStatic();
            Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName());

            verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

            verifyStatic();
            IOC.register(eq(createAsyncOpKey), any(CreateNewInstanceStrategy.class));

            IObject arg = mock(IObject.class);

            CreateAsyncOperationActor actor = mock(CreateAsyncOperationActor.class);
            whenNew(CreateAsyncOperationActor.class).withArguments(arg).thenReturn(actor);

            assertTrue("Objects must return correct object", targetFuncArgumentCaptor.getValue().apply(new Object[]{arg}) == actor);
            verifyNew(CreateAsyncOperationActor.class).withArguments(arg);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenNewCreateInstanceThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey createAsyncOpKey = mock(IKey.class);
        when(Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName())).thenReturn(createAsyncOpKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenThrow(new InvalidArgumentException(""));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {

            verifyStatic();
            Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName());

            verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

            IObject arg = mock(IObject.class);

            CreateAsyncOperationActor actor = mock(CreateAsyncOperationActor.class);
            whenNew(CreateAsyncOperationActor.class).withArguments(arg).thenReturn(actor);

            assertTrue("Objects must return correct object", targetFuncArgumentCaptor.getValue().apply(new Object[]{arg}) == actor);
            verifyNew(CreateAsyncOperationActor.class).withArguments(arg);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectResolveWhenNewCreateAsyncOperationActorThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CreateAsyncOperationActorPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        //verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey createAsyncOpKey = mock(IKey.class);
        when(Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName())).thenReturn(createAsyncOpKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(mock(CreateNewInstanceStrategy.class));//the method which was used for constructor is importantly

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName());

        verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

        IObject arg = mock(IObject.class);

        CreateAsyncOperationActor actor = mock(CreateAsyncOperationActor.class);
        whenNew(CreateAsyncOperationActor.class)
                .withArguments(arg)
                .thenThrow(new CreateAsyncOperationActorException("", new Exception()));

        try {
            assertTrue("Objects must return correct object",
                    targetFuncArgumentCaptor.getValue().apply(new Object[]{arg}) == actor);
        } catch (RuntimeException e) {
            verifyNew(CreateAsyncOperationActor.class).withArguments(arg);
            return;
        }
        assertTrue("Must throw exception", false);
    }
}
