package info.smart_tools.smartactors.base.synchronized_lazy_named_items_storage_strategy;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.HashMap;
import java.util.Map;

/**
 * Strategy that stores named objects resolved lazily using added strategies.
 *
 * <p>
 *  A strategy for a object name may be changed or removed until the object for that name is resolved.
 * </p>
 *
 * <p>
 *  All operations are synchronized.
 * </p>
 */
public class SynchronizedLazyNamedItemsStorageStrategy
        implements IResolveDependencyStrategy, IAdditionDependencyStrategy {
    /**
     * A object that lazily resolves a single item.
     */
    private static final class LazyRef {
        private Object item;
        private ResolveDependencyStrategyException err;
        private boolean inProgress;

        private final IResolveDependencyStrategy factory;

        private LazyRef(final IResolveDependencyStrategy factory) {
            this.factory = factory;

            this.inProgress = false;
        }

        Object get(final Object... args)
                throws ResolveDependencyStrategyException {
            if (null != err) {
                throw err;
            }

            if (null != item) {
                return item;
            }

            try {
                if (inProgress) {
                    throw new ResolveDependencyStrategyException("Named items dependency loop detected.");
                }

                inProgress = true;
                item = factory.resolve(args);
            } catch (ResolveDependencyStrategyException e) {
                err = e;
                throw e;
            } finally {
                inProgress = false;
            }

            return item;
        }

        boolean resolved() {
            return item != null || err != null;
        }
    }

    private final Object lock = new Object();

    private final Map<Object, LazyRef> groups = new HashMap<>();

    @Override
    public void register(final Object key, final IResolveDependencyStrategy strategy)
            throws AdditionDependencyStrategyException {
        LazyRef ref = new LazyRef(strategy);
        LazyRef res;

        synchronized (lock) {
            res = groups.compute(key, (id0, present) -> {
                if (present != null && present.resolved()) {
                    return present;
                }

                return ref;
            });
        }

        if (res != ref) {
            throw new AdditionDependencyStrategyException("Item '" + key + "' is already resolved.");
        }
    }

    @Override
    public void remove(final Object key)
            throws AdditionDependencyStrategyException {
        synchronized (lock) {
            LazyRef ref = groups.get(key);

            if (null != ref && ref.resolved()) {
                throw new AdditionDependencyStrategyException("Item '" + key + "' cannot be removed as it is resolved.");
            }

            groups.remove(key);
        }
    }

    @Override
    public <T> T resolve(final Object... args)
            throws ResolveDependencyStrategyException {
        Object id = args[0];

        try {
            synchronized (lock) {
                LazyRef ref = groups.get(id);

                if (null == ref) {
                    throw new ResolveDependencyStrategyException("No item named registered with id '" + id + "'");
                }

                return (T) ref.get();
            }
        } catch (ResolveDependencyStrategyException e) {
            throw new ResolveDependencyStrategyException("Error resolving item '" + id + "'", e);
        }
    }
}
