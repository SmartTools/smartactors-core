package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.PostgresSchema;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * A set of methods which writes query parts for comparison operators and their parameters.
 */
final class Operators {

    /**
     * Private constructor to avoid instantiation.
     */
    private Operators() {
    }

    /**
     * Registers all operators to the resolver.
     * @param resolver resolver to write queries with all these operators
     */
    public static void addAll(final PostgresQueryWriterResolver resolver) {

        // Basic field comparison operators
        resolver.addQueryWriter("$eq", formattedCheckWriterForComparisons(
                "((%s)=to_json(?)::jsonb)",
                "((%s)=(?::%s))"));
        resolver.addQueryWriter("$ne", formattedCheckWriterForComparisons(
                "((%s)!=to_json(?)::jsonb)",
                "((%s)!=(?::%s))"));
        resolver.addQueryWriter("$lt", formattedCheckWriterForComparisons(
                "((%s)<to_json(?)::jsonb)",
                "((%s)<(?::%s))"));
        resolver.addQueryWriter("$gt", formattedCheckWriterForComparisons(
                "((%s)>to_json(?)::jsonb)",
                "((%s)>(?::%s))"));
        resolver.addQueryWriter("$lte", formattedCheckWriterForComparisons(
                "((%s)<=to_json(?)::jsonb)",
                "((%s)<=(?::%s))"));
        resolver.addQueryWriter("$gte", formattedCheckWriterForComparisons(
                "((%s)>=to_json(?)::jsonb)",
                "((%s)>=(?::%s))"));

        //Check on present
        resolver.addQueryWriter("$isNull", Operators::writeFieldExistsCheckCondition);

        // ISO 8601 date/time operators
        /*TODO: Find a way to build an index on date/time field.*/
        resolver.addQueryWriter("$date-from", formattedCheckWriter("(parse_timestamp_immutable(%s)>=(?)::timestamptz)"));
        resolver.addQueryWriter("$date-to", formattedCheckWriter("(parse_timestamp_immutable(%s)<=(?)::timestamptz)"));

        // Value in list check
        resolver.addQueryWriter("$in", Operators::writeFieldInArrayCheckCondition);

        // Tags operators
        resolver.addQueryWriter("$hasTag", formattedCheckWriter("((%s)??(?))"));

        // Fulltext search
        resolver.addQueryWriter("$fulltext", formattedCheckWriterForFulltext("%s@@(to_tsquery(?,?))"));
    }

    /**
     * Creates the condition writer based on the format string.
     * @param format format string, contains '%s' for field path and '?' for parameters
     * @return the condition writer ready to be added to basic resolver
     */
    private static QueryWriter formattedCheckWriter(final String format) {
        return (query, resolver, contextFieldPath, queryParameter) ->
                writeFieldCheckCondition(format, query, contextFieldPath, queryParameter);
    }

