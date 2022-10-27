package info.smart_tools.smartactors.scheduler.actor.impl.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple optimistic lock.
 *
 * <p>
 * Uses only atomic operations if possible, otherwise uses a condition variable.
 * </p>
 *
 * <p>
 * Multiple {@link QuickLock}'s may share a single monitor so {@link QuickLock}'s do not require many system resources.
 * </p>
 *
 * <p>
 * {@link QuickLock} supports recursive locking.
 * </p>
 */
public class QuickLock {
    private static final int STATE_FREE = 0;
    private static final int STATE_LOCKED = 1;
    private static final int STATE_LOCKED_WAIT = 2;

    private final Object lock;
    private final AtomicInteger state = new AtomicInteger(STATE_FREE);
    private volatile long ownerTID = -1;
    private int lockDepth = 0;

    /**
     * The constructor.
     *
     * @param lock    the object monitor of which will be used
     */
    public QuickLock(final Object lock) {
        this.lock = lock;
    }

    /**
     * Acquire the lock.
     *
     * Uses only atomic operations if there is no thread owning the lock.
     *
     * @return lock depth - 1 if the lock is acquired, {@literal >}1 if the lock is acquired recursively
     * @throws InterruptedException if thread is interrupted while trying to acquire the lock
     */
    public int lock() throws InterruptedException {
        final long tid = Thread.currentThread().getId();

        if (state.get() != STATE_FREE) {
            if (ownerTID == tid) {
                return ++lockDepth;
            }
        }

        int captured;

        do {
            captured = state.get();

            if (STATE_FREE == captured) {
                if (state.compareAndSet(STATE_FREE, STATE_LOCKED)) {
                    ownerTID = tid;
                    lockDepth = 1;
                    return lockDepth;
                }

                continue;
            }

            if (STATE_LOCKED_WAIT == captured) {
                synchronized (lock) {
                    while (STATE_LOCKED_WAIT == state.get()) {
                        lock.wait();
                    }
                }

                continue;
            }

            if (STATE_LOCKED == captured) {
                state.compareAndSet(STATE_LOCKED, STATE_LOCKED_WAIT);
            }
        } while (true);
    }

    /**
     * Release the lock.
     *
     * Uses only atomic operations if there was no other thread(s) trying to acquire this lock while it was owned by current thread.
     */
    public void unlock() {
        if (lockDepth > 1) {
            --lockDepth;
            return;
        }

        ownerTID = -1;

        if (state.compareAndSet(STATE_LOCKED, STATE_FREE)) {
            return;
        }

        // state == STATE_LOCKED_WAIT
        synchronized (lock) {
            state.set(STATE_FREE);

            lock.notifyAll();
        }
    }
}
