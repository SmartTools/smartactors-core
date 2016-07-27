package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LongArrayToListResolveDependencyStrategyTest {

    private LongArrayToListResolveDependencyStrategy strategy;

    @Before
    public void setUp() {

        strategy = new LongArrayToListResolveDependencyStrategy();
    }

    @Test
    public void ShouldConvertLongArrayToList() throws ResolveDependencyStrategyException {

        long[] array = new long[] {1, 2L, 5, Long.MIN_VALUE};
        List<Long> result = strategy.resolve(array);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0), new Long(1));
        assertEquals(result.get(1), new Long(2));
        assertEquals(result.get(2), new Long(5));
        assertEquals(result.get(3), new Long(Long.MIN_VALUE));
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws ResolveDependencyStrategyException {

        Long[] array = new Long[] {1L, 2L};
        strategy.resolve(array);
        fail();
    }
}
