package info.smart_tools.smartactors.core_service_starter.core_starter;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
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
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ObjectsSectionProcessingStrategy}
 */
public class ObjectsSectionProcessingStrategyTest extends PluginsLoadingTestBase {
    private IReceiverObjectListener listenerMock;
    private IReceiverObjectCreator[] creatorMocks;
    private IObject[] objectConfigMocks;
    private IObject configMock;
    private IResolveDependencyStrategy fullCreatorResolutionStrategy;

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
        listenerMock = mock(IReceiverObjectListener.class);
        fullCreatorResolutionStrategy = mock(IResolveDependencyStrategy.class);

        creatorMocks = new IReceiverObjectCreator[3];
        objectConfigMocks = new IObject[creatorMocks.length];

        for (int i = 0; i < creatorMocks.length; i++) {
            creatorMocks[i] = mock(IReceiverObjectCreator.class);
            objectConfigMocks[i] = mock(IObject.class);
            when(fullCreatorResolutionStrategy.resolve(same(objectConfigMocks[i]))).thenReturn(creatorMocks[i]);
        }

        when(configMock.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "objects")))
                .thenReturn(Arrays.asList(objectConfigMocks));

        IOC.register(Keys.getOrAdd("global router registration receiver object listener"), new SingletonStrategy(listenerMock));
        IOC.register(Keys.getOrAdd("full receiver object creator"), fullCreatorResolutionStrategy);
    }

    @Test
    public void Should_createObjects()
            throws Exception {
        new ObjectsSectionProcessingStrategy().onLoadConfig(configMock);

        for (int i = 0; i < creatorMocks.length; i++) {
            verify(creatorMocks[i]).create(same(listenerMock), same(objectConfigMocks[i]), any());
        }
    }

    @Test
    public void Should_storeSectionName()
            throws Exception {
        assertEquals(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "objects"),
                new ObjectsSectionProcessingStrategy().getSectionName()
        );
    }

    @Test(expected = ConfigurationProcessingException.class)
    public void Should_wrapCreationExceptions()
            throws Exception {
        doThrow(ReceiverObjectCreatorException.class).when(creatorMocks[0]).create(any(), any(), any());

        new ObjectsSectionProcessingStrategy().onLoadConfig(configMock);
    }
}
