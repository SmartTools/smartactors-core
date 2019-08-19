package info.smart_tools.smartactors.morph_expressions.parser;

/**
 * A types of token which using in a lexer and a parser.
 * <br>The token types are used to enforce order of operations
 * when building the syntax tree in the parser.
 *
 * @see Lexer
 * @see RecursiveDescentParser
 *
 * @see TokenType#match(Token, TokenType)
 */
enum TokenType {

    OR, // logical operator '&&'
    AND, // logical operator '||'
    LPAR, // left(open) parentheses '('
    RPAR, // right(close) parentheses ')'
    NEG, // unary operator '!'
    ADD_SUB, // arithmetical operators '+' and '-'
    INC_DEC, // unary operators '++' and '--'
    MUL_DIV, // arithmetical operators '*', '/' and '%'
    CONSTANT, // constant
    IDENTIFIER, // identifier
    EQUALITY, // logical operator '=='
    CONDITION, // logical conditions like a '>', '<', '>=', '<='
    DELIMITER; // delimiters like a ',' or ';'

    /**
     * Checks a matching of a token for a type.
     *
     * @param token  the token to be checked for a type.
     * @param tokenType  the type to check.
     * @return if {@param token} has {@param tokenType} then true, else false.
     */
    static boolean match(final Token token, final TokenType tokenType) {
        return token != null && token.getType().equals(tokenType);
    }

}