    /**
     * Creates the condition writer based on the format string.
     * @param format format string, contains '%s' for column name and '?' for parameters
     * @return the condition writer ready to be added to basic resolver
     */
    private static QueryWriter formattedCheckWriterForFulltext(final String format) {
        return (query, resolver, contextFieldPath, queryParameter) -> {
            Writer writer = query.getBodyWriter();

            try {
                if (queryParameter instanceof String) {
                    writer.write(String.format(format,
                            PostgresSchema.FULLTEXT_COLUMN + "_" + PostgresSchema.DEFAULT_FTS_DICTIONARY));
                    query.pushParameterSetter((statement, index) -> {
                        statement.setString(index++, PostgresSchema.DEFAULT_FTS_DICTIONARY);
                        statement.setString(index++, (String) queryParameter);
                        return index;
                    });
                } else if (queryParameter instanceof IObject) {
                    IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

                    IFieldName languageFieldName = IOC.resolve(fieldNameKey, "language");
                    String fulltextLanguage = (String)((IObject) queryParameter).getValue(languageFieldName);
                    final String language = fulltextLanguage != null ? fulltextLanguage : PostgresSchema.DEFAULT_FTS_DICTIONARY;

                    IFieldName queryFieldName = IOC.resolve(fieldNameKey, "query");
                    Object queryField = ((IObject) queryParameter).getValue(queryFieldName);
                    if (queryField == null) {
                        throw new QueryBuildException("Error while writing a query string: can't find 'query' parameter in 'fulltext' filter");
                    }
                    if (queryField instanceof String) {
                        writer.write(String.format(format,
                                PostgresSchema.FULLTEXT_COLUMN + "_" + language));
                        query.pushParameterSetter((statement, index) -> {
                            statement.setString(index++, language);
                            statement.setString(index++, (String) queryField);
                            return index;
                        });
                    } else if (queryField instanceof IObject) {
                        throw new QueryBuildException("Composite condition is not supported for fulltext");
                        /*
                        String fmt = String.format(format,
                                PostgresSchema.FULLTEXT_COLUMN + "_" + language, language, "?");
                        //writeFieldCheckCondition(fmt, query, contextFieldPath, queryField);
                        resolver.resolve(null).write(query, resolver, null, queryField);
                         */
                    } else {
                        throw new QueryBuildException("Unknown type of 'query' option for fulltext");
                    }
                } else {
                    throw new QueryBuildException("Composite node value should be an node or a string");
                }
            } catch (ReadValueException | InvalidArgumentException | ResolutionException | IOException e) {
                throw new QueryBuildException("Error while writing a query string", e);
            }
        };
    }

    /**
     * Writes part of sql query with basic field comparison operators
     * @param format sql string for condition. Contains '%s' for field path and '?' for parameters
     * @param query query statement object where to write the body and add parameter setters
     * @param contextFieldPath current field path, for example document#>'{field}'
     * @param queryParameter current value of the query parameter
     * @throws QueryBuildException if something goes wrong
     */
    private static void writeFieldCheckCondition(
            final String format,
            final QueryStatement query,
            final String contextFieldPath,
            final Object queryParameter
    ) throws QueryBuildException {

        if (contextFieldPath == null) {
            throw new QueryBuildException("Field check conditions not allowed outside of field context");
        }

        try {
            query.getBodyWriter().write(String.format(format, PostgresFieldPath.fromString(contextFieldPath).toSQL()));

            query.pushParameterSetter((statement, index) -> {
                statement.setObject(index++, queryParameter);
                return index;
            });
        } catch (IOException e) {
            throw new QueryBuildException("Query search conditions write failed because of exception", e);
        }
    }

    /**
     * Writes part of sql query with basic field comparison operators
     * @param format sql string for condition. Contains '%s' for field path and type to cast, '?' for parameters
     * @param query query statement object where to write the body and add parameter setters
     * @param contextFieldPath current field path, for example document#>>'{field}'
     * @param queryParameterValue value field of current query parameter
     * @param queryParameterType type field of current query parameter, [currentParameterValue] will be casted to this type
     * @throws QueryBuildException if something goes wrong
     */
    private static void writeFieldCheckConditionWithTypeCast(
            final String format,
            final QueryStatement query,
            final String contextFieldPath,
            final Object queryParameterValue,
            final String queryParameterType
    ) throws QueryBuildException {

        if (contextFieldPath == null) {
            throw new QueryBuildException("Field check conditions not allowed outside of field context");
        }

        try {
            query.getBodyWriter().write(String.format(
                    format,
                    PostgresFieldPath.fromStringAndType(contextFieldPath, queryParameterType).toSQL(), queryParameterType));

            query.pushParameterSetter((statement, index) -> {
                statement.setObject(index++, queryParameterValue);
                return index;
            });
        } catch (IOException e) {
            throw new QueryBuildException("Query search conditions write failed because of exception", e);
        }
    }

