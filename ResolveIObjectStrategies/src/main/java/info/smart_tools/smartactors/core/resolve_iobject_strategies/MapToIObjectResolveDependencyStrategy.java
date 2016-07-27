package info.smart_tools.smartactors.core.resolve_iobject_strategies;

import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.HashMap;
import java.util.Map;

/**
 * Convert from Map with string keys to IObject.
 */
public class MapToIObjectResolveDependencyStrategy implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {

        try {
            Map<String, Object> stringObjectMap = (Map<String, Object>) args[0];
            Map<IFieldName, Object> fieldNameObjectMap = new HashMap<>();
            for (String key: stringObjectMap.keySet()) {
                IFieldName fieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), key);
                fieldNameObjectMap.put(fieldName, stringObjectMap.get(key));
            }

            return (T) new DSObject(fieldNameObjectMap);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Can't create IObject from Map.", e);
        }
    }
}
