package info.smart_tools.smartactors.plugin.resolve_iobject_strategies;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_type_strategy.ResolveByTypeStrategy;
import info.smart_tools.smartactors.core.resolve_iobject_strategies.MapToIObjectResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_iobject_strategies.StringToIObjectResolveDependencyStrategy;

import java.util.Map;

/**
 * Plugin registers resolve by type strategy for IObject and fills it by
 * concrete converting IObject strategies from different classes (map, string)
 */
public class ResolveIObjectByTypeStrategiesPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     * @throws InvalidArgumentException if bootstrap is null
     */
    public ResolveIObjectByTypeStrategiesPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("ResolveIObjectByTypeStrategiesPlugin");
            item
                .after("IOC")
                .process(() -> {
                    try {
                        IKey typeStrategy = Keys.getOrAdd(IObject.class.getCanonicalName() + "convert");
                        ResolveByTypeStrategy resolveStrategy = new ResolveByTypeStrategy();
                        resolveStrategy.register(Map.class, new MapToIObjectResolveDependencyStrategy());
                        resolveStrategy.register(String.class, new StringToIObjectResolveDependencyStrategy());
                        IOC.register(typeStrategy, resolveStrategy);
                    } catch (ResolutionException | RegistrationException e) {
                        throw new RuntimeException(e);
                    }
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load resolve iobject by type strategies plugin", e);
        }
    }
}
