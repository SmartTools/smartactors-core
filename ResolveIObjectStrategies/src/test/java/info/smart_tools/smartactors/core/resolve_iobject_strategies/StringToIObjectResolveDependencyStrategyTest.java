package info.smart_tools.smartactors.core.resolve_iobject_strategies;

import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(String.class)
public class StringToIObjectResolveDependencyStrategyTest {

    private StringToIObjectResolveDependencyStrategy strategy;

    @Before
    public void setUp() throws Exception {

        strategy = new StringToIObjectResolveDependencyStrategy();
    }

    @Test
    public void ShouldCorrectConvertStringToIObject() throws Exception {

        String json = "{" +
            "\"key1\": 123," +
            "\"key2\": \"value\"" +
        "}";

        IObject result = strategy.resolve(json);
        assertEquals(result.getValue(new FieldName("key1")), 123);
        assertEquals(result.getValue(new FieldName("key2")), "value");
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_StringIsIncorrect() throws Exception {

        String invalid = "invalid";
        strategy.resolve(invalid);
        fail();
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_AnyErrorIsOccurred() throws Exception {

        mockStatic(String.class);
        when(String.valueOf(anyString())).thenThrow(new RuntimeException(""));

        strategy.resolve("");
        fail();
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void ShouldThrowException_When_NullIsPassed() throws Exception {

        strategy.resolve(null);
        fail();
    }
}
