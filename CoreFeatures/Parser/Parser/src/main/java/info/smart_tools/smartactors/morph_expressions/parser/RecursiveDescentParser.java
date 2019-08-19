package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IFunction;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IProperty;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.EvaluatingExpressionException;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.ExecutionException;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.ParsingException;
import info.smart_tools.smartactors.morph_expressions.parser.exception.SyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RecursiveDescentParser is a kind of top-down parser built from a set of mutually
 * recursive procedures where each such procedure implements one of the productions of the grammar.
 *
 * <p>Thus the structure of the resulting program closely mirrors
 * that of the grammar it recognizes.</p>
 */
class RecursiveDescentParser {

    private final Map<String, IProperty> properties;
    private final Map<String, IFunction> functions;

    /**
     * Constructs a RecursiveDescentParser with an extended properties and functions.
     *
     * @param properties the extended properties which may be used in expressions.
     * @param functions the extended function which may be used in expressions.
     *
     * @see IProperty
     * @see IFunction
     */
    RecursiveDescentParser(
            final Map<String, IProperty> properties,
            final Map<String, IFunction> functions
    ) {
        this.properties = properties;
        this.functions = functions;
    }

    /**
     * Parses a list of tokens and create syntax tree which to be used in an evaluator.
     * Detects an illegal grammar or a syntax errors in an expressions.
     *
     * @param tokens the list of tokens to be parsed.
     * @return the root node of evaluation(syntax tree).
     * @throws ParsingException when the given list of tokens is null or
     *              detected the syntax errors in the parsed expression.
     */
    IEvaluationNode parse(final List<Token> tokens) throws ParsingException {
        try {
            LinkedList<Token> _tokens = new LinkedList<>(tokens);
            IEvaluationNode root = processOperatorOr(_tokens);
            if (!_tokens.isEmpty()) {
                throw new ParsingException("Unexpected end of expression!");
            }
            return root;
        } catch (SyntaxException ex) {
            throw new ParsingException(ex.getMessage(), ex);
        } catch (NullPointerException ex) {
            throw new ParsingException("The given list of tokens is null!", ex);
        }
    }

    private IEvaluationNode processOperatorOr(final LinkedList<Token> tokens) {
        IEvaluationNode node = processOperatorAnd(tokens);
        Token token = tryGetFirst(tokens);
        while (TokenType.match(token, TokenType.OR)) {
            tokens.pop();
            node = createOperatorNode(
                    token.getFunction(),
                    Arrays.asList(node, processOperatorAnd(tokens))
            );
            token = tryGetFirst(tokens);
        }
        return node;
    }

    private IEvaluationNode processOperatorAnd(final LinkedList<Token> tokens) {
        IEvaluationNode node = processOperatorEquality(tokens);
        Token token = tryGetFirst(tokens);
        while (TokenType.match(token, TokenType.AND)) {
            tokens.pop();
            node = createOperatorNode(
                    token.getFunction(),
                    Arrays.asList(node, processOperatorEquality(tokens))
            );
            token = tryGetFirst(tokens);
        }
        return node;
    }

    private IEvaluationNode processOperatorEquality(final LinkedList<Token> tokens) {
        IEvaluationNode node = processOperatorCondition(tokens);
        Token token = tryGetFirst(tokens);
        while (TokenType.match(token, TokenType.EQUALITY)) {
            tokens.pop();
            node = createOperatorNode(
                    token.getFunction(),
                    Arrays.asList(node, processOperatorCondition(tokens))
            );
            token = tryGetFirst(tokens);
        }
        return node;
    }

    private IEvaluationNode processOperatorCondition(final LinkedList<Token> tokens) {
        IEvaluationNode node = processOperatorAddSub(tokens);
        Token token = tryGetFirst(tokens);
        while (TokenType.match(token, TokenType.CONDITION)) {
            tokens.pop();
            node = createOperatorNode(
                    token.getFunction(),
                    Arrays.asList(node, processOperatorAddSub(tokens))
            );
            token = tryGetFirst(tokens);
        }
        return node;
    }

    private IEvaluationNode processOperatorAddSub(final LinkedList<Token> tokens) {
        IEvaluationNode node = processOperatorMulDiv(tokens);
        Token token = tryGetFirst(tokens);
        while (TokenType.match(token, TokenType.ADD_SUB)) {
            tokens.pop();
            node = createOperatorNode(
                    token.getFunction(),
                    Arrays.asList(node, processOperatorMulDiv(tokens))
            );
            token = tryGetFirst(tokens);
        }
        return node;
    }

    private IEvaluationNode processOperatorMulDiv(final LinkedList<Token> tokens) {
        IEvaluationNode node = processUnaryOperator(tokens);
        Token token = tryGetFirst(tokens);
        while (TokenType.match(token, TokenType.MUL_DIV)) {
            tokens.pop();
            node = createOperatorNode(
                    token.getFunction(),
                    Arrays.asList(node, processUnaryOperator(tokens))
            );
            token = tryGetFirst(tokens);
        }
        return node;
    }

