package info.smart_tools.smartactors.core.examples.scope;

import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iscope.exception.ScopeException;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.string_ioc_key.Key;

/**
 *  A sample of scope based IoC.
 */
public final class SimpleIOC {

    /** Key for getting instance of storage from current scope */
    private static final IKey STORAGE_KEY;

    static {
        try {
            STORAGE_KEY = new Key(java.util.UUID.randomUUID().toString());
            ScopeProvider.subscribeOnCreationNewScope(
                    scope -> {
                        try {
                            RecursiveContainer<IKey, Object> parentStorage = null;
                            try {
                                parentStorage = (RecursiveContainer<IKey, Object>) scope.getValue(STORAGE_KEY);
                            } catch (ScopeException e) {
                                // parent storage does not exists, create a new with null parent
                            }
                            scope.setValue(STORAGE_KEY, new RecursiveContainer<>(parentStorage));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (InvalidArgumentException | ScopeProviderException e) {
            throw new RuntimeException(e);
        }
    }

    private SimpleIOC() {
        // avoid instantiation
    }

    /**
     * Initializes the IoC.
     * This method is empty, but call it when it's necessary to initialize the class,
     * i.e. call it's static initialization block.
     */
    public static void init() {
        // empty method to force initialization of the class when necessary
    }

    /**
     * Registers the object in the IoC.
     * @param key key for the object
     * @param value the object to register
     * @throws SimpleIOCException if something goes wrong
     */
    public static void register(final IKey key, final Object value) throws SimpleIOCException {
        try {
            RecursiveContainer<IKey, Object> storage = (RecursiveContainer<IKey, Object>)
                    ScopeProvider.getCurrentScope().getValue(STORAGE_KEY);
            storage.put(key, value);
        } catch (Throwable e) {
            throw new SimpleIOCException(e);
        }
    }

    /**
     * Resolves the previously registered object.
     * @param key the key to find the object
     * @param <T> type of the result to cast the stored object
     * @return the resolved object, can be null
     * @throws SimpleIOCException if something goes wrong
     */
    public static <T> T resolve(final IKey key) throws SimpleIOCException {
        try {
            RecursiveContainer<IKey, Object> storage = (RecursiveContainer<IKey, Object>)
                    ScopeProvider.getCurrentScope().getValue(STORAGE_KEY);
            return (T) storage.get(key);
        } catch (Throwable e) {
            throw new SimpleIOCException(e);
        }
    }

}
