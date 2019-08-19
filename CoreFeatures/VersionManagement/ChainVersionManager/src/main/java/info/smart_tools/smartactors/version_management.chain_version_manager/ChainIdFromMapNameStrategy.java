package info.smart_tools.smartactors.version_management.chain_version_manager;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChainIdFromMapNameStrategy {

    private Map<Object, Map<Comparable, Object>> chainIds = new ConcurrentHashMap<>();
    private static Map<Object, ChainVersionStrategies> chainVersionStrategies = new ConcurrentHashMap<>();
    private IStrategy resolve_by_message_strategy = new IStrategy() {
        @Override
        public <T> T resolve(final Object... args) throws StrategyException {
            return (T) resolve_by_message(args[0], (IObject) args[1]);
        }
    };

    private IStrategy resolve_by_module_dependencies_strategy = new IStrategy() {
        @Override
        public <T> T resolve(final Object... args) throws StrategyException {
            return (T) resolve_by_module_dependencies(args[0]);
        }
    };

    private IStrategy register_message_version_strategy = new IStrategy() {
        @Override
        public <T> T resolve(final Object... args) throws StrategyException {
            registerVersionResolutionStrategy(
                    args[0],                            // map name
                    (IStrategy) args[1] // message version resolution strategy
            );
            return (T) null;
        }
    };

    public IStrategy getRegisterMessageVersionStrategy() {
        return register_message_version_strategy;
    }

    public IStrategy getResolveByMessageStrategy() {
        return resolve_by_message_strategy;
    }

    public IStrategy getResolveByModuleDependenciesStrategy() {
        return resolve_by_module_dependencies_strategy;
    }

    private void registerVersionResolutionStrategy(final Object mapName, final IStrategy strategy)
            throws StrategyException {
        Comparable version = ModuleManager.getCurrentModule().getVersion();
        if (mapName == null || version == null) {
            throw new StrategyException("Chain name and version cannot be null.");
        }
        ChainVersionStrategies versionStrategies = chainVersionStrategies.get(mapName);
        if (versionStrategies == null) {
            versionStrategies = new ChainVersionStrategies(mapName);
            chainVersionStrategies.put(mapName, versionStrategies);
        }
        versionStrategies.registerVersionResolutionStrategy(version, strategy);
    }

    private Object registerChain(final Object mapName, final Comparable version)
            throws StrategyException {

        //if (mapName == null || version == null) {
        //    throw new StrategyException("Map name or version cannot be null.");
        //}

        Map<Comparable, Object> versions = chainIds.get(mapName);
        if (versions == null) {
            versions = new ConcurrentHashMap<>();
            chainIds.put(mapName, versions);
        }

        String chainId = mapName.toString() + ":" + version.toString();
        Object previous = versions.put(version, chainId);
        if (previous != null) {
            System.out.println(
                    "[WARNING] Replacing chain '" + chainId + "' by chain from feature " +
                    ModuleManager.getCurrentModule().getName() + ":" + version.toString()
            );
        } else {
            registerVersionResolutionStrategy(mapName, null);
        }
        return chainId;
    }

    private Object resolve_by_message(final Object chainName, final IObject message)
            throws StrategyException {
        if (chainName == null) {
            throw new StrategyException("Chain name cannot be null.");
        }
        if (chainName.toString().indexOf(":") > -1) {
            return chainName;
        }
        if (message == null) {
            throw new StrategyException("Message for chain Id resolution cannot be null.");
        }

        Comparable version = chainVersionStrategies.get(chainName).resolveVersion(message);
        return resolve_by_version(chainName, version);
    }

    private Object resolve_by_module_dependencies(final Object chainName)
            throws StrategyException {
        if (chainName.toString().indexOf(":") > -1) {
            return chainName;
        }
        Comparable version = ModuleManager.getCurrentModule().getVersion();
        return resolve_by_version(chainName, version);
    }

    private Object resolve_by_version(final Object chainName, final Comparable version)
            throws StrategyException {

        //if (chainName == null) {
        //    throw new StrategyException("Chain name cannot be null.");
        //}

        if (version == null) {
            throw new StrategyException("Chain Id resolution failed for chain name '" + chainName + "'.");
        }

        Map<Comparable, Object> versions = chainIds.get(chainName);
        if (versions == null) {
            registerChain(chainName, version);
            versions = chainIds.get(chainName);
        }

        Object chainId = versions.get(version);
        if (chainId == null) {
            chainId = registerChain(chainName, version);
        }

        return chainId;
    }

}