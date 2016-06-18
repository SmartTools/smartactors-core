package info.smart_tools.smartactors.core.db_storage.utils;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

import java.util.regex.Pattern;

/**
 * Wrapper for collection name string
 */
public class CollectionName {

    private static Pattern VALIDATION_PATTERN = Pattern.compile("[a-zA-Z_][0-9a-zA-Z_]*");

    private String name;

    private CollectionName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    /**
     * Create collection name from input string
     * @param name string with name
     * @return created CollectionName
     * @throws QueryBuildException if input string doesn't match validation pattern
     */
    public static CollectionName fromString(String name) throws QueryBuildException {

        if (!VALIDATION_PATTERN.matcher(name).matches()) {
            throw new QueryBuildException("Invalid collection name: "+name);
        }

        return new CollectionName(name.toLowerCase());
    }
}
