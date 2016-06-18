package info.smart_tools.smartactors.core.sql_commons;


import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.IObjectIterator;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Conditions {
    private Conditions(){}

    private static void writeDefaultEmptyCondition(QueryStatement query)
        throws IOException {
        query.getBodyWriter().write("(true)");
    }

    private static void writeCompositeCondition(
            String prefix,String postfix,
            String delimiter,
            QueryStatement query,
            QueryConditionWriterResolver resolver,
            FieldPath contextFieldPath,
            Object queryParameter,
            List<SQLQueryParameterSetter> setters
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

                    if(!iterator.hasNext()) {
                        break;
                    }

                    entry = iterator.next();
                    writer.write(delimiter);
                }
            } else if (IObject.class.isAssignableFrom(queryParameter.getClass())) {
                IObjectIterator paramIterator = ((IObject)queryParameter).iterator();

                if(!paramIterator.next()) {
                    writeDefaultEmptyCondition(query);
                    return;
                }

                writer.write(prefix);

                do {
                    String key = paramIterator.getName().toString();
                    resolver.resolve(key).write(query, resolver, contextFieldPath, paramIterator.getValue(), setters);

                    if(!paramIterator.next()) {
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

                    if(!iterator.hasNext()) {
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
            throw new QueryBuildException("Error while writing a query string.",e);
        }
    }

    public static void writeAndCondition(
            QueryStatement query,
            QueryConditionWriterResolver resolver,
            FieldPath contextFieldPath,
            Object queryParameter,
            List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {
        writeCompositeCondition("(",")","AND",query,resolver,contextFieldPath,queryParameter, setters);
    }

    public static void writeOrCondition(
            QueryStatement query,
            QueryConditionWriterResolver resolver,
            FieldPath contextFieldPath,
            Object queryParameter,
            List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {
        writeCompositeCondition("(",")","OR",query,resolver,contextFieldPath,queryParameter, setters);
    }

    public static void writeNotCondition(
            QueryStatement query,
            QueryConditionWriterResolver resolver,
            FieldPath contextFieldPath,
            Object queryParameter,
            List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {
        writeCompositeCondition("(NOT(","))","AND",query,resolver,contextFieldPath,queryParameter, setters);
    }
}
