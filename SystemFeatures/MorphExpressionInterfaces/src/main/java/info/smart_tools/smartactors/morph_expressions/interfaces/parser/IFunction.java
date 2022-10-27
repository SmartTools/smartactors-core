package info.smart_tools.smartactors.morph_expressions.interfaces.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.ExecutionException;

import java.util.Map;

/**
 * A function for using in an expression.
 * For example: sqr(x) - this expression required a 'sqr' function,
 * for parse this expression, should create and register
 * 'sqr' function in a parser.
 *
 * @see IParser#registerFunction(String, IFunction)
 * @see IParser#registerFunctions(Map)
 */
@FunctionalInterface
public interface IFunction {
    /**
     * Apply a function to array of an arguments.
     *
     * @param args the arguments for function.
     * @return a result of the function.
     * @throws ExecutionException when errors occur during the function.
     */
    Object apply(Object... args) throws ExecutionException;

}
