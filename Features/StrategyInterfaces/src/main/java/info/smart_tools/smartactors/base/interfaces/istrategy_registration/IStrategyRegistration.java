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
    void register(final Object key, final IStrategy value) throws StrategyRegistrationException;

    /**
     * Method for unregistering strategy from key
     *
     * @param key key of the unregistering strategy
     * @throws StrategyRegistrationException if any errors occurred
     * @return the previous instance of {@link IStrategy} associated with {@code key},
     *         or {@code null} if there was no association for {@code key}.
     */
    IStrategy unregister(final Object key) throws StrategyRegistrationException;

}
