package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IFunction;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

public class TokenTreeTest {

    @Test
    public void should_CreateTokenTreeWithValidOverloadedOperators() {
        final IFunction add = mock(IFunction.class);
        final IFunction neg = mock(IFunction.class);
        final IFunction div = mock(IFunction.class);
        final IFunction inc = mock(IFunction.class);
        Map<String, IFunction> operators = new HashMap<String, IFunction>() {{
            put("++", inc);
            put("+", add);
            put("!", neg);
            put("/", div);
        }};

        TokenTree tree = new TokenTree(operators);

        assertEquals(inc, getTokenByLexeme("++", tree).getFunction());
        assertEquals(add, getTokenByLexeme("+", tree).getFunction());
        assertEquals(neg, getTokenByLexeme("!", tree).getFunction());
        assertEquals(div, getTokenByLexeme("/", tree).getFunction());
    }

    @Test
    public void should_IgnoreFinalOperatorsInPackageWithOverloadedOperators() {
        final IFunction add = mock(IFunction.class);
        final IFunction neg = mock(IFunction.class);
        final IFunction div = mock(IFunction.class);
        final IFunction inc = mock(IFunction.class);
        final IFunction _true = mock(IFunction.class);
        final IFunction lpar = mock(IFunction.class);
        Map<String, IFunction> operators = new HashMap<String, IFunction>() {{
            put("++", inc);
            put("+", add);
            put("(", lpar);
            put("!", neg);
            put("/", div);
            put("true", _true);
        }};

        TokenTree tree = new TokenTree(operators);

        assertEquals(inc, getTokenByLexeme("++", tree).getFunction());
        assertEquals(add, getTokenByLexeme("+", tree).getFunction());
        assertEquals(neg, getTokenByLexeme("!", tree).getFunction());
        assertEquals(div, getTokenByLexeme("/", tree).getFunction());
        assertNotEquals(_true, getTokenByLexeme("true", tree).getFunction());
        assertNotEquals(lpar, getTokenByLexeme("(", tree).getFunction());
    }

    private Token getTokenByLexeme(String lexeme, TokenTree tree) {
        char[] sequence = lexeme.toCharArray();
        final int length = sequence.length;
        TokenTree.Node found = null;
        TokenTree.Node node = tree.getRoot();
        for (int i = 0; i < length; i++) {
            node = node.getChild(sequence[i]);
            if (node == null) break;
            found = node.getToken() != null ? node : found;
        }
        return found.getToken();
    }

}
