package info.smart_tools.smartactors.core.recursive_strategy_container;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.istrategy_container.exception.StrategyContainerException;

/**
 *  The special container which cannot resolve any strategy.
 *  Used as end-of-recursion container during recursive resolving.
 */
class EmptyStrategyContainer implements IStrategyContainer {

    /**
     * Always returns null because this container doesn't hold any strategies.
     * @param key unique object identifier
     * @return null
     * @throws StrategyContainerException never
     */
    @Override
    public IResolveDependencyStrategy resolve(final Object key) throws StrategyContainerException {
//        throw new StrategyContainerException(String.format("Strategy not found for %s", String.valueOf(key)));
        return null;
    }

    /**
     * Always throws exception because this container cannot resolve any strategy.
     * @param key unique object identifier
     * @param strategy instance of {@link IResolveDependencyStrategy}
     * @throws StrategyContainerException always
     */
    @Override
    public void register(final Object key, final IResolveDependencyStrategy strategy) throws StrategyContainerException {
        throw new StrategyContainerException(
                String.format("Cannot register %s by %s in EmptyStrategyContainer",
                        String.valueOf(strategy), String.valueOf(key)));
    }

    /**
     * Always thorw exception because this container cannot remove any strategy.
     * @param key unique object identifier
     * @throws StrategyContainerException always
     */
    @Override
    public void remove(final Object key) throws StrategyContainerException {
        throw new StrategyContainerException(
                String.format("Cannot remove the strategy for %s from EmptyStrategyContainer", String.valueOf(key)));
    }

}
