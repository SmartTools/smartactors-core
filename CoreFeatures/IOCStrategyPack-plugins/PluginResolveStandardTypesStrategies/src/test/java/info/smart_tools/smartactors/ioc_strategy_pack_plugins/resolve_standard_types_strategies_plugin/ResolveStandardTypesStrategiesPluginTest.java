package info.smart_tools.smartactors.ioc_strategy_pack_plugins.resolve_standard_types_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, ResolveStandardTypesStrategiesPlugin.class, IActionNoArgs.class})
@RunWith(PowerMockRunner.class)
public class ResolveStandardTypesStrategiesPluginTest {

    private IBootstrap bootstrap;
    private ResolveStandardTypesStrategiesPlugin plugin;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new ResolveStandardTypesStrategiesPlugin(bootstrap);
    }

    @Test
    public void ShouldRegisterStrategiesForStandardTypes() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.process(any())).thenReturn(bootstrapItem);
        plugin.load();
        verifyNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin");

        verify(bootstrapItem).after("IOC");
        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        IKey stringConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(String.class.getCanonicalName() + "convert")).thenReturn(stringConvertKey);
        IKey integerConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(Integer.class.getCanonicalName() + "convert")).thenReturn(integerConvertKey);
        IKey bigDecimalConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(BigDecimal.class.getCanonicalName() + "convert")).thenReturn(bigDecimalConvertKey);
        IKey localDateTimeConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(LocalDateTime.class.getCanonicalName() + "convert")).thenReturn(localDateTimeConvertKey);
        IKey listConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(List.class.getCanonicalName() + "convert")).thenReturn(listConvertKey);
        IKey characterConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(Character.class.getCanonicalName() + "convert")).thenReturn(characterConvertKey);
        IKey booleanConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(boolean.class.getCanonicalName() + "convert")).thenReturn(booleanConvertKey);
        IKey intConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(int.class.getCanonicalName() + "convert")).thenReturn(intConvertKey);

        verify(bootstrap).add(eq(bootstrapItem));

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(String.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getKeyByName(Integer.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getKeyByName(BigDecimal.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getKeyByName(LocalDateTime.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getKeyByName(List.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getKeyByName(int.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getKeyByName(boolean.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getKeyByName(Character.class.getCanonicalName() + "convert");

        verifyStatic();
        IOC.register(eq(stringConvertKey), any(IStrategy.class));
        verifyStatic();
        IOC.register(eq(integerConvertKey), any(IStrategy.class));
        verifyStatic();
        IOC.register(eq(bigDecimalConvertKey), any(IStrategy.class));
        verifyStatic();
        IOC.register(eq(localDateTimeConvertKey), any(IStrategy.class));
        verifyStatic();
        IOC.register(eq(listConvertKey), any(IStrategy.class));
        verifyStatic();
        IOC.register(eq(intConvertKey), any(IStrategy.class));
        verifyStatic();
        IOC.register(eq(booleanConvertKey), any(IStrategy.class));
        verifyStatic();
        IOC.register(eq(characterConvertKey), any(IStrategy.class));

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor1 = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).revertProcess(actionArgumentCaptor1.capture());

        actionArgumentCaptor1.getValue().execute();

        assertNull(IOC.unregister(eq(stringConvertKey)));
        assertNull(IOC.unregister(eq(integerConvertKey)));
        assertNull(IOC.unregister(eq(bigDecimalConvertKey)));
        assertNull(IOC.unregister(eq(localDateTimeConvertKey)));
        assertNull(IOC.unregister(eq(listConvertKey)));
        assertNull(IOC.unregister(eq(intConvertKey)));
        assertNull(IOC.unregister(eq(booleanConvertKey)));
        assertNull(IOC.unregister(eq(characterConvertKey)));
    }

    @Test
    public void ShouldNotifyAboutKeyDeregistrationIssue() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.process(any())).thenReturn(bootstrapItem);
        plugin.load();

        IKey stringConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(String.class.getCanonicalName() + "convert")).thenReturn(stringConvertKey);
        IKey integerConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(Integer.class.getCanonicalName() + "convert")).thenReturn(integerConvertKey);
        IKey bigDecimalConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(BigDecimal.class.getCanonicalName() + "convert")).thenReturn(bigDecimalConvertKey);
        IKey localDateTimeConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(LocalDateTime.class.getCanonicalName() + "convert")).thenReturn(localDateTimeConvertKey);
        IKey listConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(List.class.getCanonicalName() + "convert")).thenReturn(listConvertKey);
        IKey characterConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(Character.class.getCanonicalName() + "convert")).thenReturn(characterConvertKey);
        IKey booleanConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(boolean.class.getCanonicalName() + "convert")).thenReturn(booleanConvertKey);
        IKey intConvertKey = mock(IKey.class);
        when(Keys.getKeyByName(int.class.getCanonicalName() + "convert")).thenReturn(intConvertKey);

        doThrow(new DeletionException("TestException")).when(IOC.class);
        IOC.unregister(any());

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).revertProcess(actionArgumentCaptor.capture());

        actionArgumentCaptor.getValue().execute();
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_BootstrapErrorIsOccurred() throws Exception {

        whenNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin").thenThrow(new InvalidArgumentException(""));
        plugin.load();
        fail();
    }

    @Test(expected = ActionExecutionException.class)
    public void ShouldThrowException_When_InternalErrorIsOccurred() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.process(any())).thenReturn(bootstrapItem);

        plugin.load();

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        when(Keys.getKeyByName(String.class.getCanonicalName() + "convert")).thenThrow(new ResolutionException(""));
        actionArgumentCaptor.getValue().execute();
        fail();
    }

}
