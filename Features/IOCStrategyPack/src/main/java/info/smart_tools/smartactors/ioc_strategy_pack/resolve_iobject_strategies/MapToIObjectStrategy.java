package info.smart_tools.smartactors.ioc_strategy_pack.resolve_iobject_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.HashMap;
import java.util.Map;

/**
 * Convert from Map with string keys to IObject.
 */
public class MapToIObjectStrategy implements IStrategy {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Object... args) throws StrategyException {

        try {
            Map<String, Object> stringObjectMap = (Map<String, Object>) args[0];
            Map<IFieldName, Object> fieldNameObjectMap = new HashMap<>();
            for (String key: stringObjectMap.keySet()) {
                IFieldName fieldName = IOC.resolve(
                        Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        key
                );
                fieldNameObjectMap.put(fieldName, stringObjectMap.get(key));
            }

            return (T) IOC.resolve(
                    Keys.getKeyByName(IObject.class.getCanonicalName()),
                    fieldNameObjectMap
            );
        } catch (Exception e) {
            throw new StrategyException("Can't create IObject from Map.", e);
        }
    }
}
