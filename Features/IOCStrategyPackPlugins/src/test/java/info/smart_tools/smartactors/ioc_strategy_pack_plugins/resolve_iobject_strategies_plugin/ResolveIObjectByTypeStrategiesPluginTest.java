package info.smart_tools.smartactors.ioc_strategy_pack_plugins.resolve_iobject_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class ResolveIObjectByTypeStrategiesPluginTest extends IOCInitializer {

    private ResolveIObjectByTypeStrategiesPlugin plugin;
    private IBootstrap bootstrap;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Before
    public void setUp() throws Exception {
        bootstrap = new Bootstrap();
        bootstrap.add(new BootstrapItem("IOC").process(()->{}));
        plugin = new ResolveIObjectByTypeStrategiesPlugin(bootstrap);
    }

    @Test
    public void ShouldCorrectLoadAndRevertPlugin() throws Exception {
        plugin.load();
        bootstrap.start();
        IKey typeStrategy = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject" + "convert");
        IKey expandableTypeStrategy = Keys.getKeyByName("expandable_strategy#" + "info.smart_tools.smartactors.iobject.iobject.IObject");

        Object a = IOC.resolve(typeStrategy, "{}");
        Object b = IOC.resolve(expandableTypeStrategy);
        assertNotNull(a);
        assertNotNull(b);
        bootstrap.revert();
        try {
            Object a1 = IOC.resolve(typeStrategy, "{}");
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object b1 = IOC.resolve(expandableTypeStrategy, "{}");
            fail();
        } catch (ResolutionException e) {/**/}
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_InternalErrorIsOccurred() throws Exception {
        IBootstrap b = mock(IBootstrap.class);
        IPlugin pl = new ResolveIObjectByTypeStrategiesPlugin(b);
        doThrow(InvalidArgumentException.class).when(b).add(any());
        pl.load();
    }

    @Test(expected = InvalidArgumentException.class)
    public void ShouldThrowException_On_CreationWithNullArgument() throws Exception {
        new ResolveIObjectByTypeStrategiesPlugin(null);
    }

    @Test(expected = ProcessExecutionException.class)
    public void ShouldThrowException_When_ExceptionInLambdaIsThrown() throws Exception {
        plugin.load();
        ScopeProvider.setCurrentScope(null);
        bootstrap.start();
    }
}
