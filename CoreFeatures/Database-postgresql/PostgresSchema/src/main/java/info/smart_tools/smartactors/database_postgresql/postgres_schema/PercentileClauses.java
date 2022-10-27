package info.smart_tools.smartactors.database_postgresql.postgres_schema;

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

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

/**
 * A set of methods to write SQL statement to look up for percentiles in the collection
 */
final class PercentileClauses {

    /**
     * Private constructor to prevent instantiation
     */
    private PercentileClauses() {
    }

    /**
     * Writes expression for percentile search using percentile_disc function,
     * i.e. {@code percentile_disc(array[0.25, 0.75]) WITHIN GROUP (ORDER BY (...)::numeric}
     * @param queryStatement SQL query body to write
     * @param percentileCriteria JSON with parameters for percentile search
     * @throws QueryBuildException if failed to write the body
     */
    static void writePercentileDiscrete(final QueryStatement queryStatement, final IObject percentileCriteria) throws QueryBuildException {
        try {
            IKey fieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
            Writer writer = queryStatement.getBodyWriter();

            IFieldName fieldFN = IOC.resolve(fieldNameKey, "field");
            IFieldName valuesFN = IOC.resolve(fieldNameKey, "values");
            String fieldName = (String) percentileCriteria.getValue(fieldFN);
            List<Number> values = (List<Number>) percentileCriteria.getValue(valuesFN);

            writer.write("percentile_disc(");
            writer.write("array[");
            Iterator<Number> iterator = values.iterator();
            while (iterator.hasNext()) {
                Number value = iterator.next();
                writer.write(String.valueOf(value));
                if (iterator.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("]) WITHIN GROUP (");
            writer.write("ORDER BY (");
            writer.write(PostgresSchema.DOCUMENT_COLUMN);
            writer.write("#>>'{");
            writer.write(fieldName); // TODO: do something about possible SQL injections
            writer.write("}')::numeric)");
        } catch (ResolutionException e) {
            throw new QueryBuildException("Failed to resolve IFieldName key", e);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException("Unable to get value from percentile criteria object");
        } catch (IOException e) {
            throw new QueryBuildException("Unable to write to query", e);
        }
    }
}
