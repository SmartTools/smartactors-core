package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ObjectArrayToListResolveDependencyStrategyTest {

    private ObjectArrayToListResolveDependencyStrategy strategy;

    @Before
    public void setUp() {

        strategy = new ObjectArrayToListResolveDependencyStrategy();
    }

    @Test
    public void ShouldConvertObjectArrayToList() throws ResolveDependencyStrategyException {

        Object object = new Object();
        Object[] array = new Object[] {1L, "string", object};
        List<Short> result = strategy.resolve(array);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0), new Long(1L));
        assertEquals(result.get(1), "string");
        assertEquals(result.get(2), object);
    }

    @Test
    public void ShouldConvertConcreteTypeArrayToList() throws ResolveDependencyStrategyException {

        Integer[] array = new Integer[] {new Integer(1), new Integer(143)};
        List<Short> result = strategy.resolve(array);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), new Integer(1));
        assertEquals(result.get(1), new Integer(143));
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws ResolveDependencyStrategyException {

        strategy.resolve(null);
        fail();
    }
}
