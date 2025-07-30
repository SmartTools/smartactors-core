package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CharArrayToListStrategyTest {

    private CharArrayToListStrategy strategy;

    @Before
    public void setUp() {

        strategy = new CharArrayToListStrategy();
    }

    @Test
    public void ShouldConvertCharArrayToList() throws StrategyException {

        char[] array = new char[] {'1', 'd', '+'};
        List<Byte> result = strategy.resolve(array);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0), new Character('1'));
        assertEquals(result.get(1), new Character('d'));
        assertEquals(result.get(2), new Character('+'));
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws StrategyException {

        Character[] array = new Character[] {'1', '2'};
        strategy.resolve(array);
        fail();
    }
}
