package com.my_project.http_client_server_demo.demo_actor;

import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class DemoActorPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public DemoActorPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("register_http_cl_sv_demo_actor")
    public void register() throws Exception {
        IOC.register(Keys.getOrAdd("http cl-sv demo actor"),
                new SingletonStrategy(new DemoActor()));
    }
}
