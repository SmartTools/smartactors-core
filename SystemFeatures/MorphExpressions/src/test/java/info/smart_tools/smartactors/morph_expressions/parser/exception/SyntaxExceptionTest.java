package info.smart_tools.smartactors.morph_expressions.parser.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SyntaxExceptionTest {

    @Test(expected = SyntaxException.class)
    public void checkMessageMethod()
            throws SyntaxException {
        String str = "test";
        SyntaxException exception = new SyntaxException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = SyntaxException.class)
    public void checkMessageAndCauseMethod()
            throws SyntaxException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        SyntaxException exception = new SyntaxException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

}
