package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ByteArrayToListResolveDependencyStrategyTest {

    private ByteArrayToListResolveDependencyStrategy strategy;

    @Before
    public void setUp() {

        strategy = new ByteArrayToListResolveDependencyStrategy();
    }

    @Test
    public void ShouldConvertByteArrayToList() throws ResolveDependencyStrategyException {

        byte[] array = new byte[] {(byte)0xba, (byte)0x8a};
        List<Byte> result = strategy.resolve(array);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), new Byte((byte) 0xba));
        assertEquals(result.get(1), new Byte((byte) 0x8a));
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_ErrorIsOccurred() throws ResolveDependencyStrategyException {

        String invalid = "invalid";
        strategy.resolve(invalid);
        fail();
    }
}
