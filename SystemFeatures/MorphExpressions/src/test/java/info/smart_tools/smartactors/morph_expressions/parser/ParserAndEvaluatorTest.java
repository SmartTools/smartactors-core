package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IFunction;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IParser;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IProperty;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.EvaluatingExpressionException;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.ParsingException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ParserAndEvaluatorTest {

    private static IParser parser = ExpressionParser.create();

    @BeforeClass
    public static void setUp() {
        parser.registerProperties(new HashMap<String, IProperty>() {{
            put("required", scope -> scope.get("x") != null);
            put("today", scope -> new Date());
        }});
        parser.registerFunctions(new HashMap<String, IFunction>() {{
            put("sqr", args -> (double) args[0] * (double) args[0]);
            put("constant", args -> "constant");
            put("sum", args -> (double) args[0] + (double) args[1] + (double) args[2]);
        }});
    }

    // <smartforms>
    //--------------------------------------------------------------------------------------------------------------
    @Test
    public void shouldParseAndEvalPredefinedConstant() throws ParsingException, EvaluatingExpressionException {
        assertEquals(true, parser.parse(("true")).eval());
        assertEquals(false, parser.parse(("false")).eval());
    }

    @Test
    public void shouldParseAndEvalValidNumbers() throws ParsingException, EvaluatingExpressionException {
        assertEquals(0d, parser.parse("0").eval(), 0d);
        assertEquals(5d, parser.parse("5").eval(), 0d);
        assertEquals(5.4, parser.parse("5.4").eval(), 0d);
        assertEquals(5.4, parser.parse("005.4").eval(), 0d);
        assertEquals(5.4, parser.parse("005.400").eval(), 0d);
        assertEquals(2d, parser.parse("2.").eval(), 0d);
    }

    @Test
    public void should_Throw_SyntaxException_Reason_Invalid_Number() {
        try {
            parser.parse(".");
            fail("Expected SyntaxException");
        } catch (ParsingException ignored) { }
        try {
            parser.parse("3.2.1");
            fail("Expected SyntaxException");
        } catch (ParsingException ignored) { }
    }

    @Test
    public void shouldParseAndEvalString() throws ParsingException, EvaluatingExpressionException {
        assertEquals("string", parser.parse(("\"string\"")).eval());
        assertEquals("string", parser.parse(("\'string\'")).eval());
    }

    @Test
    public void should_Throw_SyntaxException_Reason_Invalid_String() {
        try {
            parser.parse("\"string");
            fail("Expected SyntaxException");
        } catch (ParsingException ignored) { }
        try {
            parser.parse("string\"");
            fail("Expected SyntaxException");
        } catch (ParsingException ignored) { }
        try {
            parser.parse("'string\"");
            fail("Expected SyntaxException");
        } catch (ParsingException ignored) { }
        try {
            parser.parse("\"string'");
            fail("Expected SyntaxException");
        } catch (ParsingException ignored) { }
    }

    @Test
    public void shouldParseAndEvalAdditionOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(3d, parser.parse("1 + 2").eval(), 0d);
        assertEquals(7d, parser.parse("1 + 2 + 4").eval(), 0d);
        assertEquals(9d, parser.parse("1 + 2 * 4").eval(), 0d);
        assertEquals(2d, parser.parse("+2").eval(), 0d);
    }

    @Test
    public void shouldParseAndEvalIncreaseOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(3d, parser.parse("++2").eval(), 0d);
        assertEquals(4d, parser.parse("1 + ++2").eval(), 0d);
    }

    @Test
    public void shouldParseAndEvalSubtractOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(-1d, parser.parse("1 - 2").eval(), 0d);
        assertEquals(-1d, parser.parse("1 - 1 - 1").eval(), 0d);
        assertEquals(-7d, parser.parse("1 - 2 * 4").eval(), 0d);
        assertEquals(-2d, parser.parse("-2").eval(), 0d);
    }

    @Test
    public void shouldParseAndEvalDecreaseOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(1d, parser.parse("--2").eval(), 0d);
        assertEquals(0d, parser.parse("1 - --2").eval(), 0d);
    }

    @Test
    public void shouldParseAndEvalMultiplyOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(8d, parser.parse("4 * 2").eval(), 0d);
        assertEquals(8d, parser.parse("2 * 2 * 2").eval(), 0d);
        assertEquals(-4d, parser.parse("2 * -2").eval(), 0d);
    }

    @Test
    public void shouldParseAndEvalDivideOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(2d, parser.parse("4 / 2").eval(), 0d);
        assertEquals(2d, parser.parse("8 / 2 / 2").eval(), 0d);
        assertEquals(-1d, parser.parse("2 / -2").eval(), 0d);
    }

    @Test
    public void shouldModOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(2d, parser.parse("8 % 3").eval(), 0d);
        assertEquals(1d, parser.parse("5 % -2").eval(), 0d);
    }

    @Test
    public void shouldParseAndEvalEqualOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(false, parser.parse("2 == 3").eval());
        assertEquals(true, parser.parse("2 == 2").eval());
        assertEquals(true, parser.parse("true == 2 > 1").eval());
    }

    @Test
    public void shouldParseAndEvalNotEqualOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(false, parser.parse("2 != 2").eval());
        assertEquals(true, parser.parse("2 != 3").eval());
        assertEquals(true, parser.parse("true != 1 > 2").eval());
    }

    @Test
    public void shouldParseAndEvalGreaterOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(false, parser.parse("2 > 3").eval());
        assertEquals(false, parser.parse("2 > 2").eval());
        assertEquals(true, parser.parse("2 > 1").eval());
        assertEquals(true, parser.parse("1 > 2 - 3").eval());
    }

    @Test
    public void shouldParseAndEvalGreaterOrEqualOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(false, parser.parse("2 >= 3").eval());
        assertEquals(true, parser.parse("2 >= 2").eval());
        assertEquals(true, parser.parse("2 >= 1").eval());
        assertEquals(true, parser.parse("1 >= 2 - 1").eval());
    }

    @Test
    public void shouldParseAndEvalLessOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(true, parser.parse("2 < 3").eval());
        assertEquals(false, parser.parse("2 < 2").eval());
        assertEquals(false, parser.parse("2 < 1").eval());
        assertEquals(false, parser.parse("1 < 2 - 1").eval());
    }

    @Test
    public void shouldParseAndEvalLessOrEqualOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(true, parser.parse("2 <= 3").eval());
        assertEquals(true, parser.parse("2 <= 2").eval());
        assertEquals(false, parser.parse("2 <= 1").eval());
        assertEquals(true, parser.parse("1 <= 2 - 1").eval());
    }

    @Test
    public void shouldParseAndEvalLogicalOrOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(2d, parser.parse("2 || 3").eval(), 0d);
        assertEquals(2d, parser.parse("2 || 0").eval(), 0d);
        assertEquals(true, parser.parse("true || true").eval());
        assertEquals(true, parser.parse("true || false").eval());
        assertEquals(true, parser.parse("false || true").eval());
        assertEquals(false, parser.parse("false || false").eval());
        assertEquals(true, parser.parse("false || 0 == 0").eval());
    }

    @Test
    public void shouldParseAndEvalLogicalAndOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(3d, parser.parse("2 && 3").eval(), 0d);
        assertEquals(0d, parser.parse("2 && 0").eval(), 0d);
        assertEquals(true, parser.parse("true && true").eval());
        assertEquals(false, parser.parse("true && false").eval());
        assertEquals(false, parser.parse("false && true").eval());
        assertEquals(false, parser.parse("false && false").eval());
        assertEquals(true, parser.parse("true && 0 == 0").eval());
    }

    @Test
    public void shouldParseAndEvalLogicalNotOperator() throws ParsingException, EvaluatingExpressionException {
        assertEquals(false, parser.parse("!1").eval());
        assertEquals(true, parser.parse("!0").eval());
        assertEquals(false, parser.parse("!!0").eval());
        assertEquals(false, parser.parse("!true").eval());
        assertEquals(true, parser.parse("!false").eval());
    }

    @Test
    public void shouldParseAndEvalParentheses() throws ParsingException, EvaluatingExpressionException {
        assertEquals(1d, parser.parse("1 - (1 - 1)").eval(), 0d);
        assertEquals(6d, parser.parse("1 - ((2 - 3) - 4)").eval(), 0d);
        assertEquals(9d, parser.parse("3 * (2 + 1)").eval(), 0d);
        assertEquals(9d, parser.parse("(2 + 1) * 3").eval(), 0d);
    }

    @Test(expected = ParsingException.class)
    public void should_ThrowException_Reason_Does_Not_Contains_Close_Parentheses() throws ParsingException {
        parser.parse("3 * (1 + 2");
    }

    @Test(expected = ParsingException.class)
    public void should_ThrowException_Reason_Does_Not_Contains_Open_Parentheses() throws ParsingException {
        parser.parse("1 + 2)");
    }

    @Test
    public void shouldParseAndEvalVariable() throws ParsingException, EvaluatingExpressionException {
        Map<String, Object> scope = new HashMap<String, Object>(1) {{
           put("x", 5d);
        }};
        assertEquals(5d, parser.parse("x").eval(scope), 0d);

        scope = new HashMap<String, Object>() {{
           put("x", new HashMap<String, Object>() {{ put("y", 5); }});
        }};
        assertEquals(5, (int) parser.parse("x.y").eval(scope));

        scope = new HashMap<String, Object>() {{
            put("x", new HashMap<String, Object>() {{ put("y", 7); }});
            put("z", 2);
        }};
        assertEquals(9d, parser.parse("x.y + z").eval(scope), 0d);
    }

    @Test(expected = EvaluatingExpressionException.class)
    public void should_ThrowException_Reason_Variable_Is_Not_Defined() throws ParsingException, EvaluatingExpressionException {
        parser.parse(("x")).eval(Collections.emptyMap());
    }

    @Test
    public void shouldParseAndEvalAndEvalComputedProperty() throws ParsingException, EvaluatingExpressionException {
        Map<String, Object> emptyScope = new HashMap<>(1);
        Map<String, Object> scope = new HashMap<String, Object>() {{
           put("x", 5);
        }};
        assertEquals(false, parser.parse("required").eval(emptyScope));
        assertEquals(true, parser.parse("required").eval(scope));
        assertTrue(((Date) parser.parse("today").eval()).getTime() > 0);
    }

    @Test
    public void shouldParseAndEvalAndEvalFunctions() throws ParsingException, EvaluatingExpressionException {
        assertEquals("constant", parser.parse("constant()").eval());
        assertEquals(4d, parser.parse("sqr(2)").eval(), 0d);
        assertEquals(16d, parser.parse("sqr(sqr(2))").eval(), 0d);
        assertEquals(6d, parser.parse("sum(1, 2, 3)").eval(), 0d);
    }

    @Test(expected = EvaluatingExpressionException.class)
    public void should_ThrowException_Reason_Function_Is_Not_Defined() throws ParsingException, EvaluatingExpressionException {
        parser.parse("unregistered()").eval(Collections.emptyMap());
    }

    @Test(expected = ParsingException.class)
    public void should_ThrowException_Reason_Function_Definition_Does_Not_Contains_Close_Parentheses() throws ParsingException {
        parser.parse("unregistered(");
    }
    // </smartforms>
    //--------------------------------------------------------------------------------------------------------------

}
