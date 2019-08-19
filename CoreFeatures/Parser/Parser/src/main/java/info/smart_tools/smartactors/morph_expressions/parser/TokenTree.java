package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IFunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Token tree a container with reserved tokens which using in a lexer and parser.
 * <br>Used  for fast search the reserved token by lexeme.
 * <br>Handlers for a some lexemes can be overloaded.
 *
 * <p>The reserved lexemes: <code> true </code> <code> false </code> <code> ! </code> <code> ++ </code>
 * <code> -- </code> <code> || </code> <code> && </code> <code> > </code> <code> >= </code> <code> < </code>
 * <code> <= </code> <code> == </code> <code> != </code> <code> + </code> <code> - </code> <code> * </code>
 * <code> / </code> <code> % </code> <code> ( </code>, <code> ) </code> <code> , </code> <code> ; </code>
 * </p>
 *
 * <p>The overloaded lexemes: <code> ! </code> <code> ++ </code> <code> -- </code> <code> || </code>
 * <code> && </code> <code> > </code> <code> >= </code> <code> < </code> <code> <= </code> <code> == </code>
 * <code> != </code> <code> + </code> <code> - </code> <code> * </code> <code> / </code> <code> % </code>
 * </p>
 *
 * @see TokenTree#getRoot()
 *
 * @see Token
 * @see Lexer
 * @see RecursiveDescentParser
 */
class TokenTree {

    private List<String> finalLexemes = Arrays.asList("true", "false", "(", ")", ",", ";");

    private final Token[] tokens = new Token[] {
            Token.of("true", TokenType.CONSTANT, args -> Boolean.TRUE),
            Token.of("false", TokenType.CONSTANT, args -> Boolean.FALSE),
            Token.of("!", TokenType.NEG),
            Token.of("++", TokenType.INC_DEC),
            Token.of("--", TokenType.INC_DEC),
            Token.of("||", TokenType.OR),
            Token.of("&&", TokenType.AND),
            Token.of(">", TokenType.CONDITION),
            Token.of(">=", TokenType.CONDITION),
            Token.of("<", TokenType.CONDITION),
            Token.of("<=", TokenType.CONDITION),
            Token.of("==", TokenType.EQUALITY),
            Token.of("!=", TokenType.EQUALITY),
            Token.of("+", TokenType.ADD_SUB),
            Token.of("-", TokenType.ADD_SUB),
            Token.of("*", TokenType.MUL_DIV),
            Token.of("/", TokenType.MUL_DIV),
            Token.of("%", TokenType.MUL_DIV),
            Token.of("(", TokenType.LPAR),
            Token.of(")", TokenType.RPAR),
            Token.of(",", TokenType.DELIMITER),
            Token.of(";", TokenType.DELIMITER)
    };

    static class Node {

        private Map<Character, Node> children;
        private Token token;

        Node() {
            this.children = new HashMap<>(1);
            this.token = null;
        }

        Node getChild(final char key) {
            return children.get(key);
        }

        Node addChild(final char key) {
            Node node = new Node();
            children.put(key, node);
            return node;
        }

        Token getToken() {
            return token;
        }

        void setToken(final Token lexeme) {
            this.token = lexeme;
        }

    }

    private final Map<String, IFunction> operators;
    private final Node root;

    /**
     * Constructs a TokenTree.
     */
    TokenTree() {
        this.root = new Node();
        this.operators = new Operators().getAll();
        for (Token token : tokens) {
            addNode(token);
        }
    }

    /**
     * Constructs a TokenTree with overloaded operators.
     * If the tree previously contained a operator for
     * the key from the package, the old value is replaced by the specified value.
     * If operator from given package cannot be overloaded then he ignored.
     *
     * @param operators the operators to be overloaded.
     */
    TokenTree(final Map<String, IFunction> operators) {
        this.root = new Node();
        this.operators = new Operators().getAll();
        for (Map.Entry<String, IFunction> entry : operators.entrySet()) {
            if (!finalLexemes.contains(entry.getKey())) {
                this.operators.put(entry.getKey(), entry.getValue());
            }
        }
        for (Token token : tokens) {
            addNode(token);
        }
    }

    /**
     * Gets a root node of a token tree.
     *
     * @return the root node of the token tree.
     */
    Node getRoot() {
        return root;
    }

    private void addNode(final Token token) {
        Node nextNode, currentNode = root;
        String lexeme = token.getLexeme();
        int length = lexeme.length();
        char key;
        for (int i = 0; i < length; i++) {
            key = lexeme.charAt(i);
            nextNode = currentNode.getChild(key);
            currentNode = nextNode != null ? nextNode : currentNode.addChild(key);
        }
        if (token.getFunction() == null) {
            token.setFunction(operators.get(lexeme));
        }
        currentNode.setToken(token);
    }

}
