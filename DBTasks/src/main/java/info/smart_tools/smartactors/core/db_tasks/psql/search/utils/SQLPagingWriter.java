package info.smart_tools.smartactors.core.db_tasks.psql.search.utils;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * General writer a PAGING(LIMIT and OFFSET) clause for psql db.
 */
public class SQLPagingWriter {
    private final int minPageSize;
    private final int maxPageSize;

    private SQLPagingWriter(final int minPageSize, final int maxPageSize) {
        this.minPageSize = minPageSize;
        this.maxPageSize = maxPageSize;
    }

    /**
     * Factory method for creation a new instance of <pre>SQLPagingWriter</pre>.
     *
     * @return a new instance of <pre>SQLPagingWriter</pre>.
     */
    public static SQLPagingWriter create(final int minPageSize, final int maxPageSize) {
        return new SQLPagingWriter(minPageSize, maxPageSize);
    }

    /**
     *
     * @param message
     * @return
     * @throws ReadValueException
     * @throws InvalidArgumentException
     */
    public int takePageSize(final IObject message) throws ReadValueException, InvalidArgumentException {
        int pageSize = DBQueryFields.PAGE_SIZE.in(message);
        return pageSize > maxPageSize ?
                maxPageSize : (pageSize < minPageSize ? minPageSize : pageSize);
    }

    /**
     *
     * @param message
     * @return
     * @throws ReadValueException
     * @throws InvalidArgumentException
     */
    public int takePageNumber(final IObject message) throws ReadValueException, InvalidArgumentException {
        int pageNumber = DBQueryFields.PAGE_NUMBER.in(message);
        pageNumber -= 1;
        return pageNumber < 0 ? 0 : pageNumber;
    }

    /**
     * Writes a PAGING(LIMIT and OFFSET) clause into the query statement.
     *
     * @param queryStatement - a compiled statement of query.
     *
     * @throws QueryBuildException when writing body of query error.
     */
    public void write(@Nonnull final QueryStatement queryStatement) throws QueryBuildException {
        try {
            queryStatement.getBodyWriter().write("LIMIT(?)OFFSET(?)");
        } catch (IOException e) {
            throw new QueryBuildException("Error while writing PAGING clause of search query SQL.", e);
        }
    }
}
