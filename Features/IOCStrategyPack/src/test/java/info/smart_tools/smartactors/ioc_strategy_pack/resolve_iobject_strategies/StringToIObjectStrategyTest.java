package info.smart_tools.smartactors.ioc_strategy_pack.resolve_iobject_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(String.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class StringToIObjectStrategyTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }


    private StringToIObjectStrategy strategy;

    @Before
    public void setUp() throws Exception {

        strategy = new StringToIObjectStrategy();
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

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_StringIsIncorrect() throws Exception {

        String invalid = "invalid";
        strategy.resolve(invalid);
        fail();
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_AnyErrorIsOccurred() throws Exception {

        mockStatic(String.class);
        when(String.valueOf(anyString())).thenThrow(new RuntimeException(""));

        strategy.resolve("");
        fail();
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_NullIsPassed() throws Exception {

        strategy.resolve(null);
        fail();
    }
}
