package info.smart_tools.smartactors.database_in_memory.in_memory_database;

import java.util.HashSet;
import java.util.Set;

/**
 * Simplest implementation to emulate full text matching.
 * Just takes up-to 3 letter tokens from the search string
 * and tries to find out any of occurrences of the tokens in the target string.
 */
class SimpleFullTextMatcher {

    private static final int TOKEN_LENGTH = 3;

    private Set<String> tokens = new HashSet<>();

    /**
     * Creates the matcher
     * @param search search string to be split to tokens.
     */
    SimpleFullTextMatcher(final String search) {
        for (String word : search.split("\\W+")) {
            String token = word.substring(0, Math.min(TOKEN_LENGTH, word.length()));
            tokens.add(token.toLowerCase());
        }
    }

    /**
     * Runs comparison over the search string.
     * @param search the string where to try to found tokens
     * @return true if any token was found
     */
    boolean matches(final String search) {
        if (search == null) {
            return false;
        }
        String lowerCase = search.toLowerCase();
        for (String token : tokens) {
            if (lowerCase.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
