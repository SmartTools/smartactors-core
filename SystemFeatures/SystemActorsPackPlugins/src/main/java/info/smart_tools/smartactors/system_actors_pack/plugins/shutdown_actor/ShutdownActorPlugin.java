package info.smart_tools.smartactors.system_actors_pack.plugins.shutdown_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.system_actors_pack.actors.shutdown_actor.ShutdownActor;

public class ShutdownActorPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public ShutdownActorPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("shutdown_actor")
    public void registerActor() throws ResolutionException, InvalidArgumentException, RegistrationException {
        IOC.register(Keys.getKeyByName("shutdown actor"), new SingletonStrategy(new ShutdownActor()));
    }
}
