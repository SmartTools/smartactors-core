package info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EvaluatingExpressionExceptionTest {

    @Test(expected = EvaluatingExpressionException.class)
    public void checkMessageMethod()
            throws EvaluatingExpressionException {
        String str = "test";
        EvaluatingExpressionException exception = new EvaluatingExpressionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = EvaluatingExpressionException.class)
    public void checkMessageAndCauseMethod()
            throws EvaluatingExpressionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        EvaluatingExpressionException exception = new EvaluatingExpressionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

}
