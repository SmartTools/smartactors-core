package info.smart_tools.smartactors.base.pool;

import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.ipool.exception.GettingFromPoolException;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Pool
 */

public class PoolTest {
    @Test(expected = GettingFromPoolException.class)
    public void Should_getItemsWhenRequired()
            throws Exception {
        Pool pool = new Pool(3, Object::new);
        assertNotNull(pool.get());
        assertNotNull(pool.get());
        assertNotNull(pool.get());
        pool.get();
        fail();
    }

    @Test
    public void Should_incrementCounterWhenExceptionIsThrown()
            throws Exception {
        Supplier func = mock(Supplier.class);
        when(func.get()).thenThrow(new RuntimeException()).thenReturn(new Object());
        Pool pool = new Pool(1, func);
        try {
            pool.get();
            fail();
        } catch (GettingFromPoolException ignored) {}
        assertNotNull(pool.get());
    }

    @Test
    public void Should_returnConnectionSecondTime_WhenItIsReturnedToPool()
            throws Exception {
        Pool pool = new Pool(2, Object::new);
        Object obj1 = pool.get();
        Object obj2 = pool.get();
        pool.put(obj1);
        pool.put(obj2);
        assertEquals(pool.get(), obj1);
        assertEquals(pool.get(), obj2);
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

    @Test
    public void Should_createItemOneTimeWhenItemReturns() throws Exception {
        Supplier<Object> createFunc = mock(Supplier.class);
        when(createFunc.get()).thenReturn(new Object());
        Pool pool = new Pool(10, createFunc);
        Object item = pool.get();
        pool.put(item);
        pool.get();
        verify(createFunc, times(1)).get();
    }

    @Test
    public void Should_executeActionNoArgsWhenItemReturns() throws Exception {
        Supplier<Object> createFunc = mock(Supplier.class);
        when(createFunc.get()).thenReturn(new Object());
        Pool pool = new Pool(1, createFunc);
        IActionNoArgs pAction = mock(IActionNoArgs.class);
        pool.onAvailable(pAction);
        Object item = pool.get();
        pool.put(item);
        verify(pAction).execute();
    }

    @Test
    public void Should_executeActionNoArgsWhenFreeItemsAreExists() throws Exception {
        Supplier<Object> createFunc = mock(Supplier.class);
        when(createFunc.get()).thenReturn(new Object());
        Pool pool = new Pool(1, createFunc);
        IActionNoArgs pAction = mock(IActionNoArgs.class);
        pool.onAvailable(pAction);
        verify(pAction).execute();
    }

    @Test
    public void Should_returnNewItemIfItsInvalid() throws Exception {
        Pool pool = new Pool(1, Object::new, (item) -> false);
        Object obj1 = pool.get();
        pool.put(obj1);
        Object obj2 = pool.get();

        assertNotEquals(obj1, obj2);
    }
}
