package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IntArrayToListStrategyTest {

    private IntArrayToListStrategy strategy;

    @Before
    public void setUp() {

        strategy = new IntArrayToListStrategy();
    }

    @Test
    public void ShouldConvertIntArrayToList() throws StrategyException {

        int[] array = new int[] {1, 2, 5, 7};
        List<Integer> result = strategy.resolve(array);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0), new Integer(1));
        assertEquals(result.get(1), new Integer(2));
        assertEquals(result.get(2), new Integer(5));
        assertEquals(result.get(3), new Integer(7));
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws StrategyException {

        Integer[] array = new Integer[] {1, 2};
        strategy.resolve(array);
        fail();
    }
}
