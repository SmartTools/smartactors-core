package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;

import java.io.IOException;
import java.util.Optional;

/**
 * Writes a PAGING (LIMIT and OFFSET) clauses into request to Postgres database.
 */
public class PagingWriter {

    /** Const param for paging */
    private static final int MAX_PAGE_SIZE = 1000;
    private static final int MIN_PAGE_SIZE = 1;

    /**
     * Creates the writer.
     */
    public PagingWriter() {
    }

    /**
     * Writes a PAGING (LIMIT and OFFSET) clause into the query statement by page number and size.
     * @param queryStatement the statement where to write the query body and add parameter setters
     * @param pageNumber number of the page of rows
     * @param pageSize size of the page or rows
     * @throws QueryBuildException when writing body of query errors
     */
    public void writeByPageSizeAndNumber(final QueryStatement queryStatement, final int pageNumber, final int pageSize) throws QueryBuildException {
        try {
            queryStatement.getBodyWriter().write("LIMIT(?)OFFSET(?)");
            queryStatement.pushParameterSetter((statement, index) -> {
                int size = pageSize;
                int page = pageNumber - 1;

                page = (page < 0) ? 0 : page;
                size = (size > MAX_PAGE_SIZE) ?
                        MAX_PAGE_SIZE : ((size < MIN_PAGE_SIZE) ? MIN_PAGE_SIZE : size);

                statement.setInt(index++, size);
                statement.setInt(index++, size * page);
                return index;
            });
        } catch (IOException e) {
            throw new QueryBuildException("Error while writing PAGING clause of search query", e);
        }
    }

    /**
     * Writes a PAGING (LIMIT and OFFSET) clause into the query statement using explicit limit and offset values.
     * @param queryStatement the statement where the query body and add parameter setters
     * @param limit row number limit to extract from database
     * @param offset [optional] number of rows to skip
     * @throws QueryBuildException when writing body of query errors
     */
    public void writeByLimitAndOffset(final QueryStatement queryStatement, final Integer limit, final Long offset) throws QueryBuildException {
        long offsetOrDefault = Optional.ofNullable(offset).orElse(0L);
        int limitValidated = Optional.ofNullable(limit).orElseThrow(
                () -> new QueryBuildException("Error while writing PAGING clause of search query"));
        try {
            queryStatement.getBodyWriter().write("LIMIT(?)OFFSET(?)");
            queryStatement.pushParameterSetter((statement, index) -> {
                statement.setInt(index++, limitValidated);
                statement.setLong(index++, offsetOrDefault);
                return index;
            });
        } catch (IOException e) {
            throw new QueryBuildException("Error while writing PAGING clause of search query", e);
        }
    }
}
