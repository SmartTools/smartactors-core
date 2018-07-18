package info.smart_tools.smartactors.endpoint_components_generic_plugins.global_outbound_connection_channel_storage_plugin;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.default_outbound_channel_listener.DefaultOutboundChannelListener;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalOutboundConnectionChannelStoragePlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public GlobalOutboundConnectionChannelStoragePlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("global_outbound_channel_storage")
    public void registerStorage() throws Exception {
        Map<Object, IOutboundConnectionChannel> storageMap = new ConcurrentHashMap<>();

        IOC.register(Keys.getOrAdd("global outbound connection channel storage channel listener"),
                new SingletonStrategy(new DefaultOutboundChannelListener(storageMap)));
        IOC.register(Keys.getOrAdd("global outbound connection channel"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    IOutboundConnectionChannel channel = storageMap.get(args[0]);

                    if (null == channel) {
                        throw new FunctionExecutionException("No outbound channel registered with id='" + args[0] + "'.");
                    }

                    return channel;
                }));
    }
}
