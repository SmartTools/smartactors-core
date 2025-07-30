package info.smart_tools.smartactors.morph_expressions.parser.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TypeConversionExceptionTest {

    @Test(expected = TypeConversionException.class)
    public void checkMessageMethod()
            throws TypeConversionException {
        String str = "test";
        TypeConversionException exception = new TypeConversionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

}
