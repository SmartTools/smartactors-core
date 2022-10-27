package info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.function.Function;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, ResolveByNameIocStrategy.class, IFieldNamePlugin.class, IFieldName.class, Function.class})
@RunWith(PowerMockRunner.class)
public class IFieldNamePluginTest {

    private IFieldNamePlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new IFieldNamePlugin(bootstrap);
    }

    @Test
    public void MustCorrectLoadPlugin() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);
        when(item.process(any())).thenReturn(item);
        when(item.revertProcess(any())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");

        verify(item).after("IOC");

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).process(iActionNoArgsArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(iFieldNameKey);

        ArgumentCaptor<ResolveByNameIocStrategy> resolveByNameIocStrategyArgumentCaptor =
                ArgumentCaptor.forClass(ResolveByNameIocStrategy.class);

        iActionNoArgsArgumentCaptor.getValue().execute();

        verifyStatic();

        Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        verifyStatic();
        IOC.register(eq(iFieldNameKey), resolveByNameIocStrategyArgumentCaptor.capture());

        String exampleFieldName = "exampleField";
        FieldName newFieldName = mock(FieldName.class);
        whenNew(FieldName.class).withArguments(exampleFieldName).thenReturn(newFieldName);

        assertTrue("Must return correct value",
                resolveByNameIocStrategyArgumentCaptor.getValue().resolve(exampleFieldName) == newFieldName);

        verifyNew(FieldName.class).withArguments(exampleFieldName);

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor2 = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).revertProcess(iActionNoArgsArgumentCaptor2.capture());

        iActionNoArgsArgumentCaptor2.getValue().execute();

        assertNull(IOC.unregister(eq(iFieldNameKey)));
    }

    @Test
    public void MustInCorrectLoadPluginWhenNewBootstrapItemThrowException() throws Exception {
        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenThrow(new InvalidArgumentException(""));
        try {
            plugin.load();
        } catch (PluginException e) {
            verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenKeysThrowException() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);
        when(item.process(any())).thenReturn(item);
        when(item.revertProcess(any())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");

        verify(item).after("IOC");

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).process(iActionNoArgsArgumentCaptor.capture());

        verify(bootstrap).add(item);

        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenThrow(new ResolutionException(""));

        try {
            iActionNoArgsArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {

            verifyStatic();
            Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
            return;
        }
        assertTrue("Must throw exception", false);

    }

    @Test
    public void MustInCorrectExecuteActionWhenNewCreateStrategyThrowException() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);
        when(item.process(any())).thenReturn(item);
        when(item.revertProcess(any())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");

        verify(item).after("IOC");

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).process(iActionNoArgsArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(iFieldNameKey);

        whenNew(ResolveByNameIocStrategy.class).withArguments(any()).thenThrow(new InvalidArgumentException(""));

        try {
            iActionNoArgsArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {

            verifyStatic();
            Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

            verifyNew(ResolveByNameIocStrategy.class).withArguments(any());
            return;
        }
        assertTrue("Must throw Exception", false);

    }

    @Test
    public void MustInCorrectExecuteActionWhenIOCRegisterThrowException () throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenReturn(item);
        when(item.after(anyString())).thenReturn(item);
        when(item.process(any())).thenReturn(item);
        when(item.revertProcess(any())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");

        verify(item).after("IOC");

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).process(iActionNoArgsArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);

        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(iFieldNameKey);

        ArgumentCaptor<ResolveByNameIocStrategy> resolveByNameIocStrategyArgumentCaptor =
                ArgumentCaptor.forClass(ResolveByNameIocStrategy.class);

        doThrow(new RegistrationException("")).when(IOC.class);
        IOC.register(eq(iFieldNameKey), any());
        try {
            iActionNoArgsArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {

            verifyStatic();
            Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

            verifyStatic();
            IOC.register(eq(iFieldNameKey), resolveByNameIocStrategyArgumentCaptor.capture());

            String exampleFieldName = "exampleField";
            FieldName newFieldName = mock(FieldName.class);
            whenNew(FieldName.class).withArguments(exampleFieldName).thenReturn(newFieldName);

            assertTrue("Must return correct value",
                    resolveByNameIocStrategyArgumentCaptor.getValue().resolve(exampleFieldName) == newFieldName);

            verifyNew(FieldName.class).withArguments(exampleFieldName);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test(expected = RuntimeException.class)
    public void MustInCorrectResolveDependencyWhenArgLength0() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);

        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");

        verify(item).after("IOC");

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).process(iActionNoArgsArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);

        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(iFieldNameKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        ResolveByNameIocStrategy resolveByNameIocStrategy = mock(ResolveByNameIocStrategy.class);
        whenNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.capture()).thenReturn(resolveByNameIocStrategy);

        iActionNoArgsArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

        verifyNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

        verifyStatic();
        IOC.register(iFieldNameKey, resolveByNameIocStrategy);

        targetFuncArgumentCaptor.getValue().apply(new Object[0]);

    }

    @Test(expected = RuntimeException.class)
    public void MustInCorrectResolveDependencyWhenArg0IsNotString() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);

        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");

        verify(item).after("IOC");

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).process(iActionNoArgsArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);

        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(iFieldNameKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        ResolveByNameIocStrategy createNewInstanceStrategy = mock(ResolveByNameIocStrategy.class);
        whenNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.capture()).thenReturn(createNewInstanceStrategy);

        iActionNoArgsArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

        verifyNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

        verifyStatic();
        IOC.register(iFieldNameKey, createNewInstanceStrategy);

        targetFuncArgumentCaptor.getValue().apply(new Object[]{2});

    }

    @Test
    public void MustInCorrectResolveDependencyWhenNewFieldNameThrowException() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);
        when(item.process(any())).thenReturn(item);
        when(item.revertProcess(any())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");

        verify(item).after("IOC");

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).process(iActionNoArgsArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(iFieldNameKey);

        ResolveByNameIocStrategy createNewInstanceStrategy = mock(ResolveByNameIocStrategy.class);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);
        whenNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.capture()).thenReturn(createNewInstanceStrategy);

        iActionNoArgsArgumentCaptor.getValue().execute();

        verifyStatic();

        Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

        verifyNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

        verifyStatic();
        IOC.register(iFieldNameKey, createNewInstanceStrategy);

        String fieldNameName = "asd";

        whenNew(FieldName.class).withArguments(fieldNameName).thenThrow(new InvalidArgumentException(""));

        try {
            targetFuncArgumentCaptor.getValue().apply(new Object[]{fieldNameName});
        } catch (RuntimeException e) {
            verifyNew(FieldName.class).withArguments(fieldNameName);
        }
    }

    @Test
    public void MustReportToConsoleWhenKeyRemovingThrowsException() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("IFieldNamePlugin").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);
        when(item.process(any())).thenReturn(item);
        when(item.revertProcess(any())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("IFieldNamePlugin");

        verify(item).after("IOC");

        ArgumentCaptor<IActionNoArgs> iActionNoArgsArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(item).revertProcess(iActionNoArgsArgumentCaptor.capture());

        verify(bootstrap).add(item);

        doThrow(new DeletionException("TestException")).when(IOC.class);
        IOC.unregister(any());

        iActionNoArgsArgumentCaptor.getValue().execute();
    }

}