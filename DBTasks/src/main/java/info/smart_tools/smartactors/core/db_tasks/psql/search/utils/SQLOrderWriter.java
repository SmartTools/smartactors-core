package info.smart_tools.smartactors.core.db_tasks.psql.search.utils;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.ifield.IField;
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
 * General writer an ORDER clause for psql db.
 */
public class SQLOrderWriter {
    private static final IField ORDER_FIELD_ORDER_BY_ITEM_FN;
    private static final IField ORDER_DIRECTION_ORDER_BY_ITEM_FN;

    static {
        try {
            ORDER_FIELD_ORDER_BY_ITEM_FN =
                    IOC.resolve(Keys.getOrAdd(IField.class.toString()), "field");
            ORDER_DIRECTION_ORDER_BY_ITEM_FN =
                    IOC.resolve(Keys.getOrAdd(IField.class.toString()), "order");
        } catch (ResolutionException e) {
            throw new RuntimeException("SQL order writer's fields initialize error: " + e.getMessage(), e);
        }
    }

    private SQLOrderWriter() { }

    /**
     *
     * @return
     */
    public static SQLOrderWriter create() {
        return new SQLOrderWriter();
    }

    /**
     * Writes an ORDER clause into the query statement.
     *
     * @param queryStatement - a compiled statement of query.
     * @param order - custom result order.
     *
     * @throws QueryBuildException when:
     *              1. Writing body of query error;
     *              2. IOC resolution error object;
     *              3. Reading field from <pre>queryMessage</pre> error.
     */
    public void write(
            @Nonnull final QueryStatement queryStatement,
            final List<IObject> order
    ) throws QueryBuildException {
        if (order == null || order.size() == 0) {
            return;
        }

        try {
            queryStatement.getBodyWriter().write("ORDER BY");

            for (IObject orderByItem : order) {
                String sortDirection = ORDER_DIRECTION_ORDER_BY_ITEM_FN.in(orderByItem);
                FieldPath fieldPath =
                        PSQLFieldPath.fromString(ORDER_FIELD_ORDER_BY_ITEM_FN.in(orderByItem));

                sortDirection = ("DESC".equalsIgnoreCase(String.valueOf(sortDirection))) ? "DESC" : "ASC";
                StringBuilder orderPart = new StringBuilder("(")
                        .append(fieldPath.getSQLRepresentation())
                        .append(")")
                        .append(sortDirection)
                        .append(",");
                queryStatement.getBodyWriter().write(orderPart.toString());
            }

            queryStatement.getBodyWriter().write("(1)");
        } catch (IOException | ReadValueException e) {
            throw new QueryBuildException("Error while writing ORDER BY clause of search query SQL.", e);
        } catch (InvalidArgumentException e) {
            throw new QueryBuildException("Invalid argument exception" , e);
        }
    }
}
