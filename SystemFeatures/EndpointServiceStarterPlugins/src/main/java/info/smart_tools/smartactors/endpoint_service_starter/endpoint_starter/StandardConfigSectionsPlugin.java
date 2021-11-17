package info.smart_tools.smartactors.endpoint_service_starter.endpoint_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 *
 */
public class StandardConfigSectionsPlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public StandardConfigSectionsPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {

            /* "endpoints" section */
            IBootstrapItem<String> endpointsSectionItem = new BootstrapItem("config_section:endpoints");

            endpointsSectionItem
//                    .after("config_sections:start")
//                    .before("config_sections:done")
//                    .after("config_section:maps")
//                    .after("config_section:executor")
//                    .after("IFieldNamePlugin")
//                    .before("starter")
                    .process(() -> {
                        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("endpoints_section_schema.json")) {
                            if (stream == null) {
                                throw new ActionExecutionException("Schema for section \"endpoints\" not found in resources.");
                            }

                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

                            IFieldName sectionName = IOC.resolve(
                                    Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                    "endpoints"
                            );
                            String schema = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));

                            configurationManager.addSectionStrategy(new EndpointsSectionProcessingStrategy(sectionName, schema));
                        } catch (ResolutionException | InvalidArgumentException | IOException e) {
                            throw new ActionExecutionException(e);
                        }
                    });

            bootstrap.add(endpointsSectionItem);

            /* "client" section */
            IBootstrapItem<String> clientSectionItem = new BootstrapItem("config_section:client");

            clientSectionItem
//                    .after("configuration_manager")
//                    .after("config_section:maps")
//                    .after("config_section:executor")
//                    .after("IFieldNamePlugin")
//                    .before("starter")
                    .process(() -> {
                        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("client_section_schema.json")) {
                            if (stream == null) {
                                throw new ActionExecutionException("Schema for section \"client\" not found in resources.");
                            }

                            IConfigurationManager configurationManager = IOC.resolve(
                                    Keys.getKeyByName(IConfigurationManager.class.getCanonicalName())
                            );
                            IFieldName clientSectionName = IOC.resolve(
                                    Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                    "client"
                            );
                            String schema = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));

                            configurationManager.addSectionStrategy(new ClientSectionProcessingStrategy(clientSectionName, schema));
                        } catch (ResolutionException | InvalidArgumentException | IOException e) {
                            throw new ActionExecutionException(e);
                        }
                    });

            bootstrap.add(clientSectionItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
