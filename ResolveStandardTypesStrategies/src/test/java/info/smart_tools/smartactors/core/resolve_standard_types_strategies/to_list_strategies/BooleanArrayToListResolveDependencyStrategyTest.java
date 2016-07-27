package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BooleanArrayToListResolveDependencyStrategyTest {

    private BooleanArrayToListResolveDependencyStrategy strategy;

    @Before
    public void setUp() {

        strategy = new BooleanArrayToListResolveDependencyStrategy();
    }

    @Test
    public void ShouldConvertBooleanArrayToList() throws ResolveDependencyStrategyException {

        boolean[] array = new boolean[] {true, false};
        List<Boolean> result = strategy.resolve(array);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), true);
        assertEquals(result.get(1), false);
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws ResolveDependencyStrategyException {

        boolean var = false;
        strategy.resolve(var);
        fail();
    }
}
