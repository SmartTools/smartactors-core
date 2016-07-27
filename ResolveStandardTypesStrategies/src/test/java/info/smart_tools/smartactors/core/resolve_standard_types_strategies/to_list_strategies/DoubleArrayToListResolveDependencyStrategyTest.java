package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DoubleArrayToListResolveDependencyStrategyTest {

    private DoubleArrayToListResolveDependencyStrategy strategy;

    @Before
    public void setUp() {

        strategy = new DoubleArrayToListResolveDependencyStrategy();
    }

    @Test
    public void ShouldConvertDoubleArrayToList() throws ResolveDependencyStrategyException {

        double[] array = new double[] {12.0, 5.7, 34.33};
        List<Double> result = strategy.resolve(array);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0), new Double(12.0));
        assertEquals(result.get(1), new Double(5.7));
        assertEquals(result.get(2), new Double(34.33));
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws ResolveDependencyStrategyException {

        Double[] array = new Double[] {1.0, 2.6};
        strategy.resolve(array);
        fail();
    }
}
