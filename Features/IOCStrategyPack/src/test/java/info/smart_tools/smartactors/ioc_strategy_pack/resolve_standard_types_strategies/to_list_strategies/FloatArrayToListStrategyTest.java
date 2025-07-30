package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FloatArrayToListStrategyTest {

    private FloatArrayToListStrategy strategy;

    @Before
    public void setUp() {

        strategy = new FloatArrayToListStrategy();
    }

    @Test
    public void ShouldConvertFloatArrayToList() throws StrategyException {

        float[] array = new float[] {(float) 12.0, (float) 5.7};
        List<Float> result = strategy.resolve(array);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), new Float(12.0));
        assertEquals(result.get(1), new Float(5.7));
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws StrategyException {

        Float[] array = new Float[] {new Float(1.0)};
        strategy.resolve(array);
        fail();
    }
}
