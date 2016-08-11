package info.smart_tools.smartactors.plugin.messaging_identifiers;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

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
                    .after("FieldNamePlugin")
                    .after("IFieldNamePlugin")
                    .before("configure")
                    .process(() -> {
                        try {
                            IFieldName targetFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "target");

                            // Just use strings as identifiers for chains and receivers
                            IResolveDependencyStrategy toStringStrategy = new IResolveDependencyStrategy() {
                                @Override
                                public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
                                    return (T) String.valueOf(args[0]);
                                }
                            };

                            // To get receiver id from chain step IObject -- read its "target" field and cast to string
                            IResolveDependencyStrategy targetToStringStrategy = new IResolveDependencyStrategy() {
                                @Override
                                public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
                                    try {
                                        return (T) String.valueOf(((IObject) args[0]).getValue(targetFieldName));
                                    } catch (ReadValueException | InvalidArgumentException | ClassCastException e) {
                                        throw new ResolveDependencyStrategyException(e);
                                    }
                                }
                            };

                            IOC.register(Keys.getOrAdd("chain_id"), toStringStrategy);
                            IOC.register(Keys.getOrAdd("route_from_object_name"), toStringStrategy);
                            IOC.register(Keys.getOrAdd("chain_id_from_map_name"), toStringStrategy);
                            IOC.register(Keys.getOrAdd("receiver_id_from_iobject"), targetToStringStrategy);
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException(
                                    "MessagingIdentifiers plugin can't load: can't get MessagingIdentifiers key", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException(
                                    "MessagingIdentifiers plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(strategiesItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
