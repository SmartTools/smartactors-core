package info.smart_tools.smartactors.core_service_starter.core_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

public class CoreBarrierPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * <p>
     * Constructor of a plugin extending this class should look like the following:
     * </p>
     *
     * <pre>
     *     class MyPlugin extends BootstrapPlugin {
     *         public MyPlugin(final IBootstrap bs) {
     *             super(bs);
     *         }
     *     }
     * </pre>
     *
     * @param bootstrap the bootstrap
     */
    public CoreBarrierPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Method for plugin's initialization
     *
     * @throws ResolutionException      throws if unable to resolve from {@link IOC} container
     * @throws RegistrationException    throws if unable to register in {@link IOC} container
     * @throws InvalidArgumentException throws if passed arguments are invalid
     */
    @Item("core")
    @After({
            "read_initial_config",
            "PluginOutOfResourcesExceptionHandlingMap",
    })
    public void init() throws ResolutionException, RegistrationException, InvalidArgumentException {
    }
}
