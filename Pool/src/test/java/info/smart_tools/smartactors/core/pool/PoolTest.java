package info.smart_tools.smartactors.core.pool;

import info.smart_tools.smartactors.core.ipool.exception.PoolTakeException;
import org.junit.Test;
import java.util.function.Supplier;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for Pool
 */

public class PoolTest {
    @Test(expected = PoolTakeException.class)
    public void Should_getItemsWhenRequired()
            throws Exception {
        Pool pool = new Pool(3, Object::new);
        assertNotNull(pool.take());
        assertNotNull(pool.take());
        assertNotNull(pool.take());
        pool.take();
        fail();
    }

    @Test
    public void Should_incrementCounterWhenExceptionIsThrown()
            throws Exception {
        Supplier func = mock(Supplier.class);
        when(func.get()).thenThrow(new RuntimeException()).thenReturn(new Object());
        Pool pool = new Pool(1, func);
        try {
            pool.take();
            fail();
        } catch (PoolTakeException ignored) {}
        assertNotNull(pool.take());
    }

    @Test
    public void Should_returnConnectionSecondTime_WhenItIsReturnedToPool()
            throws Exception {
        Pool pool = new Pool(2, Object::new);
        Object obj1 = pool.take();
        Object obj2 = pool.take();
        pool.put(obj1);
        pool.put(obj2);
        assertEquals(pool.take(), obj1);
        assertEquals(pool.take(), obj2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Should_throwExceptionWhenFunctionIsNull()
            throws Exception {
        Pool pool = new Pool(1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Should_throwExceptionWhenMaxItemsCountLessZero()
            throws Exception {
        Pool pool = new Pool(-1, Object::new);
    }
}
