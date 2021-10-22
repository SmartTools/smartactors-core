package info.smart_tools.smartactors.ioc_viewer_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc_viewer.models.IocModule;
import info.smart_tools.smartactors.ioc_viewer.models.IocStrategy;
import info.smart_tools.smartactors.ioc_viewer.models.IocValue;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IocViewerPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public IocViewerPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> iocViewerItem = new BootstrapItem("ioc-viewer-plugin");

            iocViewerItem
                    .process(() -> {
                        try {
                            Thread debugThread = new IocViewerThread();
                            debugThread.start();
                        } catch (Exception e) {
                            throw new ActionExecutionException("Failed to start ioc viewer thread", e);
                        }
                    });

            bootstrap.add(iocViewerItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Failed to initialize bootstrap item", e);
        }
    }

    // TODO: move to separate class
    private class IocViewerThread extends Thread {

        private final IStrategyContainer container;

        public IocViewerThread() throws Exception {
            super("ioc-viewer-thread");

            Field strategyContainerKey = IOC.class.getDeclaredField("strategyContainerKey");
            strategyContainerKey.setAccessible(true);
            IKey key = (IKey) strategyContainerKey.get(null);

            container = (IStrategyContainer) ScopeProvider.getCurrentScope().getValue(key);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    int a = 1;
                    Thread.sleep(500);
                    int b = a++;
//                    System.out.println("Sleep cycle: " + Instant.now().toString());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to sleep", e);
                }
            }
        }

        public IocValue[] parseIoc() {
            try {
                Field strategyStorageField = container.getClass().getDeclaredField("strategyStorage");
                strategyStorageField.setAccessible(true);
                Map<Object, Map<IModule, IStrategy>> result = (Map<Object, Map<IModule, IStrategy>>) strategyStorageField.get(container);

                return result.entrySet().stream()
                        .map(this::parseDependency)
                        .toArray(IocValue[]::new);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Something went wrong", e);
            }
        }

        private IocValue parseDependency(final Map.Entry<Object, Map<IModule, IStrategy>> dependency) {
            String key = dependency.getKey().toString();

            List<IocStrategy> strategies = new ArrayList<>();

            dependency.getValue().forEach((module, strategy) -> {
                IocModule parsedModule = this.parseModule(module);
                // TODO: set an actual strategy, instead of class name
                IocStrategy parsedStrategy = new IocStrategy(parsedModule, strategy.getClass().getSimpleName());

                strategies.add(parsedStrategy);
            });

            return new IocValue(key, strategies);
        }

        private IocModule parseModule(final IModule module) {
            return new IocModule(module.getName(), module.getVersion());
        }
    }
}
