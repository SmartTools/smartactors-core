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
public class MyIOC {

    /** Key for getting instance of storage from current scope */
    private static final IKey STORAGE_KEY;

    static {
        try {
            STORAGE_KEY = new Key(java.util.UUID.randomUUID().toString());
            ScopeProvider.subscribeOnCreationNewScope(
                    scope -> {
                        try {
                            MyRecursiveContainer<IKey, Object> parentStorage = null;
                            try {
                                parentStorage = (MyRecursiveContainer<IKey, Object>) scope.getValue(STORAGE_KEY);
                            } catch (ScopeException e) {
                                // parent storage does not exists, create a new with null parent
                            }
                            scope.setValue(STORAGE_KEY, new MyRecursiveContainer<>(parentStorage));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (InvalidArgumentException | ScopeProviderException e) {
            throw new RuntimeException(e);
        }
    }

    private MyIOC() {
        // avoid instantiation
    }

    public static void init() {
        // empty method to force initialization of the class when necessary
    }

    public static void register(final IKey key, final Object value) throws MyIOCException {
        try {
            MyRecursiveContainer<IKey, Object> storage = (MyRecursiveContainer<IKey, Object>)
                    ScopeProvider.getCurrentScope().getValue(STORAGE_KEY);
            storage.put(key, value);
        } catch (Throwable e) {
            throw new MyIOCException(e);
        }
    }

    public static <T> T resolve(final IKey key) throws MyIOCException {
        try {
            MyRecursiveContainer<IKey, Object> storage = (MyRecursiveContainer<IKey, Object>)
                    ScopeProvider.getCurrentScope().getValue(STORAGE_KEY);
            return (T) storage.get(key);
        } catch (Throwable e) {
            throw new MyIOCException(e);
        }
    }

}
