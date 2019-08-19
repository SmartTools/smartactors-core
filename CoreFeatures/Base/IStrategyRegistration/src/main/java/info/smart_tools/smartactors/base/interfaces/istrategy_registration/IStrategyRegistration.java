package info.smart_tools.smartactors.base.interfaces.istrategy_registration;

import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;

/**
 * Interface to add or delete strategies from strategies
 */
public interface IStrategyRegistration {

    /**
     * Method for register new strategy to key
     *
     * @param key   key for using strategy
     * @param value {@link IStrategy} object, which should register by the key
     * @throws StrategyRegistrationException if any errors occurred
     */
    void register(Object key, IStrategy value) throws StrategyRegistrationException;

    /**
     * Method for unregistering strategy from key
     *
     * @param key key of the unregistering strategy
     * @throws StrategyRegistrationException if any errors occurred
     * @return the previous instance of {@link IStrategy} associated with <tt>key</tt>,
     *         or <tt>null</tt> if there was no association for <tt>key</tt>.
     */
    IStrategy unregister(Object key) throws StrategyRegistrationException;

}
