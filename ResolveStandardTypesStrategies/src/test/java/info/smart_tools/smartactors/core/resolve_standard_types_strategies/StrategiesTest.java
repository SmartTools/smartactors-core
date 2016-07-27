package info.smart_tools.smartactors.core.resolve_standard_types_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_bigdecimal.DoubleToBigDecimalResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_bigdecimal.StringToBigDecimalResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_date_strategies.StringToDateResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_integer_strategies.DoubleToIntResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_integer_strategies.StringToIntResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.ObjectToStringResolveDependencyStrategy;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class StrategiesTest {
    @Test
    public void DoubleToBigDecimalTest() throws Exception {
        IResolveDependencyStrategy strategy = new DoubleToBigDecimalResolveDependencyStrategy();
        assertEquals(strategy.resolve(new Double("2")), new BigDecimal("2.0"));
    }

    @Test
    public void StringToBigDecimalTest() throws Exception {
        IResolveDependencyStrategy strategy = new StringToBigDecimalResolveDependencyStrategy();
        assertEquals(strategy.resolve("2.0"), new BigDecimal("2.0"));
    }

    @Test
    public void StringToLocalDateTimeTest() throws Exception {
        IResolveDependencyStrategy strategy = new StringToDateResolveDependencyStrategy();
        assertEquals(strategy.resolve("2015-08-04T10:11:30"), LocalDateTime.parse("2015-08-04T10:11:30"));
    }

    @Test
    public void DoubleToIntegerTest() throws Exception {
        IResolveDependencyStrategy strategy = new DoubleToIntResolveDependencyStrategy();
        assertEquals(strategy.resolve(new Double("2.0")), Integer.valueOf("2"));
    }

    @Test
    public void StringToIntegerTest() throws Exception {
        IResolveDependencyStrategy strategy = new StringToIntResolveDependencyStrategy();
        assertEquals(strategy.resolve("2"), Integer.valueOf("2"));
    }

    @Test
    public void IntegerToString() throws Exception {
        IResolveDependencyStrategy strategy = new ObjectToStringResolveDependencyStrategy();
        assertEquals(strategy.resolve(new Double("2.0")), "2.0");
    }
}
