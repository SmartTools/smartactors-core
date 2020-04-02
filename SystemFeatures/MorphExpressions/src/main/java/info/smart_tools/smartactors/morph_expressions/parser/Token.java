package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IFunction;

/**
 * Token is a structure representing a lexeme that explicitly
 * indicates its categorization for the purpose of parsing.
 */
class Token {
    
    private String lexeme;
    private TokenType type;
    private IFunction function;

    private Token(String lexeme, TokenType type) {
        this.lexeme = lexeme;
        this.type = type;
        this.function = null;
    }

    private Token(String lexeme, TokenType type, IFunction function) {
        this.lexeme = lexeme;
        this.type = type;
        this.function = function;
    }

    /**
     * Constructs a Token with a specified type and a lexeme.
     *
     * @param lexeme a unit of lexical meaning.
     * @param type   the type of token.
     *
     * @see TokenType
     */
    static Token of(String lexeme, TokenType type) {
        return new Token(lexeme, type);
    }

    /**
     * Constructs a Token with a specified type, lexeme and handler.
     *
     * @param lexeme   a unit of lexical meaning.
     * @param type     the type of token.
     * @param function the handler of lexeme.
     */
    static Token of(String lexeme, TokenType type, IFunction function) {
        return new Token(lexeme, type, function);
    }

    String getLexeme() {
        return lexeme;
    }

    TokenType getType() {
        return type;
    }

    IFunction getFunction() {
        return function;
    }

    void setFunction(IFunction function) {
        this.function = function;
    }

}
