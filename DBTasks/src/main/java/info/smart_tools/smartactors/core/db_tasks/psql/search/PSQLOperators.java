package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Class for constructing queries with comparison operators and parameters
 */
final class PSQLOperators {

    private PSQLOperators() { }

    /**
     * Writes sql-query with basic field comparison operators
     * @param format Sql string for condition. Contains '%s' for field path and '?' for parameters
     * @param query Query statement object with part of sql query (select...) and parameter setters
     * @throws QueryBuildException
     */
    private static void writeFieldCheckCondition(
            final String format,
            final QueryStatement query,
            final Object queryParameter,
            final List<IDeclaredParam> declaredParams
    ) throws QueryBuildException {
        try {
            query.getBodyWriter().write(format);
            addParameterToDeclared(declaredParams, queryParameter.toString(), 1);
        } catch (NullPointerException e) {
            throw new QueryBuildException("Field check conditions not allowed outside of field context.");
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
            final QueryConditionResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<IDeclaredParam> declaredParams
    ) throws QueryBuildException {
        try {
            String isNullStr = String.valueOf(queryParameter);
            if (!(isNullStr.equalsIgnoreCase("true") || isNullStr.equalsIgnoreCase("false"))) {
                throw new QueryBuildException("Parameter for existence checking should represent boolean value.");
            }
            Boolean isNull = Boolean.parseBoolean(isNullStr);
            String condition = isNull ? " is null" : " is not null";
            StringBuilder formatBuilder = new StringBuilder()
                    .append("(")
                    .append(contextFieldPath.getSQLRepresentation())
                    .append(")")
                    .append(condition);
            query.getBodyWriter().write(formatBuilder.toString());
        } catch (NullPointerException e) {
            throw new QueryBuildException("Field check conditions not allowed outside of field context.");
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
            final QueryConditionResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<IDeclaredParam> declaredParams
    ) throws QueryBuildException {
        try {
            List<Object> paramAsList = (List<Object>)queryParameter;
            Writer writer = query.getBodyWriter();
            if (paramAsList.size() == 0) {
                writer.write("(FALSE)");
                return;
            }
            String parameterName = paramAsList.get(0).toString();
            int parametersNumber = Integer.valueOf(paramAsList.get(1).toString());

            StringBuilder formatBuilder = new StringBuilder("((")
                    .append(contextFieldPath.getSQLRepresentation())
                    .append(")in(");

            for (int i = parametersNumber; i > 0; --i) {
                formatBuilder
                        .append("to_json(?)::jsonb")
                        .append((i == 1) ? "" : ",");
            }
            formatBuilder.append("))");
            writer.write(formatBuilder.toString());
            addParameterToDeclared(declaredParams, parameterName, parametersNumber);
        } catch (NumberFormatException e) {
            throw new QueryBuildException("Invalid query format of operator \"$in\".");
        } catch (ClassCastException e) {
            throw new QueryBuildException("Parameter of \"$in\" operator should be an JSON array.");
        } catch (NullPointerException e) {
            throw new QueryBuildException("Operator \"$in\" not allowed outside of field context.");
        } catch (IOException e) {
            throw new QueryBuildException("Query search conditions write failed because of exception.", e);
        }
    }

    private static QueryConditionWriter formattedCheckWriter(final String fPartFormat, final String sPartFormat) {
        return (query, resolver, contextFieldPath, queryParameter, parametersOrder) -> {
            try {
                StringBuilder formatBuilder = new StringBuilder(fPartFormat)
                        .append(contextFieldPath.getSQLRepresentation())
                        .append(sPartFormat);
                writeFieldCheckCondition(formatBuilder.toString(), query, queryParameter, parametersOrder);
            } catch (NullPointerException e) {
                throw new QueryBuildException("Field check conditions not allowed outside of field context.");
            }
        };
    }

    /**
     * Registers operators.
     * @param resolver Resolver for writing queries by operators
     */
    public static void addAll(@Nonnull final ConditionsResolverBase resolver) {
        // Basic field comparison operators
        resolver.addOperator("$eq", formattedCheckWriter("((", ")=to_json(?)::jsonb)"));
        resolver.addOperator("$ne", formattedCheckWriter("((", ")!=to_json(?)::jsonb)"));
        resolver.addOperator("$lt", formattedCheckWriter("((", ")<to_json(?)::jsonb)"));
        resolver.addOperator("$gt", formattedCheckWriter("((", ")>to_json(?)::jsonb)"));
        resolver.addOperator("$lte", formattedCheckWriter("((", ")<=to_json(?)::jsonb)"));
        resolver.addOperator("$gte", formattedCheckWriter("((", ")>=to_json(?)::jsonb)"));

        //Check on present
        resolver.addOperator("$isNull", PSQLOperators::writeFieldExistsCheckCondition);

        // ISO 8601 date/time operators
        /*TODO: Find a way to build an index on date/time field.*/
        resolver.addOperator("$date-from", formattedCheckWriter("(parse_timestamp_immutable(", ")>=(?)::timestamp)"));
        resolver.addOperator("$date-to", formattedCheckWriter("(parse_timestamp_immutable(", ")<=(?)::timestamp)"));

        // Value in list check
        resolver.addOperator("$in", PSQLOperators::writeFieldInArrayCheckCondition);

        // Tags operators
        resolver.addOperator("$hasTag", formattedCheckWriter("((", ")??(?))"));

        // Fulltext search
        resolver.addOperator("$fulltext", formattedCheckWriter(
                "(to_tsvector('russian',(", ")::text))@@(to_tsquery(russian,?))"));
    }

    private static void addParameterToDeclared(final List<IDeclaredParam> declaredParams,
                                               final String name,
                                               final int count
    ) throws QueryBuildException {
        try {
            IFieldName fieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), name);
            IDeclaredParam container = DeclaredParam.create(fieldName, count);
            if (declaredParams.contains(container)) {
                throw new QueryBuildException("Name of query parameter must be unique!");
            }
            declaredParams.add(container);
        } catch (ResolutionException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }
}
