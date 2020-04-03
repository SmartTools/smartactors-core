package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.utils;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_plugins.chain_modification_strategies_plugin.ChainModificationStrategiesPlugin;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.statistics.sensors.interfaces.ISensorHandle;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EmbeddedSensorCreationStrategy}.
 */
public class EmbeddedSensorCreationStrategyTest extends PluginsLoadingTestBase {
    private IMessageReceiver[] receivers = new IMessageReceiver[10];
    private IMessageProcessor processor = mock(IMessageProcessor.class);
    private IChainStorage chainStorageMock;
    private Object modId = new Object();
    private IReceiverChain chain;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
        load(ChainModificationStrategiesPlugin.class);
    }

    @Override
    protected void registerMocks() throws Exception {
        IOC.register(Keys.getKeyByName("prepend sensor receiver"), new PrependSensorReceiverStrategy());

        for (int i = 0; i < 10; i++) {
            receivers[i] = mock(IMessageReceiver.class);
        }

        IOC.register(Keys.getKeyByName("embedded sensor receiver"), new SingletonStrategy(receivers[9]));
        IOC.register(Keys.getKeyByName("additional sensor"), new SingletonStrategy(receivers[8]));

        chainStorageMock = mock(IChainStorage.class);
        IOC.register(Keys.getKeyByName(IChainStorage.class.getCanonicalName()), new SingletonStrategy(chainStorageMock));
        when(chainStorageMock.update(eq("the_chain__0"), any())).thenAnswer(invocation -> {
            chain = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), invocation.getArgumentAt(1, IObject.class)
                        .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "modification"))),
                    chain,
                    invocation.getArgumentAt(1, IObject.class)
            );
            return modId;
        });

        IOC.register(Keys.getKeyByName("chain_id_from_map_name"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) String.valueOf(args[0]).concat("__0");
            }
        });
    }

    @Test
    public void Should_replaceSomeReceiversByWrappers()
            throws Exception {
        chain = mock(IReceiverChain.class);
        when(chain.get(0)).thenReturn(receivers[0]);
        when(chain.get(1)).thenReturn(receivers[1]);
        when(chain.get(2)).thenReturn(receivers[2]);

        ISensorHandle handle = new EmbeddedSensorCreationStrategy().resolve("stat_chain", IOC.resolve(
                Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'chain':'the_chain'," +
                        "'args':{}," +
                        "'embed':[" +
                        "   {" +
                        "       'step':0," +
                        "       'dependency':'additional sensor'" +
                        "   }," +
                        "   {" +
                        "       'step':2" +
                        "   }" +
                        "]" +
                        "}").replace('\'','"')
        ));

        assertNotNull(handle);

        chain.get(0).receive(processor);
        verify(receivers[0], times(1)).receive(processor);
        verify(receivers[8], times(1)).receive(processor);

        chain.get(2).receive(processor);
        verify(receivers[2], times(1)).receive(processor);
        verify(receivers[9], times(1)).receive(processor);
    }
}
