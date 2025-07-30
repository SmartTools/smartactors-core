package info.smart_tools.smartactors.morph_expressions.parser;

import info.smart_tools.smartactors.morph_expressions.interfaces.parser.IFunction;
import info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception.ExecutionException;
import info.smart_tools.smartactors.morph_expressions.parser.exception.TypeConversionException;

import java.util.HashMap;
import java.util.Map;

import static info.smart_tools.smartactors.morph_expressions.parser.TypeConverter.convertToBool;
import static info.smart_tools.smartactors.morph_expressions.parser.TypeConverter.convertToDouble;

/**
 * A default implementation of an reserved overloaded operators.
 */
public class Operators {

    private final Map<String, IFunction> operators = new HashMap<String, IFunction>() {{
        put("!", args -> !(convertToBool(args[0])));
        put("++", args -> {
            try {
                return convertToDouble(args[0]) + 1.0;
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("--", args -> {
            try {
                return convertToDouble(args[0]) - 1.0;
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("||", args -> convertToBool(args[0]) ? args[0] : args[1]);
        put("&&", args -> !convertToBool(args[0]) ? args[0] : args[1]);
        put(">", args -> {
            if (args[0] instanceof String && args[1] instanceof String) {
                return ((String) args[0]).compareTo((String) args[1]) > 0;
            }
            try {
                return convertToDouble(args[0]) > convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put(">=", args -> {
            if (args[0] instanceof String && args[1] instanceof String) {
                return ((String) args[0]).compareTo((String) args[1]) > 0;
            }
            try {
                return convertToDouble(args[0]) >= convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("<", args -> {
            if (args[0] instanceof String && args[1] instanceof String) {
                return ((String) args[0]).compareTo((String) args[1]) > 0;
            }
            try {
                return convertToDouble(args[0]) < convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("<=", args -> {
            if (args[0] instanceof String && args[1] instanceof String) {
                return ((String) args[0]).compareTo((String) args[1]) > 0;
            }
            try {
                return convertToDouble(args[0]) <= convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("==", args -> {
            if (args[0] instanceof Number || args[1] instanceof Number) {
                try {
                    return convertToDouble(args[0]).equals(convertToDouble(args[1]));
                } catch (TypeConversionException ex) {
                    throw new ExecutionException(ex.getMessage(), ex);
                }
            }
            return args[0].equals(args[1]);
        });
        put("!=", args -> {
            if (args[0] instanceof Number || args[1] instanceof Number) {
                try {
                    return !convertToDouble(args[0]).equals(convertToDouble(args[1]));
                } catch (TypeConversionException ex) {
                    throw new ExecutionException(ex.getMessage(), ex);
                }
            }
            return !args[0].equals(args[1]);
        });
        put("+", args -> {
            if (args[0] instanceof String || args[1] instanceof String) {
                return String.valueOf(args[0]) + String.valueOf(args[1]);
            }
            try {
                return convertToDouble(args[0]) + convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("-", args -> {
            try {
                return convertToDouble(args[0]) - convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("*", args -> {
            try {
                return convertToDouble(args[0]) * convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("/", args -> {
            try {
                return convertToDouble(args[0]) / convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
        put("%", args -> {
            try {
                return convertToDouble(args[0]) % convertToDouble(args[1]);
            } catch (TypeConversionException ex) {
                throw new ExecutionException(ex.getMessage(), ex);
            }
        });
    }};

    /**
     * @return the package with all default implementation of operators.
     */
    public Map<String, IFunction> getAll() {
        return operators;
    }

}
