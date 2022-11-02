package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BooleanArrayToListStrategyTest {

    private BooleanArrayToListStrategy strategy;

    @Before
    public void setUp() {

        strategy = new BooleanArrayToListStrategy();
    }

    @Test
    public void ShouldConvertBooleanArrayToList() throws StrategyException {

        boolean[] array = new boolean[] {true, false};
        List<Boolean> result = strategy.resolve(array);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), true);
        assertEquals(result.get(1), false);
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws StrategyException {

        boolean var = false;
        strategy.resolve(var);
        fail();
    }
}
