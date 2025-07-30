package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.EvaluatingExpressionException;

import java.util.Map;

/**
 * A single evaluation node of syntax tree.
 */
@FunctionalInterface
interface IEvaluationNode {
    /**
     * Evaluates a parsed expression by the given scope used its part of syntax tree.
     *
     * @param scope the scope with values to be evaluated.
     * @return a result of evaluation of the given scope.
     * @throws EvaluatingExpressionException when errors occur during the evaluation.
     */
    Object eval(Map<String, Object> scope) throws EvaluatingExpressionException;

}
