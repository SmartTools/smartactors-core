package info.smart_tools.smartactors.endpoint_plugins.endpoint_external_configuration_file_reader_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.endpoint.endpoint_external_configuration_file_reader.EndpointExternalConfigurationFileReader;
import info.smart_tools.smartactors.endpoint.iendpoint_external_configuration_reader.exception.EndpointExternalConfigurationReaderException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin for registering {@link EndpointExternalConfigurationFileReader} in IOC
 * Class can be resolved by key {@code "endpoint external configuration file reader"}
 * This class requires 1 argument - default endpoint configuration object
 */
public class EndpointExternalConfigurationFileReaderPlugin extends BootstrapPlugin {

     /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointExternalConfigurationFileReaderPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Method that registers reader in IOC
     * @throws PluginException if unable to initialize plugin
     */
    @Item("endpoint-external-configuration-file-reader-plugin")
    public void init() throws PluginException {
        try {
            IOC.register(Keys.getKeyByName("endpoint external configuration file reader"),
                    new ApplyFunctionToArgumentsStrategy(
                            (args) -> {
                                try {
                                    IObject configuration = (IObject) args[0];
                                    return new EndpointExternalConfigurationFileReader(configuration);
                                } catch (EndpointExternalConfigurationReaderException e) {
                                    throw new RuntimeException("Unable to create EndpointExternalConfigurationFileReader instance", e);
                                }
                            }
                    )
            );
        } catch (RegistrationException | ResolutionException | InvalidArgumentException e) {
            throw new PluginException("Unable to resolve 'EndpointExternalConfigurationFileReader' key from IOC", e);
        }
    }

    /**
     * Method that unregisters reader from IOC.
     *
     * @throws ResolutionException if it is not possible to resolve key from IOC
     * @throws DeletionException   if it is not possible to unregister actor from IOC
     */
    @ItemRevert("endpoint-external-configuration-file-reader-plugin")
    public void unregister() throws ResolutionException, DeletionException {
        IOC.unregister(Keys.getKeyByName("endpoint external configuration file reader"));
    }
}
