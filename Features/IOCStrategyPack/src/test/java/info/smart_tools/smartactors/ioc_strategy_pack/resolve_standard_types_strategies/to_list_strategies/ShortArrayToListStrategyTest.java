package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ShortArrayToListStrategyTest {

    private ShortArrayToListStrategy strategy;

    @Before
    public void setUp() {

        strategy = new ShortArrayToListStrategy();
    }

    @Test
    public void ShouldConvertShortArrayToList() throws StrategyException {

        short[] array = new short[] {1, 2, 3};
        List<Short> result = strategy.resolve(array);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0), new Short("1"));
        assertEquals(result.get(1), new Short("2"));
        assertEquals(result.get(2), new Short("3"));
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws StrategyException {

        Short[] array = new Short[] {1, 2};
        strategy.resolve(array);
        fail();
    }
}
