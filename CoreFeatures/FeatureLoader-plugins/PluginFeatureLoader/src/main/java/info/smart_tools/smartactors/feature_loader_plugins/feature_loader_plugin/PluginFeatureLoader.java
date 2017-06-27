package info.smart_tools.smartactors.feature_loader_plugins.feature_loader_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loader.feature_loader.FeatureLoader;
import info.smart_tools.smartactors.feature_loader.feature_loader.FeatureStatusImpl;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loader.interfaces.ifeature_loader.GlobalFeatureLoader;
import info.smart_tools.smartactors.feature_loader.interfaces.ifeature_loader.IFeatureLoader;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.feature_loading_system.plugin_creator.PluginCreator;
import info.smart_tools.smartactors.feature_loading_system.plugin_loader_from_jar.PluginLoader;
import info.smart_tools.smartactors.feature_loading_system.plugin_loader_visitor_empty_implementation.PluginLoaderVisitor;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;

/**
 *
 */
public class PluginFeatureLoader extends BootstrapPlugin {
    /**
     * The constructor.
     * @param bootstrap    the bootstrap
     */
    public PluginFeatureLoader(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        super(bootstrap);
    }

    /**
     * Item registering the feature loader and some related components.
     * @throws ResolutionException if cannot resolve the keys
     * @throws RegistrationException if cannot register a strategy
     * @throws InvalidArgumentException if some of called methods throws
     */
    @Item("feature_loader")
    @After({"IOC", "configuration_manager", "config_sections:done", "IFieldNamePlugin", "filesystem_facade", "ConfigurationObject", "queue", "iobject"})
    public void registerFeatureLoader()
            throws ResolutionException, RegistrationException, InvalidArgumentException, ChangeValueException {
        IOC.register(Keys.getOrAdd(FeatureStatusImpl.class.getCanonicalName()), new CreateNewInstanceStrategy(args -> {
            try {
                return new FeatureStatusImpl((String) args[0], (IBiAction) args[1]);
            } catch (InvalidArgumentException e) {
                throw new RuntimeException(e);
            }
        }));
        IOC.register(Keys.getOrAdd("plugin creator"), new SingletonStrategy(new PluginCreator()));
        IOC.register(Keys.getOrAdd("plugin loader visitor"), new SingletonStrategy(new PluginLoaderVisitor<String>()));
        IOC.register(Keys.getOrAdd("plugin loader"), new CreateNewInstanceStrategy(args -> {
            try {
                return new PluginLoader(
                        (ClassLoader) args[0], (IAction<Class>) args[1], (IPluginLoaderVisitor) args[2]);
            } catch (InvalidArgumentException e) {
                throw new RuntimeException(e);
            }
        }));

        IFieldName sizeFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "queueSize");
        IObject sizeObj = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        sizeObj.setValue(sizeFN, 10);
        IQueue queue = IOC.resolve(Keys.getOrAdd(IQueue.class.getCanonicalName()), sizeObj);
        IOC.register(Keys.getOrAdd("feature group load completion task queue"),
                new SingletonStrategy(queue));

        IFeatureLoader featureLoader = new FeatureLoader();
        GlobalFeatureLoader.set(featureLoader);
        IOC.register(Keys.getOrAdd("feature loader"), new SingletonStrategy(featureLoader));
    }
}