    private IEvaluationNode processUnaryOperator(final LinkedList<Token> tokens) {
        Token token = tryGetFirst(tokens);
        Map<String, IFunction> signFunc = new HashMap<String, IFunction>() {{
            put("+", args -> args[0]);
            put("-", args -> -((Number) args[0]).doubleValue());
        }};
        if (TokenType.match(token, TokenType.ADD_SUB)) {
            tokens.pop();
            return createOperatorNode(
                    signFunc.get(token.getLexeme()),
                    Collections.singletonList(processUnaryOperator(tokens))
            );
        }
        if (TokenType.match(token, TokenType.INC_DEC) || TokenType.match(token, TokenType.NEG)) {
            tokens.pop();
            return createOperatorNode(
                    token.getFunction(),
                    Collections.singletonList(processUnaryOperator(tokens))
            );
        }
        return processIdentifier(tokens);
    }

    private IEvaluationNode processIdentifier(final LinkedList<Token> tokens) {
        Token token = tryGetFirst(tokens);
        if (TokenType.match(token, TokenType.IDENTIFIER)) {
            String lexeme = token.getLexeme();
            tokens.pop();
            token = tryGetFirst(tokens);
            if (!TokenType.match(token, TokenType.LPAR)) {
                return createIdentifierNode(lexeme);
            }
            List<IEvaluationNode> params = new ArrayList<>();
            tokens.pop();
            token = tryGetFirst(tokens);
            if (!TokenType.match(token, TokenType.RPAR)) {
                params.add(processOperatorOr(tokens));
                token = tryGetFirst(tokens);
                while (TokenType.match(token, TokenType.DELIMITER)) {
                    tokens.pop();
                    params.add(processOperatorOr(tokens));
                    token = tryGetFirst(tokens);
                }
            }
            token = tryGetFirst(tokens);
            if (!TokenType.match(token, TokenType.RPAR)) {
                throw new SyntaxException("Parenthesis ) expected!");
            }
            tokens.pop();
            return createOperatorNode(lexeme, params);
        }
        return processConstant(tokens);
    }

    private IEvaluationNode processConstant(final LinkedList<Token> tokens) {
        Token token = tryGetFirst(tokens);
        if (TokenType.match(token, TokenType.CONSTANT)) {
            tokens.pop();
            return createConstantNode(token.getFunction());
        }
        return processParentheses(tokens);
    }

    private IEvaluationNode processParentheses(final LinkedList<Token> tokens) {
        Token token = tryGetFirst(tokens);
        if (TokenType.match(token, TokenType.RPAR)) {
            throw new SyntaxException("Unexpected end of expression!");
        }
        if (TokenType.match(token, TokenType.LPAR)) {
            tokens.pop();
            IEvaluationNode node = processOperatorOr(tokens);
            token = tryGetFirst(tokens);
            if (!TokenType.match(token, TokenType.RPAR)) {
                throw new SyntaxException("Parenthesis ) expected!");
            }
            tokens.pop();
            return node;
        }
        throw new SyntaxException("Unexpected end of expression!");
    }

    private IEvaluationNode createConstantNode(IFunction function) {
        return (scope) -> {
            try {
                return function.apply();
            } catch (ExecutionException ex) {
                throw new EvaluatingExpressionException(ex.getMessage(), ex);
            }
        };
    }

    private IEvaluationNode createIdentifierNode(String lexeme) {
        IProperty property = properties.get(lexeme);
        return (scope) -> {
            if (property != null) {
                try {
                    return property.apply(scope);
                } catch (ExecutionException ex) {
                    throw new EvaluatingExpressionException(ex.getMessage(), ex);
                }
            }
            return seekProperty(scope, lexeme);
        };
    }

    private IEvaluationNode createOperatorNode(IFunction operator, final List<IEvaluationNode> nodes) {
        return scope -> {
            try {
                int i = 0;
                Object[] args = new Object[nodes.size()];
                for (IEvaluationNode node : nodes) {
                    args[i++] = node.eval(scope);
                }
                return operator.apply(args);
            } catch (ExecutionException ex) {
                throw new EvaluatingExpressionException(ex.getMessage(), ex);
            }
        };
    }

    private IEvaluationNode createOperatorNode(String functionKey, final List<IEvaluationNode> nodes) {
        return scope -> {
            try {
                IFunction operator = Optional
                        .ofNullable(functions.get(functionKey))
                        .orElseThrow(() ->
                                new EvaluatingExpressionException("Unregistered function:[" + functionKey + "]!"));
                int i = 0;
                Object[] args = new Object[nodes.size()];
                for (IEvaluationNode node : nodes) {
                    args[i++] = node.eval(scope);
                }
                return operator.apply(args);
            } catch (ExecutionException ex) {
                throw new EvaluatingExpressionException(ex.getMessage(), ex);
            }
        };
    }

    private Token tryGetFirst(final LinkedList<Token> tokens) {
        return tokens.isEmpty() ? null : tokens.getFirst();
    }

    private Object seekProperty(final Map<String, Object> scope, String path) throws EvaluatingExpressionException {
        try {
            String[] parts = path.split("\\.");
            Map<String, Object> currentScope = scope;
            for (String part : parts) {
                Object value = currentScope.get(part);
                if (value != null) {
                    if (value instanceof Map) {
                        currentScope = (Map) value;
                        continue;
                    }
                    return value;
                }
            }
            throw new EvaluatingExpressionException("Unknown identifier: [" + path + "]! " +
                    "It's may be unregistered property or value that is missing in the scope.");
        } catch (ClassCastException ex) {
            throw new EvaluatingExpressionException("Scope and nested scopes must be a Map<String, Object> type!");
        } catch (NullPointerException ex) {
            throw new EvaluatingExpressionException("Scope is undefined!");
        }
    }

}
