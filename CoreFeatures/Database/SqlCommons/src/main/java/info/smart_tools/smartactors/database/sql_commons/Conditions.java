package info.smart_tools.smartactors.database.sql_commons;


import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Conditions {
    private Conditions(){}

    private static void writeDefaultEmptyCondition(final QueryStatement query)
        throws IOException {
        query.getBodyWriter().write("(true)");
    }

    private static void writeCompositeCondition(
            final String prefix,
            final String postfix,
            final String delimiter,
            final QueryStatement query,
            final QueryConditionWriterResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {
        Writer writer = query.getBodyWriter();

        try {
            if (Map.class.isAssignableFrom(queryParameter.getClass())) {
                /*TODO: Remove this ranch after IObject`s will be used instead of Maps.*/
                Map<Object, Object> paramAsMap = (Map<Object, Object>) queryParameter;

                if (paramAsMap.size() == 0) {
                    writeDefaultEmptyCondition(query);
                    return;
                }

                writer.write(prefix);

                Iterator<Map.Entry<Object, Object>> iterator = paramAsMap.entrySet().iterator();
                Map.Entry<Object, Object> entry = iterator.next();

                while (entry != null) {
                    String key = (String) entry.getKey();
                    resolver.resolve(key).write(query, resolver, contextFieldPath, entry.getValue(), setters);

                    if (!iterator.hasNext()) {
                        break;
                    }

                    entry = iterator.next();
                    writer.write(delimiter);
                }
            } else if (IObject.class.isAssignableFrom(queryParameter.getClass())) {
                Iterator<Map.Entry<IFieldName, Object>> paramIterator = ((IObject) queryParameter).iterator();

                if (!paramIterator.hasNext()) {
                    writeDefaultEmptyCondition(query);
                    return;
                }

                writer.write(prefix);

                do {
                    Map.Entry<IFieldName, Object> entry = paramIterator.next();
                    String key = entry.getKey().toString();
                    resolver.resolve(key).write(query, resolver, contextFieldPath, entry.getValue(), setters);

                    if (!paramIterator.hasNext()) {
                        break;
                    }

                    writer.write(delimiter);
                } while (true);
            } else if (List.class.isAssignableFrom(queryParameter.getClass())) {
                List<?> paramAsList = (List<?>) queryParameter;

                if (paramAsList.size() == 0) {
                    writeDefaultEmptyCondition(query);
                    return;
                }

                writer.write(prefix);

                QueryConditionWriter resolved = resolver.resolve(null);

                Iterator<?> iterator = paramAsList.iterator();
                Object entry = iterator.next();

                while (entry != null) {
                    resolved.write(query, resolver, contextFieldPath, entry, setters);

                    if (!iterator.hasNext()) {
                        break;
                    }

                    entry = iterator.next();
                    writer.write(delimiter);
                }
            } else {
                throw new QueryBuildException("Error: composite node value should be an object or an array.");
            }

            writer.write(postfix);
        } catch (IOException e) {
            throw new QueryBuildException("Error while writing a query string.", e);
        }
    }

    public static void writeAndCondition(
            final QueryStatement query,
            final QueryConditionWriterResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {
        writeCompositeCondition("(", ")", "AND", query, resolver, contextFieldPath, queryParameter, setters);
    }

    public static void writeOrCondition(
            final QueryStatement query,
            final QueryConditionWriterResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {
        writeCompositeCondition("(", ")", "OR", query, resolver, contextFieldPath, queryParameter, setters);
    }

    public static void writeNotCondition(
            final QueryStatement query,
            final QueryConditionWriterResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {
        writeCompositeCondition("(NOT(", "))", "AND", query, resolver, contextFieldPath, queryParameter, setters);
    }
}
