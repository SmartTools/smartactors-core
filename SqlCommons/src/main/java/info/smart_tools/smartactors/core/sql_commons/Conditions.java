package info.smart_tools.smartactors.core.sql_commons;


import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Conditions {
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
            final QueryConditionResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<IDeclaredParam> parametersOrder
    ) throws QueryBuildException {
        try {
            if (queryParameter == null) {
                writeDefaultEmptyCondition(query);
                return;
            }

            Writer writer = query.getBodyWriter();
            if (IObject.class.isAssignableFrom(queryParameter.getClass())) {
                Iterator<Map.Entry<IFieldName, Object>> paramIterator = ((IObject)queryParameter).iterator();

                if (!paramIterator.hasNext()) {
                    writeDefaultEmptyCondition(query);
                    return;
                }

                writer.write(prefix);

                do {
                    Map.Entry<IFieldName, Object> entry = paramIterator.next();
                    String key = entry.getKey().toString();
                    resolver.resolve(key).write(query, resolver, contextFieldPath, entry.getValue(), parametersOrder);

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
                    resolved.write(query, resolver, contextFieldPath, entry, parametersOrder);

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
            final QueryConditionResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<IDeclaredParam> parametersOrder
    ) throws QueryBuildException {
        writeCompositeCondition("(", ")", "AND", query, resolver, contextFieldPath, queryParameter, parametersOrder);
    }

    public static void writeOrCondition(
            final QueryStatement query,
            final QueryConditionResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<IDeclaredParam> parametersOrder
    ) throws QueryBuildException {
        writeCompositeCondition("(", ")", "OR", query, resolver, contextFieldPath, queryParameter, parametersOrder);
    }

    public static void writeNotCondition(
            final QueryStatement query,
            final QueryConditionResolver resolver,
            final FieldPath contextFieldPath,
            final Object queryParameter,
            final List<IDeclaredParam> parametersOrder
    ) throws QueryBuildException {
        writeCompositeCondition("(NOT(", "))", "AND", query, resolver, contextFieldPath, queryParameter, parametersOrder);
    }
}
