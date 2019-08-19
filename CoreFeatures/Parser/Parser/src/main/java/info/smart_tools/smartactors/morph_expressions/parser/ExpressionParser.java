package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IEvaluator;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IFunction;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IParser;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IProperty;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.EvaluatingExpressionException;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.ParsingException;
import info.smart_tools.smartactors.morph_expressions.parser.exception.SyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExpressionParser parses of an expressions with specified grammar and builds evaluator.
 * Uses {@link Lexer} and {@link RecursiveDescentParser} for analyze of the expressions
 * and creation a syntax tree.
 *
 * <p><code>NOTE:</code> parser using a wrappers for primitive type like an int and a double.
 * The all numeric types parser casts to <code>Double</code>.</p>
 *
 * @see Lexer
 * @see RecursiveDescentParser
 * @see IEvaluator
 */
public final class ExpressionParser implements IParser {

    private final Map<String, IProperty> properties = new HashMap<>(1);
    private final Map<String, IFunction> functions = new HashMap<>(1);

    private final Lexer lexer;
    private final RecursiveDescentParser parser;

    private ExpressionParser() {
        this.lexer = new Lexer();
        this.parser = new RecursiveDescentParser(properties, functions);
    }

    private ExpressionParser(final Map<String, IFunction> operators) {
        this.lexer = new Lexer(operators);
        this.parser = new RecursiveDescentParser(properties, functions);
    }

    /**
     * Constructs a ExpressionParser with default implementations of reserved operators.
     *
     * @see Lexer
     * @see TokenTree
     * @see Operators
     */
    public static ExpressionParser create() {
        return new ExpressionParser();
    }

    /**
     * Constructs a ExpressionParser with overloaded reserved operators.
     *
     * @param operators the reserved operators to be overloaded.
     */
    public static ExpressionParser create(final Map<String, IFunction> operators) {
        return new ExpressionParser(operators);
    }

    /**
     * Parses an expression and creates a rule of evaluating the given expression.
     * Uses {@link Lexer} and {@link RecursiveDescentParser} for analyze of the expressions
     * and creation a syntax tree.
     *
     * @param expression - expression to be parsed.
     * @return a rule of evaluating a parsed given expression.
     * @throws ParsingException when the given expression has a syntax error.
     *
     * @see Lexer
     * @see RecursiveDescentParser
     * @see IEvaluator
     */
    @Override
    public IEvaluator parse(final String expression) throws ParsingException {
        try {
            final List<Token> tokens = lexer.getTokens(expression);
            final IEvaluationNode ruleNode = parser.parse(tokens);
            return new IEvaluator() {

                private IEvaluationNode root = ruleNode;

                @Override
                public <R> R eval() throws EvaluatingExpressionException {
                    return (R) root.eval(null);
                }

                @Override
                public <R> R eval(final Map<String, Object> scope) throws EvaluatingExpressionException {
                    return (R) root.eval(scope);
                }

            };
        } catch (SyntaxException ex) {
            throw new ParsingException("Parsing error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Associates an external property with the specified lexeme in a parser.
     * If the parser previously contained a property for
     * the key, the old value is replaced by the specified value.
     *
     * @param lexeme the key with which the specified function is to be associated.
     * @param property the function to be associated with the specified key.
     *
     * @see IProperty
     */
    @Override
    public void registerProperty(final String lexeme, final IProperty property) {
        this.properties.put(lexeme, property);
    }

    /**
     * Associates a package of external properties with the specified lexeme in a parser.
     * If the parser previously contained a property for
     * the key from given package, the old value is replaced by the specified value.
     *
     * @param properties the package with properties to be registered in the parser.
     *
     * @see IProperty
     */
    @Override
    public void registerProperties(final Map<String, IProperty> properties) {
        this.properties.putAll(properties);
    }

    /**
     * Associates an external function with the specified lexeme in a parser.
     * If the parser previously contained a function for
     * the key, the old value is replaced by the specified value.
     *
     * @param lexeme the key with which the specified function is to be associated.
     * @param function the function to be associated with the specified key.
     *
     * @see IFunction
     */
    @Override
    public void registerFunction(final String lexeme, final IFunction function) {
        this.functions.put(lexeme, function);
    }

    /**
     * Associates a package of external functions with the specified lexeme in a parser.
     * If the parser previously contained a function for
     * the key from given package, the old value is replaced by the specified value.
     *
     * @param functions the package with functions to be registered in the parser.
     *
     * @see IFunction
     */
    @Override
    public void registerFunctions(final Map<String, IFunction> functions) {
        this.functions.putAll(functions);
    }

    /**
     * Unregisters an external property for a key from a parser if it is present.
     *
     * @param lexeme the key whose mapping is to be removed from the map.
     *
     * @see IProperty
     */
    @Override
    public void unregisterProperty(final String lexeme) {
        this.properties.remove(lexeme);
    }

    /**
     * Unregisters an external function for a key from a parser if it is present.
     *
     * @param lexeme the key whose mapping is to be removed from the map.
     *
     * @see IProperty
     */
    @Override
    public void unregisterFunction(final String lexeme) {
        this.functions.remove(lexeme);
    }

}
