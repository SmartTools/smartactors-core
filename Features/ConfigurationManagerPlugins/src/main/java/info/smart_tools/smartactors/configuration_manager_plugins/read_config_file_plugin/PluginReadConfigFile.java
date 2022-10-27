package info.smart_tools.smartactors.configuration_manager_plugins.read_config_file_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Reads configuration object from file. Name of the configuration file should be stored in environment variable "SM_CONFIG_FILE". By
 * default file named "configuration.json" from working directory is used.
 */
public class PluginReadConfigFile implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginReadConfigFile(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> readConfigItem = new BootstrapItem("read_initial_config");

            readConfigItem
                    .after("config_sections:done")
                    .after("iobject")
                    .after("ConfigurationObject")
                    .after("message_processor")
                    .after("message_processing_sequence")
                    .after("after exception actions")
                    .process(() -> {
                        try {
                            String fileName = System.getenv().getOrDefault("SM_CONFIG_FILE", "configuration.json");
                            byte[] rawConfig = Files.readAllBytes(Paths.get(fileName));
                            String configString = new String(rawConfig);

                            IConfigurationManager configurationManager = IOC.resolve(
                                    Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

                            IObject cObject = IOC.resolve(Keys.getKeyByName("configuration object"), configString);

                            configurationManager.applyConfig(cObject);
                        } catch (FileNotFoundException e) {
                            throw new ActionExecutionException("ReadConfigFile plugin can't load: configuration file not found.", e);
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("ReadConfigFile plugin can't load: can't get ReadConfigFile key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("ReadConfigFile plugin can't load: can't create strategy", e);
                        } catch (IOException e) {
                            throw new ActionExecutionException("ReadConfigFile plugin can't load: can't read configuration file", e);
                        } catch (ConfigurationProcessingException e) {
                            throw new ActionExecutionException("Error occurred processing configuration.", e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            String fileName = System.getenv().getOrDefault("SM_CONFIG_FILE", "configuration.json");
                            byte[] rawConfig = Files.readAllBytes(Paths.get(fileName));
                            String configString = new String(rawConfig);

                            IConfigurationManager configurationManager = IOC.resolve(
                                    Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

                            IObject cObject = IOC.resolve(Keys.getKeyByName("configuration object"), configString);

                            configurationManager.revertConfig(cObject);
                        } catch (FileNotFoundException e) {
                            throw new ActionExecutionException("ReadConfigFile plugin can't revert: configuration file not found.", e);
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("ReadConfigFile plugin can't revert: can't get ReadConfigFile key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("ReadConfigFile plugin can't revert: can't revert the strategy", e);
                        } catch (IOException e) {
                            throw new ActionExecutionException("ReadConfigFile plugin can't revert: can't read configuration file", e);
                        } catch (ConfigurationProcessingException e) {
                            /*  Now suppress all exceptions since we count revert successful
                                even if not all actions done normally. Before it was:
                                    throw new ActionExecutionException("Error occurred processing configuration.", e);
                            */
                        }
                    });

            bootstrap.add(readConfigItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
