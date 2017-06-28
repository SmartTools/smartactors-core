package info.smart_tools.smartactors.base.up_counter;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IPoorAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.IllegalUpCounterState;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of {@link IUpCounter}.
 */
public class UpCounter implements IUpCounter {
    /**
     * <pre>
     * = 0 => no processes running, system can shutdown immediately
     * > 0 => there are running processes, cannot shutdown immediately
     * < 0 => system is down, cannot increment counter
     * </pre>
     */
    private final AtomicLong counter = new AtomicLong(0);
    private Object shutdownMode = null;

    private final List<IAction<Object>> shutdownRequestCallbacks = new CopyOnWriteArrayList<>();
    private final List<IAction<Void>> shutdownCompletionCallbacks = new CopyOnWriteArrayList<>();

    /**
     * The constructor.
     *
     * @param parent    the only parent counter of this one
     * @throws IllegalUpCounterState if parent is already down
     */
    public UpCounter(final IUpCounter parent) throws IllegalUpCounterState {
        // TODO:: Add addParent(IUpCounter) method to interface instead? (may cause loops, but may be useful)
        addParent(parent);
    }

    /**
     * Default constructor.
     */
    public UpCounter() {
    }

    /**
     * @param parent     the counter to be parent of this
     * @throws IllegalUpCounterState if that counter is already down
     */
    private void addParent(final IUpCounter parent) throws IllegalUpCounterState {
        parent.up();

        try {
            parent.onShutdownRequest(mode -> {
                try {
                    this.shutdown(mode);
                } catch (Exception e) {
                    throw new ActionExecuteException(e);
                }
            });

            // Execute shutdown callbacks even in case of force shutdown of parent counter.
            // May cause memory leaks if up-counters are created very frequently.
            parent.onShutdownComplete(() -> {
                try {
                    forceShutdown();
                } catch (IllegalUpCounterState ignore) {
                    // This counter is already down, ok
                } catch (Exception e) {
                    throw new ActionExecuteException(e);
                }
            });

            onShutdownComplete(() -> {
                try {
                    parent.down();
                } catch (Exception e) {
                    throw new ActionExecuteException(e);
                }
            });
        } catch (Exception e) {
            try {
                parent.down();
            } catch (Exception ee) {
                e.addSuppressed(ee);
            }

            throw new IllegalUpCounterState(e);
        }
    }

    /**
     * Execute _all_ of given callbacks (even if some throw).
     *
     * @throws UpCounterCallbackExecutionException if at least one of callbacks throws; exceptions thrown by subsequent callbacks are
     *                                             suppressed
     */
    private <T> void executeCallbacks(final List<IAction<T>> callbacks, final T arg) throws UpCounterCallbackExecutionException {
        UpCounterCallbackExecutionException exception = null;

        for (IAction<T> cb : callbacks) {
            try {
                cb.execute(arg);
            } catch (Exception e) {
                if (exception == null) {
                    exception = new UpCounterCallbackExecutionException(e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    private boolean tryShutdown(final long expect) throws UpCounterCallbackExecutionException {
        if (counter.compareAndSet(expect, -1)) {
            executeCallbacks(shutdownCompletionCallbacks, null);
            return true;
        }

        return false;
    }

    @Override
    public void up() throws IllegalUpCounterState {
        long ctr;

        do {
            ctr = counter.get();

            if (ctr < 0) {
                throw new IllegalUpCounterState("The system is already down.");
            }
        } while (!counter.compareAndSet(ctr, ctr + 1));
    }

    @Override
    public void down() throws UpCounterCallbackExecutionException, IllegalUpCounterState {
        long ctr;

        do {
            ctr = counter.get();

            if (ctr < 0) {
                throw new IllegalUpCounterState("The system is already down.");
            }

            if (ctr == 0) {
                throw new IllegalUpCounterState("There are no processes running.");
            }

            if (ctr == 1 && shutdownMode != null) {
                if (tryShutdown(1)) {
                    return;
                }
            }
        } while (!counter.compareAndSet(ctr, ctr - 1));
    }

    @Override
    public void shutdown(final Object mode) throws UpCounterCallbackExecutionException, IllegalUpCounterState {
        shutdownMode = mode;

        try {
            executeCallbacks(shutdownRequestCallbacks, mode);
        } finally {
            tryShutdown(0);
        }
    }

    @Override
    public void forceShutdown() throws UpCounterCallbackExecutionException, IllegalUpCounterState {
        long ctr;

        do {
            ctr = counter.get();

            if (ctr < 0) {
                throw new IllegalUpCounterState("System is already down.");
            }
        } while (!tryShutdown(ctr));
    }

    @Override
    public void onShutdownRequest(final IAction<Object> callback) throws UpCounterCallbackExecutionException {
        shutdownRequestCallbacks.add(callback);
    }

    @Override
    public void onShutdownComplete(final IPoorAction callback) throws UpCounterCallbackExecutionException {
        shutdownCompletionCallbacks.add(__ -> callback.execute());
    }
}
