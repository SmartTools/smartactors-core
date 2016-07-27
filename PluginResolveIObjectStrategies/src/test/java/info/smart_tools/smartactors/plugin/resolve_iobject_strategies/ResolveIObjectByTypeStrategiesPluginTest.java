package info.smart_tools.smartactors.plugin.resolve_iobject_strategies;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_type_strategy.ResolveByTypeStrategy;
import info.smart_tools.smartactors.core.resolve_iobject_strategies.MapToIObjectResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_iobject_strategies.StringToIObjectResolveDependencyStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;

@PrepareForTest({IOC.class, Keys.class, ResolveByTypeStrategy.class, ResolveIObjectByTypeStrategiesPlugin.class})
@RunWith(PowerMockRunner.class)
public class ResolveIObjectByTypeStrategiesPluginTest {

    private ResolveIObjectByTypeStrategiesPlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        PowerMockito.mockStatic(IOC.class);
        PowerMockito.mockStatic(Keys.class);

        bootstrap = PowerMockito.mock(IBootstrap.class);
        plugin = new ResolveIObjectByTypeStrategiesPlugin(bootstrap);

    }

    @Test
    public void ShouldCorrectLoadPlugin() throws Exception {

        BootstrapItem item = PowerMockito.mock(BootstrapItem.class);
        PowerMockito.whenNew(BootstrapItem.class).withArguments("ResolveIObjectByTypeStrategiesPlugin").thenReturn(item);

        PowerMockito.when(item.after("IOC")).thenReturn(item);

        plugin.load();

        PowerMockito.verifyNew(BootstrapItem.class).withArguments("ResolveIObjectByTypeStrategiesPlugin");
        Mockito.verify(item).after("IOC");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        Mockito.verify(item).process(iPoorActionArgumentCaptor.capture());

        Mockito.verify(bootstrap).add(item);

        IKey strategyKey = PowerMockito.mock(IKey.class);
        PowerMockito.when(Keys.getOrAdd(IObject.class.getCanonicalName() + "convert")).thenReturn(strategyKey);

        ResolveByTypeStrategy strategy = PowerMockito.mock(ResolveByTypeStrategy.class);
        PowerMockito.whenNew(ResolveByTypeStrategy.class).withNoArguments().thenReturn(strategy);

        iPoorActionArgumentCaptor.getValue().execute();

        PowerMockito.verifyStatic();
        Keys.getOrAdd(IObject.class.getCanonicalName() + "convert");

        PowerMockito.verifyStatic();
        IOC.register(eq(strategyKey), eq(strategy));

        Mockito.verify(strategy).register(eq(Map.class), Matchers.any(MapToIObjectResolveDependencyStrategy.class));
        Mockito.verify(strategy).register(eq(String.class), Matchers.any(StringToIObjectResolveDependencyStrategy.class));
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_InternalErrorIsOccurred() throws Exception {

        PowerMockito.whenNew(BootstrapItem.class).withArguments("ResolveIObjectByTypeStrategiesPlugin").thenThrow(new InvalidArgumentException(""));
        plugin.load();
        fail();
    }

    @Test(expected = RuntimeException.class)
    public void ShouldThrowException_When_ExceptionInLambdaIsThrown() throws Exception {

        BootstrapItem item = PowerMockito.mock(BootstrapItem.class);
        PowerMockito.whenNew(BootstrapItem.class).withArguments("ResolveIObjectByTypeStrategiesPlugin").thenReturn(item);

        PowerMockito.when(item.after("IOC")).thenReturn(item);

        plugin.load();

        PowerMockito.verifyNew(BootstrapItem.class).withArguments("ResolveIObjectByTypeStrategiesPlugin");
        Mockito.verify(item).after("IOC");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        Mockito.verify(item).process(iPoorActionArgumentCaptor.capture());

        Mockito.verify(bootstrap).add(item);

        PowerMockito.doThrow(new ResolutionException("")).when(Keys.getOrAdd(IObject.class.getCanonicalName() + "convert"));
        iPoorActionArgumentCaptor.getValue().execute();
        fail();
    }

}
