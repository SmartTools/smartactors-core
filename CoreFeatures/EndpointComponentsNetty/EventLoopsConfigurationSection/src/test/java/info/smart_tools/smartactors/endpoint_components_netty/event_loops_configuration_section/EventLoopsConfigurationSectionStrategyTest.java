package info.smart_tools.smartactors.endpoint_components_netty.event_loops_configuration_section;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.synchronized_lazy_named_items_storage_strategy.SynchronizedLazyNamedItemsStorageStrategy;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.EventLoopGroup;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class EventLoopsConfigurationSectionStrategyTest extends TrivialPluginsLoadingTestBase {
    private final String conf1 = ("" +
            "{'nettyEventLoops':[" +
            "   {" +
            "       'id': 'event-loop-1'," +
            "       'transport': 'transport1'," +
            "       'upcounter': 'root'," +
            "       'param1':'value'" +
            "   }," +
            "   {" +
            "       'id': 'event-loop-2'," +
            "       'alias': 'event-loop-1'" +
            "   }" +
            "]}" +
            "").replace('\'','"');

    private SynchronizedLazyNamedItemsStorageStrategy loopStorage;
    private IResolveDependencyStrategy upcounterStrategy, transportStrategy;
    private IUpCounter upCounter;
    private INettyTransportProvider transportProvider;
    private EventLoopGroup eventLoopGroup;

    @Override protected void registerMocks() throws Exception {
        loopStorage = new SynchronizedLazyNamedItemsStorageStrategy();
        IOC.register(Keys.getOrAdd("netty event loop group"), loopStorage);
        IOC.register(Keys.getOrAdd("expandable_strategy#netty event loop group"), new SingletonStrategy(loopStorage));

        upcounterStrategy = mock(IResolveDependencyStrategy.class);
        transportStrategy = mock(IResolveDependencyStrategy.class);

        when(upcounterStrategy.resolve(eq("root")))
                .thenReturn(upCounter = mock(IUpCounter.class));
        when(transportStrategy.resolve(eq("transport1")))
                .thenReturn(transportProvider = mock(INettyTransportProvider.class));

        IOC.register(Keys.getOrAdd("upcounter"), upcounterStrategy);
        IOC.register(Keys.getOrAdd("netty transport provider"), transportStrategy);

        eventLoopGroup = mock(EventLoopGroup.class);

        when(transportProvider.createEventLoopGroup(any())).then(inv -> {
            IObject gConf = inv.getArgumentAt(0, IObject.class);

            assertEquals("value", gConf.getValue(
                    IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "param1")
            ));

            return eventLoopGroup;
        }).thenThrow(RuntimeException.class);
    }

    @Test public void Should_readConfiguration() throws Exception {
        new EventLoopsConfigurationSectionStrategy().onLoadConfig(
                IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), conf1)
        );

        verifyNoMoreInteractions(transportProvider);

        EventLoopGroup group = loopStorage.resolve("event-loop-2");

        assertSame(eventLoopGroup, group);
    }
}