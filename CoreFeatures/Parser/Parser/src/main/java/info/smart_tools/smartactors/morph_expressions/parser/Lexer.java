package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IFunction;
import info.smart_tools.smartactors.morph_expressions.parser.exception.SyntaxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Lexer(often called a scanner or tokenizer) breaks up an input stream
 * of characters into vocabulary symbols(creates tokens) for a parser,
 * which applies a grammatical structure to that symbol stream.
 *
 * @see Lexer#getTokens(String)
 *
 * @see Token
 * @see TokenType
 * @see TokenTree
 * @see RecursiveDescentParser
 */
class Lexer {

    private TokenTree reservedTokens;
    private List<ReaderContainer> tokenReaders;

    private static final Pattern DIGIT = Pattern.compile("\\d");
    private static final Pattern ALPHABETIC = Pattern.compile("[A-zА-яёЁ]");
    private static final String WHITESPACE = " \f\n\r\t\u00A0\u2028\u2029";

    @FunctionalInterface
    private interface ITokenReader {
        /* Detects and reads a specified tokens from an expression. */
        int read(String expression, int index, final List<Token> tokens);
    }

    private class ReaderContainer {

        private Predicate<Character> predicate;
        private ITokenReader tokenReader;

        ReaderContainer(Predicate<Character> predicate, ITokenReader tokenReader) {
            this.predicate = predicate;
            this.tokenReader = tokenReader;
        }

        Predicate<Character> getPredicate() {
            return predicate;
        }

        ITokenReader getTokenReader() {
            return tokenReader;
        }

    }

    /* Predicates of tokens. */
    private Predicate<Character> isWhitespace = ch -> WHITESPACE.indexOf(ch) != -1;
    private Predicate<Character> isQuotes = ch -> ch == '"' || ch == '\'';
    private Predicate<Character> isDigit = ch -> DIGIT.matcher(ch.toString()).matches();
    private Predicate<Character> isNumberSymbols = ch -> isDigit.test(ch) || ch == '.';
    private Predicate<Character> isAlphabetic = ch -> ALPHABETIC.matcher(ch.toString()).matches();
    private Predicate<Character> isIdentifierStart = ch -> isAlphabetic.test(ch) || ch == '_' || ch == '$';
    private Predicate<Character> isIdentifierContinue = ch -> isIdentifierStart.test(ch) ||
            isDigit.test(ch) || ch == '.' || ch == '-';

    private ITokenReader whitespaceReader = (expression, index, tokens) -> {
        final int length = expression.length();
        while (index < length) {
            if (!isWhitespace.test(expression.charAt(index))) {
                break;
            }
            ++index;
        }
        return index;
    };

    private ITokenReader stringReader = (expression, index, tokens) -> {
        final char ch = expression.charAt(index);
        final int startIndex = ++index;
        final int length = expression.length();
        while (index < length) {
            if (ch == expression.charAt(index)) {
                final String lexeme = expression.substring(startIndex, index);
                tokens.add(Token.of(lexeme, TokenType.CONSTANT, args -> lexeme));
                return ++index;
            }
            ++index;
        }
        throw new SyntaxException("Unterminated quote " + ch +  " at " + startIndex + " index!");
    };

    private ITokenReader digitReader = (expression, index, tokens) -> {
        final int startIndex = index++;
        final int length = expression.length();
        while (index < length) {
            if (!isNumberSymbols.test(expression.charAt(index))) {
                break;
            }
            ++index;
        }
        final String lexeme = expression.substring(startIndex, index);
        try {
            final double number = Double.valueOf(lexeme);
            if (Double.isNaN(number)) {
                throw new SyntaxException("Invalid number at " + startIndex + " index!");
            }
            tokens.add(Token.of(lexeme, TokenType.CONSTANT, args -> number));
        } catch (NumberFormatException ex) {
            throw new SyntaxException("Invalid number at " + startIndex + " index!", ex);
        }
        return index;
    };

    private ITokenReader identifierReader = (expression, index, tokens) -> {
        final int startIndex = index++;
        final int length = expression.length();
        while (index < length) {
            if (!isIdentifierContinue.test(expression.charAt(index))) {
                break;
            }
            ++index;
        }
        final String lexeme = expression.substring(startIndex, index);
        tokens.add(Token.of(lexeme, TokenType.IDENTIFIER));
        return index;
    };

    /**
     * Constructs a Lexer with default implementations of operators.
     *
     * @see Operators
     * @see TokenTree
     */
    Lexer() {
        this.reservedTokens = new TokenTree();
        initReaders();
    }

    /**
     * Constructs a Lexer with overloaded operators.
     *
     * @param operators the overloaded operators.
     *
     * @see TokenTree
     */
    Lexer(final Map<String, IFunction> operators) {
        this.reservedTokens = new TokenTree(operators);
        initReaders();
    }

    private void initReaders() {
        this.tokenReaders = new ArrayList<ReaderContainer>() {{
            add(new ReaderContainer(isWhitespace, whitespaceReader));
            add(new ReaderContainer(isDigit, digitReader));
            add(new ReaderContainer(isQuotes, stringReader));
            add(new ReaderContainer(isIdentifierStart, identifierReader));
        }};
    }

    /**
     * Breaks up the given expression on lexemes and creates specified tokens.
     * Determines syntax errors in the given expression.
     *
     * @param expression the expression to be analyzed.
     * @return a list of tokens corresponding to the given expression.
     * @throws SyntaxException when the given expression has an illegal grammar or a syntax errors.
     */
    List<Token> getTokens(String expression) {
        LinkedList<Token> tokens = new LinkedList<>();
        int index = 0;
        int length = expression.length();
        while (index < length) {
            index = readToken(expression, index, tokens);
        }
        return tokens;
    }

    private int readToken(String expression, int index, final List<Token> tokens) {
        int skip = readReservedToken(expression, index, tokens);
        if (skip != -1) {
            return skip;
        }
        final char ch = expression.charAt(index);
        for (ReaderContainer readerContainer : tokenReaders) {
            if (readerContainer.getPredicate().test(ch)) {
                return readerContainer.getTokenReader().read(expression, index, tokens);
            }
        }
        throw new SyntaxException("Invalid expression '" + expression +
                "'. Unrecognized lexeme at " + index + " index!");
    }

    private int readReservedToken(String expression, int index, final List<Token> tokens) {
        TokenTree.Node found = null;
        TokenTree.Node node = reservedTokens.getRoot();
        final int length = expression.length();
        while (index < length) {
            node = node.getChild(expression.charAt(index));
            if (node == null) break;
            found = node.getToken() != null ? node : found;
            ++index;
        }
        if (found != null) {
            tokens.add(found.getToken());
            return index;
        }
        return -1;
    }

}
