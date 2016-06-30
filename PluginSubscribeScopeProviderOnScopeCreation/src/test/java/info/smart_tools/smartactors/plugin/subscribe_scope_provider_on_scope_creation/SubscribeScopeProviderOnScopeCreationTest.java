package info.smart_tools.smartactors.plugin.subscribe_scope_provider_on_scope_creation;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * Tests for {@link SubscribeScopeProviderOnScopeCreation}
 */
public class SubscribeScopeProviderOnScopeCreationTest {

    @Before
    public void init()
            throws Exception {
        ScopeProvider.clearListOfSubscribers();
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
        assertNull(containerBefore);
        item.executeProcess();
        Object guid2  = ScopeProvider.createScope(null);
        IScope scopeAfter = ScopeProvider.getScope(guid2);
        IStrategyContainer containerAfter = (IStrategyContainer) scopeAfter.getValue(IOC.getIocKey());
        assertNotNull(containerAfter);
        reset(bootstrap);
    }

    @Test (expected = PluginException.class)
    public void checkPluginExceptionOnPluginLoad()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new SubscribeScopeProviderOnScopeCreation(bootstrap);
        doThrow(Exception.class).when(bootstrap).add(any(IBootstrapItem.class));
        plugin.load();
        fail();
    }
}

class Checker {
    public IBootstrapItem<String> item;
}
