package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CharArrayToListResolveDependencyStrategyTest {

    private CharArrayToListResolveDependencyStrategy strategy;

    @Before
    public void setUp() {

        strategy = new CharArrayToListResolveDependencyStrategy();
    }

    @Test
    public void ShouldConvertCharArrayToList() throws ResolveDependencyStrategyException {

        char[] array = new char[] {'1', 'd', '+'};
        List<Byte> result = strategy.resolve(array);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0), new Character('1'));
        assertEquals(result.get(1), new Character('d'));
        assertEquals(result.get(2), new Character('+'));
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws ResolveDependencyStrategyException {

        Character[] array = new Character[] {'1', '2'};
        strategy.resolve(array);
        fail();
    }
}
