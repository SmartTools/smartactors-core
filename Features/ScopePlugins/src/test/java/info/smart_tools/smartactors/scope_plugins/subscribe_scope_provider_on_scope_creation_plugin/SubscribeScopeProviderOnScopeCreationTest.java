package info.smart_tools.smartactors.scope_plugins.subscribe_scope_provider_on_scope_creation_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SubscribeScopeProviderOnScopeCreation}
 */
public class SubscribeScopeProviderOnScopeCreationTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy");
    }

    @Before
    public void init()
            throws Exception {
    }


    @Test
    public void checkPluginCreation()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new SubscribeScopeProviderOnScopeCreation(bootstrap);
        assertNotNull(plugin);
        reset(bootstrap);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnCreation()
            throws Exception {
        new SubscribeScopeProviderOnScopeCreation(null);
        fail();
    }

    @Test
    public void checkLoadExecution()
            throws Exception {
        Checker checker = new Checker();
        checker.item = new BootstrapItem("test");
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        List<IBootstrapItem<String>> itemList = new ArrayList<>();
        doAnswer((Answer<Void>) invocation -> {
            Object[] args = invocation.getArguments();
            checker.item = (IBootstrapItem<String>) args[0];
            itemList.add(checker.item);
            return null;
        })
                .when(bootstrap)
                .add(any(IBootstrapItem.class));
        IPlugin plugin = new SubscribeScopeProviderOnScopeCreation(bootstrap);
        plugin.load();
        assertEquals(itemList.size(), 1);
        IBootstrapItem<String> item = itemList.get(0);
        Object guid1  = ScopeProvider.createScope(null);
        IScope scopeBefore = ScopeProvider.getScope(guid1);
        IStrategyContainer containerBefore = null;
        try {
            containerBefore = (IStrategyContainer) scopeBefore.getValue(IOC.getIocKey());
        } catch (Exception e) {

        }
        item.executeProcess();
        Object guid2  = ScopeProvider.createScope(null);
        IScope scopeAfter = ScopeProvider.getScope(guid2);
        IStrategyContainer containerAfter = (IStrategyContainer) scopeAfter.getValue(IOC.getIocKey());
        assertNotEquals(containerAfter, containerBefore);
        reset(bootstrap);
    }

    @Test (expected = PluginException.class)
    public void checkPluginExceptionOnPluginLoad()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new SubscribeScopeProviderOnScopeCreation(bootstrap);
        doThrow(InvalidArgumentException.class).when(bootstrap).add(any(IBootstrapItem.class));
        plugin.load();
        fail();
    }
}

class Checker {
    public IBootstrapItem<String> item;
}
