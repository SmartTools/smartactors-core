package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.parser.exception.TypeConversionException;

import java.util.List;

/**
 * Util for a safe types conversion.
 *
 * <p>NOTE: util on based on the types conversion rules of the <code>JavaScript</code>.</p>
 * <code>Java</code> <code>null</code> associated with a <code>undefined</code> in the <code>JavaScript</code>.
 */
public class TypeConverter {
    /**
     * Converts a given value to a boolean type.
     *
     * @param value value to be converted to the <code>boolean</code>.
     * @return the <code>boolean</code> representation of the given value.
     */
    public static Boolean convertToBool(final Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            double val = ((Number) value).doubleValue();
            return val != 0 && !((Number) val).equals(Double.NaN);
        }
        if (value instanceof String) {
            return !((String) value).isEmpty();
        }
        if (value instanceof List) {
            return Boolean.TRUE;
        }
        return value != null;
    }

    /**
     * Converts a given value to a <code>double</code>.
     *
     * @param value value to be converted to the <code>double</code>.
     * @return the <code>double</code> representation of the given value.
     * @throws TypeConversionException when the given value can't be converted to the <code>double</code>.
     */
    public static Double convertToDouble(final Object value) throws TypeConversionException {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            if (((String) value).isEmpty()) {
                return 0.0;
            }
            try {
                return Double.valueOf((String) value);
            } catch (NumberFormatException ex) {
                return Double.NaN;
            }
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1.0 : 0.0;
        }
        if (value == null) {
            return Double.NaN;
        }
        // Java Object hasn't default method like a JavaScript
        // so the arbitrary object can't to be cast to Double type.
        throw new TypeConversionException("Failed converted to number: unrecognized type of the given value!");
    }

}
