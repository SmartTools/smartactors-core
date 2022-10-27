package info.smart_tools.smartactors.ioc_strategy_pack.resolve_iobject_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class MapToIObjectStrategyTest {

    private MapToIObjectStrategy strategy;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        strategy = new MapToIObjectStrategy();
    }

    @Test
    public void ShouldCorrectConvertMapToIObject() throws Exception {

        IKey key = mock(IKey.class);
        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(key);

        IFieldName fieldName1 = mock(IFieldName.class);
        IFieldName fieldName2 = mock(IFieldName.class);
        when(IOC.resolve(key, "key1")).thenReturn(fieldName1);
        when(IOC.resolve(key, "key2")).thenReturn(fieldName2);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key1", 123);
        map.put("key2", "value");

        IObject result = strategy.resolve(map);
        assertEquals(result.getValue(fieldName1), 123);
        assertEquals(result.getValue(fieldName2), "value");
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_AnyErrorIsOccurred() throws Exception {

        when(IOC.resolve(any(IKey.class), anyString())).thenThrow(new ResolutionException(""));

        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        strategy.resolve(map);
        fail();
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_NullIsPassed() throws Exception {

        strategy.resolve(null);
        fail();
    }
}
