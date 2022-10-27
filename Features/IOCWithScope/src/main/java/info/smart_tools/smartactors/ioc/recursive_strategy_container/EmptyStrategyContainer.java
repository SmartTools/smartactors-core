package info.smart_tools.smartactors.ioc.recursive_strategy_container;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.istrategy_container.exception.StrategyContainerException;

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
    public IStrategy resolve(final Object key) throws StrategyContainerException {
//        throw new StrategyContainerException(String.format("Strategy not found for %s", String.valueOf(key)));
        return null;
    }

    /**
     * Always throws exception because this container cannot resolve any strategy.
     * @param key unique object identifier
     * @param strategy instance of {@link IStrategy}
     * @throws StrategyContainerException always
     */
    @Override
    public void register(final Object key, final IStrategy strategy) throws StrategyContainerException {
        throw new StrategyContainerException(
                String.format("Cannot register %s by %s in EmptyStrategyContainer",
                        String.valueOf(strategy), String.valueOf(key)));
    }

    /**
     * Always throw exception because this container cannot unregister any strategy.
     * @param key unique object identifier
     * @throws StrategyContainerException always
     */
    @Override
    public IStrategy unregister(final Object key) throws StrategyContainerException {
        throw new StrategyContainerException(
                String.format("Cannot unregister the strategy for %s from EmptyStrategyContainer", String.valueOf(key)));
    }

}
