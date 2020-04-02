package info.smart_tools.smartactors.morph_expressions.interfaces.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.EvaluatingExpressionException;

import java.util.Map;

/**
 * An interpreter of syntax tree for evaluating of a some parsed expression.
 */
public interface IEvaluator {
    /**
     * Evaluates a some parsed expression.
     *
     * @param <R> - a return result type.
     * @return the result of evaluation a some parsed expression.
     * @throws EvaluatingExpressionException when errors occur during the evaluation by rule.
     */
    <R> R eval() throws EvaluatingExpressionException;

    /**
     * Evaluates a parsed expression using scope with specific properties.
     *
     * @param scope - a container with the specific properties
     *              for evaluation by rule the some parsed expression.
     * @param <R> - a return result type.
     * @return the result of evaluation a some parsed expression.
     * @throws EvaluatingExpressionException when errors occur during the evaluation by rule.
     */
    <R> R eval(Map<String, Object> scope) throws EvaluatingExpressionException;

}
