package info.smart_tools.smartactors.field_plugins.ifield_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield.IField;
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

public class IFieldPluginTest extends IOCInitializer {

    private IFieldPlugin plugin;
    private IBootstrap bootstrap;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Before
    public void setUp() throws Exception {
        bootstrap = new Bootstrap();
        bootstrap.add(new BootstrapItem("IOC").process(()->{}));
        plugin = new IFieldPlugin(bootstrap);
    }

    @Test
    public void ShouldCorrectLoadPlugin() throws Exception {
        plugin.load();
        bootstrap.start();
        plugin.load();
        IKey fieldKey = Keys.getKeyByName(IField.class.getCanonicalName());
        Object obj = IOC.resolve(fieldKey, "field");
        assertNotNull(obj);
        bootstrap.revert();
        try {
            IOC.resolve(fieldKey, "field");
            fail();
        } catch (Exception e) {/**/}
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_InternalErrorIsOccurred() throws Exception {
        IBootstrap b = mock(IBootstrap.class);
        doThrow(InvalidArgumentException.class).when(b).add(any());
        IPlugin pl = new IFieldPlugin(b);
        pl.load();
    }

    @Test(expected = ProcessExecutionException.class)
    public void ShouldThrowException_When_ExceptionInLambdaIsThrown() throws Exception {
        IOC.register(
                Keys.getKeyByName("bootstrap item"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> new BootstrapItem((String) args[0])
                )
        );
        plugin.load();
        ScopeProvider.setCurrentScope(null);
        bootstrap.start();
    }
}
