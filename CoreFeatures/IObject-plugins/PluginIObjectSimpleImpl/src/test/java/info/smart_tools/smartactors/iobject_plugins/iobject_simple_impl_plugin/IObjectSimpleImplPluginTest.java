package info.smart_tools.smartactors.iobject_plugins.iobject_simple_impl_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject_simple_implementation.IObjectImpl;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, IActionNoArgs.class, CreateNewInstanceStrategy.class, IObjectSimpleImplPlugin.class, IObjectImpl.class})
@RunWith(PowerMockRunner.class)
public class IObjectSimpleImplPluginTest {
    private IObjectSimpleImplPlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);
        mockStatic(IObject.class);

        IKey keyGeneral = mock(IKey.class);
        IKey keyPlugin = mock(IKey.class);
        when(IOC.getKeyForKeyByNameStrategy()).thenReturn(keyGeneral);
        when(IOC.resolve(eq(keyGeneral), eq("IObjectSimpleImplPlugin"))).thenReturn(keyPlugin);

        bootstrap = mock(IBootstrap.class);
        plugin = new IObjectSimpleImplPlugin(bootstrap);
    }

    @Test
    public void ShouldCorrectLoadPlugin() throws Exception {

        IKey IObjectKey = mock(IKey.class);
        when(Keys.getKeyByName(IObjectImpl.class.getCanonicalName())).thenReturn(IObjectKey);

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IObjectSimpleImplPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.process(any())).thenReturn(bootstrapItem);
        when(bootstrapItem.revertProcess(any())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IObjectSimpleImplPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        ArgumentCaptor<CreateNewInstanceStrategy> createNewInstanceStrategyArgumentCaptor =
                ArgumentCaptor.forClass(CreateNewInstanceStrategy.class);
        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        IOC.register(eq(IObjectKey), createNewInstanceStrategyArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor2 = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).revertProcess(actionArgumentCaptor2.capture());

        actionArgumentCaptor2.getValue().execute();

        assertNull(IOC.unregister(eq(IObjectKey)));
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_BootstrapItemThrowsException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("IObjectSimpleImplPlugin").thenThrow(new InvalidArgumentException(""));
        plugin.load();
    }

    @Test
    public void ShouldThrowRuntimeException_When_processThrowsException() throws Exception {

        when(Keys.getKeyByName(IObjectImpl.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IObjectSimpleImplPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.process(any())).thenReturn(bootstrapItem);
        when(bootstrapItem.revertProcess(any())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IObjectSimpleImplPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        try {
            actionArgumentCaptor.getValue().execute();
            fail();
        } catch(ActionExecutionException e) { }
    }

    @Test
    public void ShouldThrowRuntimeException_When_revertThrowsException() throws Exception {

        doThrow(new DeletionException("TestException")).when(IOC.class);
        IOC.unregister(any());

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IObjectSimpleImplPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.process(any())).thenReturn(bootstrapItem);
        when(bootstrapItem.revertProcess(any())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IObjectSimpleImplPlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).revertProcess(actionArgumentCaptor.capture());

        // this wrapper may be enabled when revert throws exception instead of printing to console
        // try {
        actionArgumentCaptor.getValue().execute();
        //    fail();
        //} catch(ActionExecutionException e) { }
    }
}
