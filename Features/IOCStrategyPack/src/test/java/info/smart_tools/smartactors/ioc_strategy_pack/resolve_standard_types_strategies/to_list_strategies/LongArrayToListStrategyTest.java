package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LongArrayToListStrategyTest {

    private LongArrayToListStrategy strategy;

    @Before
    public void setUp() {

        strategy = new LongArrayToListStrategy();
    }

    @Test
    public void ShouldConvertLongArrayToList() throws StrategyException {

        long[] array = new long[] {1, 2L, 5, Long.MIN_VALUE};
        List<Long> result = strategy.resolve(array);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0), new Long(1));
        assertEquals(result.get(1), new Long(2));
        assertEquals(result.get(2), new Long(5));
        assertEquals(result.get(3), new Long(Long.MIN_VALUE));
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws StrategyException {

        Long[] array = new Long[] {1L, 2L};
        strategy.resolve(array);
        fail();
    }
}
