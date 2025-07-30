package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal.*;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal.FloatToBigDecimalStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal.StringToBigDecimalStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_date_strategies.StringToDateStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_integer_strategies.DoubleToIntStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_integer_strategies.StringToIntStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_self.ClassToClassStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.ObjectToStringStrategy;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class StrategiesTest {
    @Test
    public void DoubleToBigDecimalTest() throws Exception {
        IStrategy strategy = new DoubleToBigDecimalStrategy();
        assertEquals(strategy.resolve(new Double("2")), new BigDecimal("2.0"));
    }

    @Test
    public void FloatToBigDecimalTest() throws Exception {
        IStrategy strategy = new FloatToBigDecimalStrategy();
        assertEquals(strategy.resolve(new Float("2")), new BigDecimal("2.0"));
    }

    @Test
    public void IntegerToBigDecimalTest() throws Exception {
        IStrategy strategy = new IntegerToBigDecimalStrategy();
        assertEquals(strategy.resolve(new Integer("2")), new BigDecimal("2"));
    }

    @Test
    public void StringToBigDecimalTest() throws Exception {
        IStrategy strategy = new StringToBigDecimalStrategy();
        assertEquals(strategy.resolve("2.0"), new BigDecimal("2.0"));
    }

    @Test
    public void StringToLocalDateTimeTest() throws Exception {
        IStrategy strategy = new StringToDateStrategy();
        assertEquals(strategy.resolve("2015-08-04T10:11:30"), LocalDateTime.parse("2015-08-04T10:11:30"));
    }

    @Test
    public void DoubleToIntegerTest() throws Exception {
        IStrategy strategy = new DoubleToIntStrategy();
        assertEquals(strategy.resolve(new Double("2.0")), Integer.valueOf("2"));
    }

    @Test
    public void StringToIntegerTest() throws Exception {
        IStrategy strategy = new StringToIntStrategy();
        assertEquals(strategy.resolve("2"), Integer.valueOf("2"));
    }

    @Test
    public void IntegerToString() throws Exception {
        IStrategy strategy = new ObjectToStringStrategy();
        assertEquals(strategy.resolve(new Double("2.0")), "2.0");
    }

    @Test
    public void ObjectToObject() throws Exception {
        Object object = new Object();
        IStrategy strategy = new ClassToClassStrategy();
        assertEquals(strategy.resolve(object), object);
    }
}
