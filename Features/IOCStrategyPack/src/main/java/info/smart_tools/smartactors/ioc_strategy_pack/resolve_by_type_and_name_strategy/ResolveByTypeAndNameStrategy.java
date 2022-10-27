package info.smart_tools.smartactors.ioc_strategy_pack.resolve_by_type_and_name_strategy;

import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * There are to parameters: type and name.
 * There is single strategy for create instance by type.
 * At resolve method first parameter should be type and second name of the object instance
 */
public class ResolveByTypeAndNameStrategy implements IStrategy, IStrategyRegistration {

    Map<String, IStrategy> creatingStrategy = new HashMap<>();
    Map<String, Object> createdDeserializationStrategies = new ConcurrentHashMap<>();

    @Override
    public Object resolve(final Object... args) throws StrategyException {
        //args[0] - type of the object
        //args[1] - name of the object
        String keyForResolvingKey = (String) args[0];
        String key = keyForResolvingKey + args[1];
        if (!createdDeserializationStrategies.containsKey(key)) {
            createdDeserializationStrategies.put(key, creatingStrategy.get(keyForResolvingKey).resolve(args));
        }
        return createdDeserializationStrategies.get(key);
    }

    @Override
    public void register(final Object key, final IStrategy strategy) throws StrategyRegistrationException {
        createdDeserializationStrategies.entrySet().removeIf((k -> k.getKey().startsWith((String) key)));
        creatingStrategy.put((String) key, strategy);
    }

    @Override
    public IStrategy unregister(final Object key) throws StrategyRegistrationException {
        return creatingStrategy.remove(key);
    }
}