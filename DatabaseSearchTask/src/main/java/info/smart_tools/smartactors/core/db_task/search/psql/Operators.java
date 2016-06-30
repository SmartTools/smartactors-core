package info.smart_tools.smartactors.core.db_task.search.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.core.sql_commons.ConditionsResolverBase;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.QueryConditionWriter;
import info.smart_tools.smartactors.core.sql_commons.QueryConditionWriterResolver;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import info.smart_tools.smartactors.core.sql_commons.psql.Schema;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Class for constructing queries with comparison operators and parameters
 */
final class Operators {
    private Operators(){}

    /**
     * Writes sql-query with basic field comparison operators
     * @param format Sql string for condition. Contains '%s' for field path and '?' for parameters
     * @param query Query statement object with part of sql query (select...) and parameter setters
     * @param contextFieldPath Field path, for example document#>'{field}'
     * @throws QueryBuildException
     */
    private static void writeFieldCheckCondition(
            final String format,
            final QueryStatement query,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {

        if (contextFieldPath == null) {
            throw new QueryBuildException("Field check conditions not allowed outside of field context.");
        }

        try {
            query.getBodyWriter().write(String.format(format, contextFieldPath.getSQLRepresentation()));

            setters.add((statement, index) -> {
                statement.setObject(index++, queryParameter);
                return index;
            });
        } catch (IOException e) {
            throw new QueryBuildException("Query search conditions write failed because of exception.", e);
        }
    }

    /**
     * Writes sql-query which checks existence of field into document
     * @param query Query statement object with part of sql query (select...) and parameter setters
     * @param resolver Resolver with lambdas for writing sql by operators
     * @param contextFieldPath Field path, for example document#>'{field}'
     * @param queryParameter Parameter value. Should be boolean: if true check field is null and vice versa
     * @throws QueryBuildException
     */
    private static void writeFieldExistsCheckCondition(
            final QueryStatement query,
            final QueryConditionWriterResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<SQLQueryParameterSetter> setters
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
            query.getBodyWriter().write(String.format(condition, contextFieldPath.getSQLRepresentation()));
        } catch (IOException e) {
            throw new QueryBuildException("Query search conditions write failed because of exception.", e);
        }
    }

    /**
     *
     * @param query Query statement object with part of sql query (select...) and parameter setters
     * @param resolver Resolver with lambdas for writing sql by operators
     * @param contextFieldPath Field path, for example document#>'{field}'
     * @param queryParameter Parameter value.
     * @throws QueryBuildException
     */
    private static void writeFieldInArrayCheckCondition(
            final QueryStatement query,
            final QueryConditionWriterResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {

        if (contextFieldPath == null) {
            throw new QueryBuildException("Operator \"$in\" not allowed outside of field context.");
        }

        if (!List.class.isAssignableFrom(queryParameter.getClass())) {
            throw new QueryBuildException("Parameter of \"$in\" operator should be an JSON array.");
        }

        List<Object> paramAsList = (List<Object>) queryParameter;
        Writer writer = query.getBodyWriter();

        try {
            if (paramAsList.size() == 0) {
                writer.write("(FALSE)");
                return;
            }

            writer.write(String.format("((%s)in(", contextFieldPath.getSQLRepresentation()));

            for (int i = paramAsList.size(); i > 0; --i) {
                writer.write(String.format("to_json(?)::jsonb%s", (i == 1) ? "" : ","));
            }

            writer.write("))");

            setters.add((statement, index) -> {
                for (Object obj : paramAsList) {
                    statement.setObject(index++, obj);
                }
                return index;
            });
        } catch (IOException e) {
            throw new QueryBuildException("Query search conditions write failed because of exception.", e);
        }
    }

    private static QueryConditionWriter formattedCheckWriter(final String format) {
        return (query, resolver, contextFieldPath, queryParameter, setters) ->
            writeFieldCheckCondition(format, query, contextFieldPath, queryParameter, setters);
    }

    /**
     * Registers operators.
     * @param resolver Resolver for writing queries by operators
     */
    public static void addAll(@Nonnull final ConditionsResolverBase resolver) {
        // Basic field comparison operators
        resolver.addOperator("$eq", formattedCheckWriter("((%s)=to_json(?)::jsonb)"));
        resolver.addOperator("$ne", formattedCheckWriter("((%s)!=to_json(?)::jsonb)"));
        resolver.addOperator("$lt", formattedCheckWriter("((%s)<to_json(?)::jsonb)"));
        resolver.addOperator("$gt", formattedCheckWriter("((%s)>to_json(?)::jsonb)"));
        resolver.addOperator("$lte", formattedCheckWriter("((%s)<=to_json(?)::jsonb)"));
        resolver.addOperator("$gte", formattedCheckWriter("((%s)>=to_json(?)::jsonb)"));

        //Check on present
        resolver.addOperator("$isNull", Operators::writeFieldExistsCheckCondition);

        // ISO 8601 date/time operators
        /*TODO: Find a way to build an index on date/time field.*/
        resolver.addOperator("$date-from", formattedCheckWriter("(parse_timestamp_immutable(%s)>=(?)::timestamp)"));
        resolver.addOperator("$date-to", formattedCheckWriter("(parse_timestamp_immutable(%s)<=(?)::timestamp)"));

        // Value in list check
        resolver.addOperator("$in", Operators::writeFieldInArrayCheckCondition);

        // Tags operators
        resolver.addOperator("$hasTag", formattedCheckWriter("((%s)??(?))"));

        // Fulltext search
        resolver.addOperator("$fulltext", formattedCheckWriter(
                String.format("(to_tsvector('%s',(%%s)::text))@@(to_tsquery(%s,?))",
                        Schema.FTS_DICTIONARY, Schema.FTS_DICTIONARY)));
    }
}
