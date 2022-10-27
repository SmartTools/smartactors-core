package info.smart_tools.smartactors.version_management.chain_version_manager;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChainIdFromMapNameStrategy {

    Map<Object, Map<Comparable, Object>> chainIds = new ConcurrentHashMap<>();
    private static Map<Object, ChainVersionStrategies> chainVersionStrategies = new ConcurrentHashMap<>();
    private IStrategy resolveByMessageStrategy = new IStrategy(){
        @Override
        public <T> T resolve(Object... args) throws StrategyException {
            return (T) resolveByMessage(args[0], (IObject) args[1]);
        }
    };

    private IStrategy resolveByModuleDependenciesStrategy = new IStrategy(){
        @Override
        public <T> T resolve(Object... args) throws StrategyException {
            return (T) resolveByModuleDependencies(args[0]);
        }
    };

    private IStrategy registerMessageVersionStrategy = new IStrategy(){
        @Override
        public <T> T resolve(Object... args) throws StrategyException {
            registerVersionResolutionStrategy(
                    args[0],                            // map name
                    (IStrategy)args[1] // message version resolution strategy
            );
            return (T) null;
        }
    };

    public IStrategy getRegisterMessageVersionStrategy() {
        return registerMessageVersionStrategy;
    }

    public IStrategy getResolveByMessageStrategy() {
        return resolveByMessageStrategy;
    }

    public IStrategy getResolveByModuleDependenciesStrategy() {
        return resolveByModuleDependenciesStrategy;
    }

    private void registerVersionResolutionStrategy(Object mapName, IStrategy strategy)
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

    private Object registerChain(Object mapName, Comparable version)
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

    private Object resolveByMessage(Object chainName, IObject message)
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

    private Object resolveByModuleDependencies(Object chainName)
            throws StrategyException {
        if (chainName.toString().indexOf(":") > -1) {
            return chainName;
        }
        Comparable version = ModuleManager.getCurrentModule().getVersion();
        return resolve_by_version(chainName, version);
    }

    private Object resolve_by_version(Object chainName, Comparable version)
            throws StrategyException {

        //if (chainName == null) {
        //    throw new StrategyException("Chain name cannot be null.");
        //}

        if (version == null) {
            throw new StrategyException("Chain Id resolution failed for chain name '"+chainName+"'.");
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