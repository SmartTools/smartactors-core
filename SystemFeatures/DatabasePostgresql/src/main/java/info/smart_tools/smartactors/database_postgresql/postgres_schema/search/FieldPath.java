package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import java.util.regex.Pattern;

/**
 * Describes a path of the document's field.
 * Path starts with name of root document's field and may be continued by a names of nesting document fields or/and arrays indexes.
 * The path must be in dotted notation, for example 'a.b', 'parent.child', etc...
 * Also the array index notation is acceptable, for example 'a[1]', 'tags[0]', etc...
 */
public interface FieldPath {

    /**
     * Valid path matches this pattern.
     */
    Pattern VALIDATION_PATTERN =
            Pattern.compile("^([a-zA-Z_а-яА-Я\\-][a-zA-Z0-9_а-яА-Я\\-]*)((\\.([a-zA-Z_а-яА-Я\\-][a-zA-Z0-9_а-яА-Я\\-]*))|(\\[[0-9]+\\]))*$");

    /**
     * The path is split to the parts using this pattern.
     */
    Pattern SPLIT_PATTERN = Pattern.compile("[\\[\\]\\.]+");

    /**
     * Checks if the string contains valid field path.
     * @param path string to check.
     * @return {@code true} if and only if {@code path} is a valid field path.
     */
    static boolean isValid(String path) {
        return VALIDATION_PATTERN.matcher(path).matches();
    }

    /**
     * Breaks field path string into parts representing single path steps - field names or array indexes.
     * @param path original field path.
     * @return array of strings representing path steps.
     */
    static String[] splitParts(String path) {
        return SPLIT_PATTERN.split(path);
    }

    /**
     * Get representation of field path used in SQL query
     * The result is depended on database and schema type
     * @return String representation of field path usable in SQL query
     */
    String toSQL();

    /**
     * Get id of field path used in SQL query
     * The result is depended on database and schema type
     * @return String representation of field path id usable in SQL query
     */
    String getId();
}
