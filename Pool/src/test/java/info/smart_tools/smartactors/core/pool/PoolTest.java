package info.smart_tools.smartactors.core.pool;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Tests for Pool
 */

public class PoolTest {
    @Test
    public void Should_openConnectionsWhenRequired()
            throws Exception {
        Pool pool = new Pool(3, Object::new);
        assertNotNull(pool.tryTake());
        assertNotNull(pool.tryTake());
        assertNotNull(pool.tryTake());
    }

    @Test
    public void Should_returnConnectionSecondTime_WhenItIsReturnedToPool()
            throws Exception {
        Pool pool = new Pool(2, Object::new);
        Object obj1 = mock(Object.class);
        Object obj2 = mock(Object.class);
        pool.put(obj1);
        pool.put(obj2);
        assertEquals(pool.tryTake(), obj1);
        assertEquals(pool.tryTake(), obj2);
    }
}
