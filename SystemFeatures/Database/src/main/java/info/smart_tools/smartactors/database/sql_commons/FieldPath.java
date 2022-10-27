package info.smart_tools.smartactors.database.sql_commons;

import java.util.regex.Pattern;

/**
 *  Describes a path of document's field.
 *
 *  Path starts with name of root document's field and may be continued by a names of nesting document fields or/and arrays indexes.
 */
public interface FieldPath {
    Pattern validationPattern = Pattern.compile("^([a-zA-Z_а-яА-Я\\-][a-zA-Z0-9_а-яА-Я\\-]*)((\\.([a-zA-Z_а-яА-Я\\-][a-zA-Z0-9_а-яА-Я\\-]*))|(\\[[0-9]+\\]))*$");
    Pattern splitPattern = Pattern.compile("[\\[\\]\\.]+");

    /**
     *  Checks if string contains valid field path.
     *
     *  @param path string to check.
     *  @return {@code true} if and only if {@code path} is a valid field path.
     */
    static boolean isValid(String path) {
        return validationPattern.matcher(path).matches();
    }

    /**
     *  Breaks field path string into parts representing single path steps - field names or array indexes.
     *
     *  @param path original field path.
     *  @return array of strings representing path steps.
     */
    static String[] splitParts(String path) {
        return splitPattern.split(path);
    }

    /**
     *  Get representation of field path used in SQL query.
     *
     *  The result is depended on database and schema type.
     *
     *  @return String representation of field path usable in SQL query.
     */
    String getSQLRepresentation();
}
