package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Writes the ORDER clause into request to Postgres database.
 */
public class OrderWriter {

    /**
     * Writes an ORDER clause into the query statement.
     * @param queryStatement - query where to write ORDER clause and add parameters.
     * @param sortMessage - message describing how to order, the ordered list
     * @throws QueryBuildException when something goes wrong
     */
    public void write(QueryStatement queryStatement, List<IObject> sortMessage) throws QueryBuildException {
        try {
            Writer writer = queryStatement.getBodyWriter();
            writer.write("ORDER BY");

            Iterator<IObject> sortObjects = sortMessage.iterator();
            while (sortObjects.hasNext()) {
                IObject sortObject = sortObjects.next();
                Iterator<Map.Entry<IFieldName, Object>> sortEntries = sortObject.iterator();
                while (sortEntries.hasNext()) {
                    Map.Entry<IFieldName, Object> entry = sortEntries.next();
                    FieldPath fieldPath = PostgresFieldPath.fromString(String.valueOf(entry.getKey()));     // TODO: convert using IOC
                    String sortDirection = resolveSortDirection(entry.getValue());      // TODO: convert using IOC
                    writer.write("(");
                    writer.write(fieldPath.toSQL());
                    writer.write(")");
                    writer.write(sortDirection);
                    if (sortObjects.hasNext() || sortEntries.hasNext()) {
                        writer.write(",");
                    }
                }
            }
        } catch (QueryBuildException qbe) {
            throw qbe;
        } catch (Exception e) {
            throw new QueryBuildException("Error while writing ORDER BY clause", e);
        }
    }

    private String resolveSortDirection(Object direction) throws QueryBuildException {
        if ("asc".equalsIgnoreCase(String.valueOf(direction))) {
            return "ASC";
        } else if ("desc".equalsIgnoreCase(String.valueOf(direction))) {
            return "DESC";
        }
        throw new QueryBuildException("Invalid direction value: " + direction);
    }

}
