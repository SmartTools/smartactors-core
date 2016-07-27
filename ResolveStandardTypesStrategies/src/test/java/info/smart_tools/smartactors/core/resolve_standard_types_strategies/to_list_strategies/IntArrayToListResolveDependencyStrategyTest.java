package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IntArrayToListResolveDependencyStrategyTest {

    private IntArrayToListResolveDependencyStrategy strategy;

    @Before
    public void setUp() {

        strategy = new IntArrayToListResolveDependencyStrategy();
    }

    @Test
    public void ShouldConvertIntArrayToList() throws ResolveDependencyStrategyException {

        int[] array = new int[] {1, 2, 5, 7};
        List<Integer> result = strategy.resolve(array);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0), new Integer(1));
        assertEquals(result.get(1), new Integer(2));
        assertEquals(result.get(2), new Integer(5));
        assertEquals(result.get(3), new Integer(7));
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws ResolveDependencyStrategyException {

        Integer[] array = new Integer[] {1, 2};
        strategy.resolve(array);
        fail();
    }
}
