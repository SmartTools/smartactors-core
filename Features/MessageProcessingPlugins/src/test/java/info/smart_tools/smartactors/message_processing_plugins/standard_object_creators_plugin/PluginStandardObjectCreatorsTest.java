package info.smart_tools.smartactors.message_processing_plugins.standard_object_creators_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PluginStandardObjectCreatorsTest extends IOCInitializer {

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy");
    }

    @Test
    public void checkPluginCreation()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new PluginStandardObjectCreators(bootstrap);
        assertNotNull(plugin);
        reset(bootstrap);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnCreation()
            throws Exception {
        new PluginStandardObjectCreators(null);
        fail();
    }

    @Test
    public void checkLoadAndRevertExecution()
            throws Exception {
        Checker checker = new Checker();
        checker.item = new BootstrapItem("test");
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        List<IBootstrapItem<String>> itemList = new ArrayList<>();
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                checker.item = (IBootstrapItem<String>) args[0];
                itemList.add(checker.item);
                return null;
            }
        })
                .when(bootstrap)
                .add(any(IBootstrapItem.class));
        IPlugin plugin = new PluginStandardObjectCreators(bootstrap);
        plugin.load();
        assertEquals(itemList.size(), 1);
        IBootstrapItem<String> item = itemList.get(0);
        item.executeProcess();
        IRoutedObjectCreator objectCreator = IOC.resolve(
                Keys.getKeyByName(IRoutedObjectCreator.class.getCanonicalName() + "#raw")
        );
        assertNotNull(objectCreator);

        item.executeRevertProcess();

        try {
            IOC.resolve(Keys.getKeyByName(IRoutedObjectCreator.class.getCanonicalName() + "#raw"));
            fail();
        } catch (ResolutionException e) {}

        reset(bootstrap);
    }

    @Test (expected = PluginException.class)
    public void checkPluginExceptionOnPluginLoad()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new PluginStandardObjectCreators(bootstrap);
        doThrow(InvalidArgumentException.class).when(bootstrap).add(any(IBootstrapItem.class));
        plugin.load();
        fail();
    }
}
