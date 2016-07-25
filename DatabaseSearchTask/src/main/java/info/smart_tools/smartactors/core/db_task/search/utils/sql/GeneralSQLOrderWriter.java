package info.smart_tools.smartactors.core.db_task.search.utils.sql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.core.db_task.search.psql.PSQLFieldPath;
import info.smart_tools.smartactors.core.db_task.search.utils.ISearchQueryWriter;
import info.smart_tools.smartactors.core.db_task.search.wrappers.ISearchQuery;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * {@see SearchQueryWriter} {@link ISearchQueryWriter}.
 * General writer an ORDER clause for psql db.
 */
public class GeneralSQLOrderWriter implements ISearchQueryWriter {
    private static final IFieldName ORDER_FIELD_ORDER_BY_ITEM_FN;
    private static final IFieldName ORDER_DIRECTION_ORDER_BY_ITEM_FN;

    static {
        try {
            ORDER_FIELD_ORDER_BY_ITEM_FN =
                    IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "field");
            ORDER_DIRECTION_ORDER_BY_ITEM_FN =
                    IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "order");
        } catch (ResolutionException e) {
            throw new RuntimeException("Static block initialize error: " + e.getMessage(), e);
        }
    }

    private GeneralSQLOrderWriter() {}

    /**
     * Factory method for creation a new instance of <pre>GeneralSQLOrderWriter</pre>.
     *
     * @return new instance of <pre>GeneralSQLOrderWriter</pre>.
     */
    public static GeneralSQLOrderWriter create() {
        return new GeneralSQLOrderWriter();
    }

    /**
     * Writes an ORDER clause into the query statement.
     *
     * @param queryStatement - a compiled statement of query.
     * @param queryMessage - message with parameters for query.
     * @param setters - list of query parameters setter.
     *                Any setter sets some parameter into query.
     *
     * @throws QueryBuildException when:
     *              1. Writing body of query error;
     *              2. IOC resolution error object;
     *              3. Reading field from <pre>queryMessage</pre> error.
     */
    @Override
    public void write(
            @Nonnull final QueryStatement queryStatement,
            @Nonnull final ISearchQuery queryMessage,
            @Nonnull final List<SQLQueryParameterSetter> setters
    ) throws QueryBuildException {
        if (queryMessage.countOrderBy() == 0) {
            return;
        }

        try {
            queryStatement.getBodyWriter().write("ORDER BY");

            for (int i = 0; i < queryMessage.countOrderBy(); ++i) {
                IObject orderItem = queryMessage.getOrderBy(i);

                FieldPath fieldPath = IOC.resolve(
                        Keys.getOrAdd(PSQLFieldPath.class.getCanonicalName()),
                        orderItem.getValue(ORDER_FIELD_ORDER_BY_ITEM_FN));

                String sortDirection = IOC.resolve(
                        Keys.getOrAdd(String.class.getCanonicalName()),
                        orderItem.getValue(ORDER_DIRECTION_ORDER_BY_ITEM_FN));

                sortDirection = ("DESC".equalsIgnoreCase(String.valueOf(sortDirection))) ? "DESC" : "ASC";
                queryStatement
                        .getBodyWriter()
                        .write(String.format("(%s)%s,", fieldPath.getSQLRepresentation(), sortDirection));
            }

            queryStatement.getBodyWriter().write("(1)");
        } catch (IOException | ResolutionException | ReadValueException e) {
            throw new QueryBuildException("Error while writing ORDER BY clause of search query SQL.", e);
        } catch (InvalidArgumentException e) { //TODO added by AKutalev, reason: now IObject can throw InvalidArgumentException
            throw new QueryBuildException("Invalid argument exception" , e);
        }
    }
}
