package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;


import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A set of methods to correctly write down select conditions like AND or OR in the SQL queries.
 */
public final class Conditions {

    /**
     * Private constructor to avoid instantiation.
     */
    private Conditions(){
    }

    /**
     * Writes the AND condition.
     * @param query the query into which body to write the condition and add parameter setters
     * @param resolver a resolver which able to return correct {@link QueryWriter}
     * @param contextFieldPath current path to object field
     * @param queryParameter current query parameter value, the argument to the condition
     * @throws QueryBuildException if the query cannot be built
     */
    public static void writeAndCondition(
            final QueryStatement query,
            final QueryWriterResolver resolver,
            final String contextFieldPath,
            final Object queryParameter
    ) throws QueryBuildException {
        writeCompositeCondition("(", ")", "AND", query, resolver, contextFieldPath, queryParameter);
    }

    /**
     * Writes the OR condition.
     * @param query the query into which body to write the condition and add parameter setters
     * @param resolver a resolver which able to return correct {@link QueryWriter}
     * @param contextFieldPath current path to object field
     * @param queryParameter current query parameter value, the argument to the condition
     * @throws QueryBuildException if the query cannot be built
     */
    public static void writeOrCondition(
            final QueryStatement query,
            final QueryWriterResolver resolver,
            final String contextFieldPath,
            final Object queryParameter
    ) throws QueryBuildException {
        writeCompositeCondition("(", ")", "OR", query, resolver, contextFieldPath, queryParameter);
    }

    /**
     * Writes the NOT condition
     * @param query the query into which body to write the condition and add parameter setters
     * @param resolver a resolver which able to return correct {@link QueryWriter}
     * @param contextFieldPath current path to object field
     * @param queryParameter current query parameter value, the argument to the condition
     * @throws QueryBuildException if the query cannot be built
     */
    public static void writeNotCondition(
            final QueryStatement query,
            final QueryWriterResolver resolver,
            final String contextFieldPath,
            final Object queryParameter
    ) throws QueryBuildException {
        writeCompositeCondition("(NOT(", "))", "AND", query, resolver, contextFieldPath, queryParameter);
    }

    /**
     * Writes the default (true) condition to the query
     * @param query query where to write the condition
     * @throws IOException if write to the query body failed
     */
    private static void writeDefaultEmptyCondition(final QueryStatement query)
        throws IOException {
        query.getBodyWriter().write("(TRUE)");
    }

    /**
     * Writes some composite condition to the query.
     * @param prefix prefix to prepend the whole condition, typically a bracket (
     * @param postfix postfix to postpone the whole condition, typically a bracket )
     * @param delimiter characters to separate condition elements, for example "AND"
     * @param query the query into which body to write the condition and add parameter setters
     * @param resolver a resolver which able to return correct {@link QueryWriter}
     * @param contextFieldPath current path to object field
     * @param queryParameter current query parameter value, the argument to the condition
     * @throws QueryBuildException
     */
    private static void writeCompositeCondition(
            final String prefix,
            final String postfix,
            final String delimiter,
            final QueryStatement query,
            final QueryWriterResolver resolver,
            final String contextFieldPath,
            final Object queryParameter
    ) throws QueryBuildException {
        Writer writer = query.getBodyWriter();

        try {
            if (queryParameter instanceof IObject) {
                Iterator<Map.Entry<IFieldName, Object>> paramIterator = ((IObject) queryParameter).iterator();

                if (!paramIterator.hasNext()) {
                    writeDefaultEmptyCondition(query);
                    return;
                }

                writer.write(prefix);

                while (paramIterator.hasNext()) {
                    Map.Entry<IFieldName, Object> entry = paramIterator.next();
                    String key = String.valueOf(entry.getKey());
                    resolver.resolve(key).write(query, resolver, contextFieldPath, entry.getValue());
                    if (paramIterator.hasNext()) {
                        writer.write(delimiter);
                    }
                }

                writer.write(postfix);

            } else if (queryParameter instanceof List) {
                List<?> paramAsList = (List<?>) queryParameter;

                if (paramAsList.size() == 0) {
                    writeDefaultEmptyCondition(query);
                    return;
                }

                writer.write(prefix);

                QueryWriter resolved = resolver.resolve(null);

                Iterator<?> iterator = paramAsList.iterator();
                while (iterator.hasNext()) {
                    Object entry = iterator.next();
                    resolved.write(query, resolver, contextFieldPath, entry);
                    if (iterator.hasNext()) {
                        writer.write(delimiter);
                    }
                }

                writer.write(postfix);

            } else {
                throw new QueryBuildException("Composite node value should be an object or an array");
            }
        } catch (IOException e) {
            throw new QueryBuildException("Error while writing a query string", e);
        }
    }
}
