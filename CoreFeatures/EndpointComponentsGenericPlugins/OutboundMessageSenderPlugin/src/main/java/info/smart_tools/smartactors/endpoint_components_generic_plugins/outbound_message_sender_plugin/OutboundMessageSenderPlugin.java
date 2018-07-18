package info.smart_tools.smartactors.endpoint_components_generic_plugins.outbound_message_sender_plugin;

import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.outbound_message_sender.OutboundMessageSender;
import info.smart_tools.smartactors.endpoint_components_generic.outbound_request_sender.OutboundRequestSender;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class OutboundMessageSenderPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public OutboundMessageSenderPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("outbound_message_sender_actor")
    public void registerSender() throws Exception {
        IOC.register(Keys.getOrAdd("outbound message sender"),
                new SingletonStrategy(new OutboundMessageSender()));
    }

    @Item("outbound_request_sender_actor")
    public void registerRequestSender() throws Exception {
        IOC.register(Keys.getOrAdd("outbound request sender"),
                new SingletonStrategy(new OutboundRequestSender()));
    }
}
