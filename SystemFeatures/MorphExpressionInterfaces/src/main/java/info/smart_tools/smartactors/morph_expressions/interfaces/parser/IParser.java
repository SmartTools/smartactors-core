package info.smart_tools.smartactors.morph_expressions.interfaces.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.ParsingException;

import java.util.Map;

/**
 * Parser of an expressions.
 *
 * @see IParser#parse(String)
 * @see IParser#registerProperty(String, IProperty)
 * @see IParser#registerProperties(Map)
 * @see IParser#registerFunction(String, IFunction)
 * @see IParser#registerFunctions(Map)
 * @see IParser#unregisterProperty(String)
 * @see IParser#unregisterFunction(String)
 */
public interface IParser {
    /**
     * Parses an expression and creates a rule of evaluating the given expression.
     *
     * @param expression - expression to be parsed.
     * @return a rule of evaluating a parsed given expression.
     * @throws ParsingException when errors occur during parsing the expression.
     */
    IEvaluator parse(String expression) throws ParsingException;

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
    void registerProperty(String lexeme, IProperty property);

    /**
     * Associates a package of external properties with the specified lexeme in a parser.
     * If the parser previously contained a property for
     * the key from given package, the old value is replaced by the specified value.
     *
     * @param properties the package with properties to be registered in the parser.
     *
     * @see IProperty
     */
    void registerProperties(Map<String, IProperty> properties);

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
    void registerFunction(String lexeme, IFunction function);

    /**
     * Associates a package of external functions with the specified lexeme in a parser.
     * If the parser previously contained a function for
     * the key from given package, the old value is replaced by the specified value.
     *
     * @param functions the package with functions to be registered in the parser.
     *
     * @see IFunction
     */
    void registerFunctions(Map<String, IFunction> functions);

    /**
     * Unregisters an external property for a key from a parser if it is present.
     *
     * @param lexeme the key whose mapping is to be removed from the map.
     *
     * @see IProperty
     */
    void unregisterProperty(String lexeme);

    /**
     * Unregisters an external function for a key from a parser if it is present.
     *
     * @param lexeme the key whose mapping is to be removed from the map.
     *
     * @see IProperty
     */
    void unregisterFunction(String lexeme);

}
