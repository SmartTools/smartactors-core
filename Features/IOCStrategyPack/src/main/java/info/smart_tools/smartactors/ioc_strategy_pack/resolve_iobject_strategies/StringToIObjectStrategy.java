package info.smart_tools.smartactors.ioc_strategy_pack.resolve_iobject_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Convert from Map with string keys to IObject.
 */
public class StringToIObjectStrategy implements IStrategy {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Object... args) throws StrategyException {

        try {
            String jsonBody = String.valueOf(args[0]);
//            return (T) new DSObject(jsonBody);
            return (T) IOC.resolve(
                    Keys.getKeyByName(IObject.class.getCanonicalName()),
                    jsonBody
            );
        } catch (Exception e) {
            throw new StrategyException("Can't create IObject from String.", e);
        }
    }
}
