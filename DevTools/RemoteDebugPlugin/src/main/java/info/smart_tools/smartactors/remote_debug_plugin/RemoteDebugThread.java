package info.smart_tools.smartactors.remote_debug_plugin;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_processing.chain_storage.interfaces.IChainState;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.remote_debug.models.chain.ChainData;
import info.smart_tools.smartactors.remote_debug.models.config.ConfigData;
import info.smart_tools.smartactors.remote_debug.models.ioc.IocModule;
import info.smart_tools.smartactors.remote_debug.models.ioc.IocStrategy;
import info.smart_tools.smartactors.remote_debug.models.ioc.IocValue;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoteDebugThread extends Thread {

    private final IStrategyContainer container;
    private final IModule cachedModule;

    public RemoteDebugThread() throws Exception {
        super("remote-debug-thread");

        Field strategyContainerKey = IOC.class.getDeclaredField("strategyContainerKey");
        strategyContainerKey.setAccessible(true);
        IKey key = (IKey) strategyContainerKey.get(null);

        container = (IStrategyContainer) ScopeProvider.getCurrentScope().getValue(key);
        cachedModule = ModuleManager.getCurrentModule();
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
            // TODO: handle exceptions properly
            e.printStackTrace();
            throw new RuntimeException("Something went wrong", e);
        }
    }

    public ChainData[] getChainData() {
        try {
            ModuleManager.setCurrentModule(cachedModule);
            // TODO having some issues with getting chain storage from IOC
            IChainStorage chainStorage = container.resolve(new Key(IChainStorage.class.getCanonicalName())).resolve();
            Field chainStates = chainStorage.getClass().getDeclaredField("chainStates");
            chainStates.setAccessible(true);

            Map<Object, IChainState> chains = (Map<Object, IChainState>) chainStates.get(chainStorage);

            return chains.entrySet().stream()
                    .map(this::parseChain)
                    .toArray(ChainData[]::new);
        } catch (Exception e) {
            // TODO: handle exceptions properly
            e.printStackTrace();
            throw new RuntimeException("Something went wrong", e);
        }
    }

    public ConfigData[] getConfigSections() {
        try {
            ModuleManager.setCurrentModule(cachedModule);
            // TODO having some issues with getting config manager from IOC
//            IConfigurationManager configurationManager = IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
            IConfigurationManager configurationManager = container.resolve(new Key(IConfigurationManager.class.getCanonicalName())).resolve();

            Field sectionStrategies = configurationManager.getClass().getDeclaredField("sectionStrategies");
            sectionStrategies.setAccessible(true);

            List<ISectionStrategy> strategies = (List<ISectionStrategy>) sectionStrategies.get(configurationManager);

            return strategies.stream()
                    .map(this::parseConfig)
                    .toArray(ConfigData[]::new);
        } catch (Exception e) {
            // TODO: handle exceptions properly
            e.printStackTrace();
            throw new RuntimeException("Something went wrong", e);
        }
    }

    private ConfigData parseConfig(final ISectionStrategy strategy) {
        String sectionName = strategy.getSectionName().toString();
        String schema = strategy.getSectionSchema();

        return new ConfigData(sectionName, schema);
    }

    private ChainData parseChain(final Map.Entry<Object, IChainState> chainData) {
        String name = chainData.getKey().toString();

        return new ChainData(name);
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
