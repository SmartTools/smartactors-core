package info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParsingExceptionTest {

    @Test(expected = ParsingException.class)
    public void checkMessageMethod()
            throws ParsingException {
        String str = "test";
        ParsingException exception = new ParsingException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ParsingException.class)
    public void checkMessageAndCauseMethod()
            throws ParsingException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ParsingException exception = new ParsingException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

}
