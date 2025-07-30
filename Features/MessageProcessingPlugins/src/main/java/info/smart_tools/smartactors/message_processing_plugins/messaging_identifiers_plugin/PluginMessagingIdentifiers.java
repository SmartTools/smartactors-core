package info.smart_tools.smartactors.message_processing_plugins.messaging_identifiers_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin defines simple strategies of creation of objects identifying components of messaging system (receivers and maps).
 */
public class PluginMessagingIdentifiers implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginMessagingIdentifiers(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> strategiesItem = new BootstrapItem("messaging_identifiers");

            strategiesItem
                    .after("IOC")
                    .after("IFieldNamePlugin")
                    .before("starter")
                    .process(() -> {
                        try {
                            IFieldName targetFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "target");

                            // Just use strings as identifiers for chains and receivers
                            IStrategy toStringStrategy = new IStrategy() {
                                @Override
                                public <T> T resolve(final Object... args) throws StrategyException {
                                    return (T) String.valueOf(args[0]);
                                }
                            };

                            // To get receiver id from chain step IObject -- read its "target" field and cast to string
                            IStrategy targetToStringStrategy = new IStrategy() {
                                @Override
                                public <T> T resolve(final Object... args) throws StrategyException {
                                    try {
                                        return (T) String.valueOf(((IObject) args[0]).getValue(targetFieldName));
                                    } catch (ReadValueException | InvalidArgumentException | ClassCastException e) {
                                        throw new StrategyException(e);
                                    }
                                }
                            };

                            IOC.register(Keys.getKeyByName("route_from_object_name"), toStringStrategy);
                            IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), toStringStrategy);
                            IOC.register(Keys.getKeyByName("chain_id_from_map_name"), toStringStrategy);
                            IOC.register(Keys.getKeyByName("receiver_id_from_iobject"), targetToStringStrategy);
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("MessagingIdentifiers plugin can't load: can't get MessagingIdentifiers key", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("MessagingIdentifiers plugin can't load: can't register new strategy", e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = {
                                "receiver_id_from_iobject",
                                "chain_id_from_map_name",
                                "chain_id_from_map_name_and_message",
                                "route_from_object_name"
                        };
                        Keys.unregisterByNames(keyNames);
                    });

            bootstrap.add(strategiesItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
