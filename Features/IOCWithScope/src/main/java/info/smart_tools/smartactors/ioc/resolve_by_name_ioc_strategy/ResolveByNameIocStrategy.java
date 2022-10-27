package info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link IStrategy}
 * <pre>
 * Strategy allows to storage instances of {@link info.smart_tools.smartactors.ioc.ikey.IKey}
 * </pre>
 */
public class ResolveByNameIocStrategy implements IStrategy {

    /**
     * Local {@link info.smart_tools.smartactors.ioc.ikey.IKey} instance storage
     */
    private Map<String, IKey> storage = new ConcurrentHashMap<>();

    /**
     * Default constructor
     */
    public ResolveByNameIocStrategy() {
    }

    /**
     * Return stored instance of {@link info.smart_tools.smartactors.ioc.ikey.IKey} if exists
     * otherwise create new instance of {@link IKey}, store to the local storage and return
     * @param <T> type of object
     * @param args needed parameters for resolve dependency
     * @return instance of object
     * @throws StrategyException if any errors occurred
     */
    @Override
    public <T> T resolve(final Object... args)
            throws StrategyException {
        try {
            IKey result = storage.get((String) args[0]);
            if (null == result) {
                result = new Key((String) args[0]);
                storage.put((String) args[0], result);
            }
            return (T) result;
        } catch (Exception e) {
            throw new StrategyException("Object resolution failed.", e);
        }
    }
}