    /**
     * Writes part of sql query which checks existence of field in the document
     * @param query query statement object where to write body and add parameter setters
     * @param resolver resolver for nested operators, is ignored here
     * @param contextFieldPath current field path, for example document#>'{field}'
     * @param queryParameter current parameter value. Must be boolean: if 'true' check field is null, if 'false' check the field is not null
     * @throws QueryBuildException if something goes wrong
     */
    private static void writeFieldExistsCheckCondition(
            final QueryStatement query,
            final QueryWriterResolver resolver,
            final String contextFieldPath,
            final Object queryParameter
    ) throws QueryBuildException {

        if (contextFieldPath == null) {
            throw new QueryBuildException("Field check conditions not allowed outside of field context.");
        }

        try {
            String isNullStr = String.valueOf(queryParameter);
            if (!(isNullStr.equalsIgnoreCase("true") || isNullStr.equalsIgnoreCase("false"))) {
                throw new QueryBuildException("Parameter for existence checking should represent boolean value.");
            }
            Boolean isNull = Boolean.parseBoolean(isNullStr);
            String condition = isNull ? "(%s) is null" : "(%s) is not null";
            query.getBodyWriter().write(String.format(condition, PostgresFieldPath.fromString(contextFieldPath).toSQL()));
        } catch (IOException e) {
            throw new QueryBuildException("Query search conditions write failed because of exception.", e);
        }
    }

    /**
     * Writes part of sql query which checks the value is presented in the array.
     * @param query query statement object where to write body and add parameter setters
     * @param resolver resolver for nested operators, is ignored here
     * @param contextFieldPath current field path, for example document#>'{field}'
     * @param queryParameter current parameter value
     * @throws QueryBuildException if something goes wrong
     */
    private static void writeFieldInArrayCheckCondition(
            final QueryStatement query,
            final QueryWriterResolver resolver,
            final String contextFieldPath,
            final Object queryParameter
    ) throws QueryBuildException {

        if (contextFieldPath == null) {
            throw new QueryBuildException("Operator \"$in\" not allowed outside of field context");
        }

        if (!(queryParameter instanceof List)) {
            throw new QueryBuildException("\"$in\" operator must be applied only to JSON array");
        }

        List paramAsList = (List) queryParameter;
        Writer writer = query.getBodyWriter();

        try {
            if (paramAsList.size() == 0) {
                writer.write("(FALSE)");
                return;
            }

            writer.write(String.format("((%s)in(", PostgresFieldPath.fromString(contextFieldPath).toSQL()));

            for (int i = paramAsList.size(); i > 0; --i) {
                writer.write(String.format("to_json(?)::jsonb%s", (i == 1) ? "" : ","));
            }

            writer.write("))");

            query.pushParameterSetter((statement, index) -> {
                for (Object obj : paramAsList) {
                    statement.setObject(index++, obj);
                }
                return index;
            });

        } catch (IOException e) {
            throw new QueryBuildException("Query search conditions write failed because of exception", e);
        }
    }

    /**
     * Creates the condition writer which depends on presence or absence type cast in the search criteria
     * @param formatWithoutTypeCast format string, contains '%s' for field path and '?' for parameters
     * @param formatWithTypeCast format string, contains '%s' for field path and type, '?' for parameters
     * @return the condition writer ready to be added to basic resolver
     */
    private static QueryWriter formattedCheckWriterForComparisons(
            final String formatWithoutTypeCast,
            final String formatWithTypeCast
    ) {
        return (query, resolver, contextFieldPath, queryParameter) -> {
            if (queryParameter instanceof IObject) {
                try {
                    IKey fieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
                    IFieldName valueFN = IOC.resolve(fieldNameKey, "value");
                    IFieldName typeFN = IOC.resolve(fieldNameKey, "type");
                    Object value = ((IObject) queryParameter).getValue(valueFN);
                    String type = (String) ((IObject) queryParameter).getValue(typeFN);

                    writeFieldCheckConditionWithTypeCast(formatWithTypeCast, query, contextFieldPath, value, type);
                } catch (ResolutionException e) {
                    throw new QueryBuildException("Unable to resolve dependency for 'value' and 'type' fields", e);
                } catch (ReadValueException | InvalidArgumentException e) {
                    throw new QueryBuildException("Unable to get 'value' and 'type' fields from the search criteria", e);
                } catch (ClassCastException e) {
                    throw new QueryBuildException("Cannot cast 'type' field in the search criteria to String", e);
                }
            } else {
                writeFieldCheckCondition(formatWithoutTypeCast, query, contextFieldPath, queryParameter);
            }

        };
    }
}
