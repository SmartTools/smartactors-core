package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.parser.exception.TypeConversionException;
import org.junit.Test;

import static info.smart_tools.smartactors.morph_expressions.parser.TypeConverter.convertToBool;
import static info.smart_tools.smartactors.morph_expressions.parser.TypeConverter.convertToDouble;
import static org.junit.Assert.assertEquals;

public class TypeConverterTest {

    private final Object _null = null;
    private final Object nonNull = new Object();
    private final Boolean _true = true;
    private final Boolean _false = false;
    private final Number zero = 0;
    private final Number nan = Double.NaN;
    private final Number num = 4.2;
    private final String emptyStr = "";
    private final String nonEmptyStr = "non empty!";

    @Test
    public void should_ConvertValueToBooleanLikeJavaScript() {
        assertEquals(true, convertToBool(nonNull));
        assertEquals(true, convertToBool(_true));
        assertEquals(true, convertToBool(num));
        assertEquals(true, convertToBool(nonEmptyStr));

        assertEquals(false, convertToBool(_null));
        assertEquals(false, convertToBool(_false));
        assertEquals(false, convertToBool(nan));
        assertEquals(false, convertToBool(zero));
        assertEquals(false, convertToBool(emptyStr));
    }

    @Test
    public void should_ConvertValueToDoubleLikeJavaScript() throws TypeConversionException {
        assertEquals(4.2, convertToDouble(num), 0d);
        assertEquals(1d, convertToDouble(_true), 0d);

        assertEquals(0d, convertToDouble(_false), 0d);
        assertEquals(0d, convertToDouble(zero), 0d);
        assertEquals(0d, convertToDouble(emptyStr), 0d);
        assertEquals(Double.NaN, convertToDouble(_null), 0d);
        assertEquals(Double.NaN, convertToDouble(nan), 0d);
        assertEquals(Double.NaN, convertToDouble(nonEmptyStr), 0d);
    }

    @Test(expected = TypeConversionException.class)
    public void should_ThrowException_WithReason_UnrecognizedTypeForConvertToDouble() throws TypeConversionException {
        convertToDouble(nonNull);
    }

}
