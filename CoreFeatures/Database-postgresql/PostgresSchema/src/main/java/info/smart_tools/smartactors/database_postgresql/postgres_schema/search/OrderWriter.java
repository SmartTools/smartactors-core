package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

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
                    PathAndDirection pathAndDirection = resolveFieldOptions(entry);
                    writer.write("(");
                    writer.write(pathAndDirection.getPath().toSQL());
                    writer.write(")");
                    writer.write(pathAndDirection.getDirection());
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

    private PathAndDirection resolveFieldOptions(final Map.Entry<IFieldName, Object> entry) throws QueryBuildException {
        try {
            IKey fieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
            IFieldName directionFN = IOC.resolve(fieldNameKey, "direction");

            if (entry.getValue() instanceof String) {
                String direction = (String) entry.getValue();
                return new PathAndDirection(
                        PostgresFieldPath.fromString(String.valueOf(entry.getKey())),
                        resolveSortDirection(direction));
            }
            if (entry.getValue() instanceof IObject) {
                IFieldName typeFN = IOC.resolve(fieldNameKey, "type");
                IObject iObjectValue = (IObject) entry.getValue();
                String direction = (String) iObjectValue.getValue(directionFN);
                String type = (String) iObjectValue.getValue(typeFN);
                return new PathAndDirection(
                        PostgresFieldPath.fromStringAndType(
                                String.valueOf(entry.getKey()),
                                type
                        ),
                        resolveSortDirection(direction)
                );
            }
            throw new QueryBuildException("Cannot parse options for the field '" + String.valueOf(entry.getKey()) + "'");
        } catch (ResolutionException e) {
            throw new QueryBuildException("Cannot resolve dependency while building query ", e);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException("Cannot read value from field name options", e);
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

class PathAndDirection {
    private FieldPath path;
    private String direction;

    public PathAndDirection(FieldPath path, String direction) {
        this.path = path;
        this.direction = direction;
    }

    public FieldPath getPath() {
        return path;
    }

    public String getDirection() {
        return direction;
    }
}
