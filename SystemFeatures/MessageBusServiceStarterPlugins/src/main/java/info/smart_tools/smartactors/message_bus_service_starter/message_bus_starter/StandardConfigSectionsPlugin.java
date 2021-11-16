package info.smart_tools.smartactors.message_bus_service_starter.message_bus_starter;

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
            /* "messageBus" section */
            IBootstrapItem<String> messageBusItem = new BootstrapItem("config_section:messageBus");

            messageBusItem
//                    .after("config_sections:start")
//                    .before("config_sections:done")
//                    .after("config_section:maps")
//                    .after("config_section:executor")
//                    .after("IFieldNamePlugin")
//                    .before("starter")
                    .process(() -> {
                        try(InputStream stream = getClass().getClassLoader().getResourceAsStream("message_bus_section_schema.json")) {
                            if (stream == null) {
                                throw new ActionExecutionException("Schema for section \"messageBus\" not found in resources.");
                            }

                            IFieldName sectionNameFieldName = IOC.resolve(
                                    Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                    "messageBus"
                            );
                            String schema = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));

                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new MessageBusSectionProcessingStrategy(sectionNameFieldName, schema));
                        } catch (ResolutionException | InvalidArgumentException | IOException e) {
                            throw new ActionExecutionException(e);
                        }
                    });
            bootstrap.add(messageBusItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
