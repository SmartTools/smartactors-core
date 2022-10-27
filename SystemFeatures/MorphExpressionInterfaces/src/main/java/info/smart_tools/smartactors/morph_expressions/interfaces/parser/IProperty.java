package info.smart_tools.smartactors.morph_expressions.interfaces.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.ExecutionException;

import java.util.Map;

/**
 * A property for using in an expression.
 * For example: email {@literal &&} x {@literal <} 100 - this expression required the 'email' property,
 * for parse this expression, should create and register
 * 'email' property in a parser.
 *
 * @see IParser#registerProperty(String, IProperty)
 * @see IParser#registerProperties(Map)
 */
@FunctionalInterface
public interface IProperty {
    /**
     * Apply a property to a some scope.
     *
     * @param scope the container with necessary for property values.
     *              The Property can use a few or all values in the given scope.
     * @return a result of the property.
     * @throws ExecutionException when errors occur during the function.
     */
    Object apply(Map<String, Object> scope) throws ExecutionException;

}
