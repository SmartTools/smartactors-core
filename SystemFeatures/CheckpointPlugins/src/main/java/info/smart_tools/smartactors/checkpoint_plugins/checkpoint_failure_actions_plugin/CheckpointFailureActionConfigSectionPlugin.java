package info.smart_tools.smartactors.checkpoint_plugins.checkpoint_failure_actions_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.checkpoint.failure_action.CheckpointFailureActionSectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 *
 */
public class CheckpointFailureActionConfigSectionPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public CheckpointFailureActionConfigSectionPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register strategy processing "checkpoint_failure_action" config section.
     *
     * @throws ResolutionException if error occurs resolving the current configuration manager
     * @throws InvalidArgumentException if configuration manager does not accept section strategy
     */
    @Item("config_section:checkpoint_failure_action")
    @Before({"checkpoint_actor"})
    @After({
            "checkpoint_failure_action_default",
    })
    public void registerSectionStrategy()
            throws ResolutionException, InvalidArgumentException {
        IConfigurationManager configurationManager = IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
        configurationManager.addSectionStrategy(new CheckpointFailureActionSectionStrategy());
    }
}
