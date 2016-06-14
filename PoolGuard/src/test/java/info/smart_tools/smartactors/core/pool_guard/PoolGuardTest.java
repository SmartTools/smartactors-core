package info.smart_tools.smartactors.core.pool_guard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.ipool.exception.PoolTakeException;
import info.smart_tools.smartactors.core.pool_guard.exception.PoolGuardException;
import org.junit.Test;

/**
 * Tests for ScopeGuard
 */
public class PoolGuardTest {
    @Test
    public void checkGuard()
            throws Exception {
        IPool pool = mock(IPool.class);
        Object obj1 = mock(Object.class);
        Object obj2 = mock(Object.class);
        when(pool.take()).thenReturn(obj1, obj2);
        PoolGuard guard1 = new PoolGuard(pool);
        PoolGuard guard2 = new PoolGuard(pool);
        assertEquals(guard1.getObject(), obj1);
        assertEquals(guard2.getObject(), obj2);
        guard1.close();
        verify(pool).put(obj1);
    }


    @Test (expected = PoolGuardException.class)
    public void checkScopeGuardExceptionOnGuard()
            throws Exception {
        IPool pool = mock(IPool.class);
        when(pool.take()).thenThrow(new PoolTakeException(""));
        PoolGuard guard = new PoolGuard(pool);
        fail();
    }
}
