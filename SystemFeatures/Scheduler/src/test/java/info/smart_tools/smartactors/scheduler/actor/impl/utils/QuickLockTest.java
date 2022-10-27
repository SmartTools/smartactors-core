package info.smart_tools.smartactors.scheduler.actor.impl.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.*;

/**
 * Test for {@link QuickLock}.
 */
public class QuickLockTest {
    private Queue<String> q;
    private CyclicBarrier barrier;

    @Before
    public void setUp() throws Exception {
        q = new ConcurrentLinkedQueue<>();
    }

    @Test(timeout = 10000)
    public void Should_synchronizeThreadsAndSupportRecursiveLocking() throws Exception {
        for (int i = 0; i < 1000; i++) {
            barrier = new CyclicBarrier(3);

            QuickLock quickLock = new QuickLock(new Object());

            new Thread(() -> {
                try {
                    barrier.await();
                    barrier.await();
                    assertEquals(1, quickLock.lock());
                    q.add("1");
                    quickLock.unlock();

                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    barrier.await();
                    assertEquals(1, quickLock.lock());

                    barrier.await();
                    q.add("2-1");
                    assertEquals(2, quickLock.lock());
                    q.add("2-2");
                    quickLock.unlock();
                    q.add("2-3");
                    quickLock.unlock();

                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                    throw new RuntimeException(e);
                }
            }).start();

            barrier.await();
            barrier.await();
            barrier.await();

            assertEquals("2-1", q.poll());
            assertEquals("2-2", q.poll());
            assertEquals("2-3", q.poll());
            assertEquals("1", q.poll());
            assertNull(q.poll());
        }
    }

    @Test(timeout = 10000)
    public void Should_notCauseDeadlock()
            throws Exception {
        int n = 16, m = 1000;
        barrier = new CyclicBarrier(n + 1);

        Object shared = new Object();
        QuickLock[] locks = new QuickLock[] {
                new QuickLock(shared),
                new QuickLock(shared),
                new QuickLock(shared),
                new QuickLock(shared),
        };

        for (int i = 0; i < n; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < m; j++) {
                        QuickLock lock = locks[j % 4];
                        lock.lock();
                        Thread.yield();
                        if ((j & 1) != 0) {
                            lock.lock();
                            Thread.yield();
                            lock.unlock();
                            Thread.yield();
                        }
                        lock.unlock();
                    }
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        barrier.await();
    }
}
