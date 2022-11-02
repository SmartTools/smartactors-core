package info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin;

import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test for {@link BootstrapPlugin}.
 */
public class BootstrapPluginTest {
    private IBootstrap bootstrapMock;
    private ArgumentCaptor<IBootstrapItem> itemArgumentCaptor;

    @Before
    public void setUp()
            throws Exception {
        bootstrapMock = mock(IBootstrap.class);
        itemArgumentCaptor = ArgumentCaptor.forClass(IBootstrapItem.class);
    }

    @Test(expected = PluginException.class)
    public void Should_throwWhenPluginContainsInvalidMethods()
            throws Exception {
        IPlugin plugin = new BootstrapPlugin(bootstrapMock) {
            @BootstrapPlugin.Item("wrongItem")
            public void wrongItemMethod(int x) {}
        };

        plugin.load();
    }

    @Test
    public void Should_createItemsForAnnotatedMethods()
            throws Exception {
        IPlugin plugin = new BootstrapPlugin(bootstrapMock) {
            @BootstrapPlugin.Item("item1")
            public void item1Method() {
            }

            @BootstrapPlugin.ItemRevert("item1")
            public void item1RevertMethod() {
            }

            @BootstrapPlugin.Item("item2")
            public void item2Method() {
            }

            public void notItemMethod() {}
        };

        plugin.load();

        verify(bootstrapMock, times(2)).add(itemArgumentCaptor.capture());

        for (IBootstrapItem item : itemArgumentCaptor.getAllValues()) {
            assertTrue(item instanceof MethodBootstrapItem);
        }
    }
}
