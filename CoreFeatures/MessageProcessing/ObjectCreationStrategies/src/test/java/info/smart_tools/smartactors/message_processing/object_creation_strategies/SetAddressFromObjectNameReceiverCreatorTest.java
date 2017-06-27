package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for {@link SetAddressFromObjectNameReceiverCreator}.
 */
public class SetAddressFromObjectNameReceiverCreatorTest extends PluginsLoadingTestBase {
    private IObject configMock;
    private IObject contextMock;
    private IObject filterConfigMock;
    private IReceiverObjectCreator underlyingCreatorMock;
    private IReceiverObjectListener listenerMock;
    private Object object = new Object();

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks() throws Exception {
        configMock = mock(IObject.class);
        contextMock = mock(IObject.class);
        filterConfigMock = mock(IObject.class);
        underlyingCreatorMock = mock(IReceiverObjectCreator.class);
        listenerMock = mock(IReceiverObjectListener.class);

        when(configMock.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "name"))).thenReturn("the_object_name");

        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem("old_object_name", object);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
            return null;
        }).when(underlyingCreatorMock).create(any(), any(), any());
    }

    @Test
    public void Should_replaceObjectName()
            throws Exception {
        IReceiverObjectCreator creator = new SetAddressFromObjectNameReceiverCreator(underlyingCreatorMock, filterConfigMock, configMock);

        creator.create(listenerMock, configMock, contextMock);

        verify(listenerMock).acceptItem(eq("the_object_name"), same(object));
        verify(listenerMock).endItems();
    }

    @Test
    public void Should_enumerateTheName()
            throws Exception {
        IReceiverObjectCreator creator = new SetAddressFromObjectNameReceiverCreator(underlyingCreatorMock, filterConfigMock, configMock);

        assertEquals(Collections.singletonList("the_object_name"), creator.enumIdentifiers(configMock, contextMock));
    }
}
