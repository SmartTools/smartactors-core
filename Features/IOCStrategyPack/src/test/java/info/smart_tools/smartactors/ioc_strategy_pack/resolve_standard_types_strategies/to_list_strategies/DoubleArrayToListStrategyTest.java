package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DoubleArrayToListStrategyTest {

    private DoubleArrayToListStrategy strategy;

    @Before
    public void setUp() {

        strategy = new DoubleArrayToListStrategy();
    }

    @Test
    public void ShouldConvertDoubleArrayToList() throws StrategyException {

        double[] array = new double[] {12.0, 5.7, 34.33};
        List<Double> result = strategy.resolve(array);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0), new Double(12.0));
        assertEquals(result.get(1), new Double(5.7));
        assertEquals(result.get(2), new Double(34.33));
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws StrategyException {

        Double[] array = new Double[] {1.0, 2.6};
        strategy.resolve(array);
        fail();
    }
}
