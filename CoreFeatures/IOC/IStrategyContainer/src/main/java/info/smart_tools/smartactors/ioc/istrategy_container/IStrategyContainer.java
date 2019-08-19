package info.smart_tools.smartactors.ioc.istrategy_container;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.istrategy_container.exception.StrategyContainerException;

/**
 * StrategyContainer interface
 * Provides methods for resolve and register dependency
 * {@link IStrategy}
 * by object unique identifier
 */
public interface IStrategyContainer {

    /**
     * Resolve dependency of {@link IStrategy} by unique object identifier
     * @param key unique object identifier
     * @return instance of {@link IStrategy}
     * @throws StrategyContainerException if any error occurred
     */
    IStrategy resolve(Object key)
            throws StrategyContainerException;

    /**
     * Register new dependency of {@link IStrategy} by unique object identifier
     * @param key unique object identifier
     * @param strategy instance of {@link IStrategy}
     * @throws StrategyContainerException  if any error occurred
     */
    void register(Object key, IStrategy strategy)
            throws StrategyContainerException;

    /**
     * Remove existing dependency of {@link IStrategy} by unique object identifier.
     * @param key unique object identifier
     * @throws StrategyContainerException  if any error occurred
     * @return the previous instance of {@link IStrategy} associated with <tt>key</tt>,
     *         or <tt>null</tt> if there was no association for <tt>key</tt>.
     */
    IStrategy unregister(Object key)
            throws StrategyContainerException;
}
