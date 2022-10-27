package info.smart_tools.smartactors.ioc_strategy_pack.resolve_iobject_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
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

public class MapToIObjectStrategyTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }


    @Test
    public void ShouldCorrectConvertMapToIObject() throws Exception {

        IFieldName fieldName1 = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "key1");
        IFieldName fieldName2 = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "key2");

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key1", 123);
        map.put("key2", "value");

        IObject result = (new MapToIObjectStrategy()).resolve(map);
        assertEquals(result.getValue(fieldName1), 123);
        assertEquals(result.getValue(fieldName2), "value");
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_AnyErrorIsOccurred() throws Exception {

        (new MapToIObjectStrategy()).resolve(new Object());
        fail();
    }

    @Test(expected = StrategyException.class)
    public void ShouldThrowException_When_NullIsPassed() throws Exception {

        (new MapToIObjectStrategy()).resolve(null);
        fail();
    }
}
