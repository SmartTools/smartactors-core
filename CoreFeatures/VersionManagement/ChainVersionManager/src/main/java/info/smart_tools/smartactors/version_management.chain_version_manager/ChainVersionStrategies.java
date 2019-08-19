package info.smart_tools.smartactors.version_management.chain_version_manager;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class ChainVersionStrategies {
    private Object mapName;
    private List<IStrategy> versionResolutionStrategies;
    private List<Comparable> versions;

    ChainVersionStrategies(final Object mapName) {
        this.mapName = mapName;
        this.versionResolutionStrategies = Collections.synchronizedList(new LinkedList<>());
        this.versions = Collections.synchronizedList(new LinkedList<>());
    }

    void registerVersionResolutionStrategy(final Comparable version, final IStrategy strategy) {
        int idx, order;
        for (idx = 0; idx < versions.size(); idx++) {
            order = versions.get(idx).compareTo(version);
            if (order == 0) {
                if (strategy != null) {
                    versionResolutionStrategies.remove(idx);
                    versionResolutionStrategies.add(idx, strategy);
                }
                return;
            } else if (order < 0) {
                break;
            }
        }
        versions.add(idx, version);
        versionResolutionStrategies.add(idx, strategy);
    }

    Comparable resolveVersion(final IObject message)
            throws StrategyException {

        Comparable version = null;
        for (IStrategy strategy : versionResolutionStrategies) {
            if (strategy != null) {
                try {
                    version = strategy.resolve(message);
                    if (version != null) {
                        break;
                    }
                } catch (Throwable e) {
                    System.out.println("[WARNING] Chain '" + mapName.toString() + "' version resolution strategy thrown exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        if (version == null) {
            version = versions.get(0);
        }
        return version;
    }
}