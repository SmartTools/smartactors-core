package info.smart_tools.smartactors.database_postgresql.postgres_schema;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.OrderWriter;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.PagingWriter;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.PostgresQueryWriterResolver;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * A set of methods to write parts of search SQL query.
 */
final class SearchClauses {

    /**
     * Private constructor to avoid instantiation.
     */
    private SearchClauses() {
    }

    /**
     * Writes WHERE clause of the select statement.
     *
     * @param statement statement which body to write and which parameters to set
     * @param criteria  search criteria
     * @throws Exception if the clause cannot be written
     */
    static void writeSearchWhere(final QueryStatement statement, final IObject criteria) throws Exception {
        IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        Writer body = statement.getBodyWriter();
        try {
            IFieldName filterField = IOC.resolve(fieldNameKey, "filter");
            IObject filter = (IObject) criteria.getValue(filterField);
            if (filter == null) {
                return; // no filter in the criteria, ignoring
            }
            body.write(" WHERE ");
            PostgresQueryWriterResolver resolver = new PostgresQueryWriterResolver();
            resolver.resolve(null).write(statement, resolver, null, filter);
        } catch (ReadValueException e) {
            // no filter in the criteria, ignoring
        }
    }

    /**
     * Writes ORDER BY clause of the select statement.
     *
     * @param statement statement which body to write and which parameters to set
     * @param criteria  search criteria
     * @throws Exception if the clause cannot be written
     */
    static void writeSearchOrder(final QueryStatement statement, final IObject criteria) throws Exception {
        IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        Writer body = statement.getBodyWriter();
        try {
            IFieldName sortField = IOC.resolve(fieldNameKey, "sort");
            List<IObject> sortItems = (List<IObject>) criteria.getValue(sortField);
            if (sortItems == null || sortItems.isEmpty()) {
                return; // no sort in the criteria, ignoring
            }
            body.write(" ");
            OrderWriter order = new OrderWriter();
            order.write(statement, sortItems);
        } catch (ReadValueException e) {
            // no sort in the criteria, ignoring
        }
    }

    /**
     * Writes OFFSET and LIMIT clauses of the select statement using page size and number.
     *
     * @param statement statement which body to write and which parameters to set
     * @param criteria  search criteria
     * @throws Exception if the clause cannot be written
     */
    static void writeSearchPagingByPageSizeAndNumber(final QueryStatement statement, final IObject criteria) throws Exception {
        IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        Writer body = statement.getBodyWriter();
        try {
            IFieldName pageField = IOC.resolve(fieldNameKey, "page");
            IObject page = (IObject) criteria.getValue(pageField);
            if (page == null) {
                writeDefaultPaging(statement);
                return; // no page in the criteria, ignoring
            }

            try {
                body.write(" ");
                PagingWriter paging = new PagingWriter();
                IFieldName sizeField = IOC.resolve(fieldNameKey, "size");
                Integer size = (Integer) page.getValue(sizeField);
                IFieldName numberField = IOC.resolve(fieldNameKey, "number");
                Integer number = (Integer) page.getValue(numberField);
                paging.writeByPageSizeAndNumber(statement, number, size);
            } catch (Exception e) {
                throw new QueryBuildException("wrong page format: " + page.serialize(), e);
            }
        } catch (ReadValueException e) {
            writeDefaultPaging(statement);
            // no page in the criteria, ignoring
        }
    }

    /**
     * Writes OFFSET and LIMIT clauses of the select statement using limit and offset params.
     *
     * @param statement statement which body to write and which parameters to set
     * @param criteria  search criteria
     * @throws Exception if the clause cannot be written
     */
    static void writeSearchPagingByLimitAndOffset(final QueryStatement statement, final IObject criteria) throws Exception {
        IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        Writer body = statement.getBodyWriter();
        try {
            IFieldName pageField = IOC.resolve(fieldNameKey, "page");
            IObject page = (IObject) criteria.getValue(pageField);
            if (page == null) {
                writeDefaultPaging(statement);
                return; // no page in the criteria, ignoring
            }

            try {
                body.write(" ");
                PagingWriter paging = new PagingWriter();
                IFieldName limitField = IOC.resolve(fieldNameKey, "limit");
                Integer limit = (Integer) page.getValue(limitField);
                IFieldName offsetField = IOC.resolve(fieldNameKey, "offset");
                Object offsetRaw = page.getValue(offsetField);
                Long offset = (offsetRaw != null) ? ((Number) offsetRaw).longValue() : null;
                paging.writeByLimitAndOffset(statement, limit, offset);
            } catch (Exception e) {
                throw new QueryBuildException("wrong page format: " + page.serialize(), e);
            }
        } catch (ReadValueException e) {
            writeDefaultPaging(statement);
            // no page in the criteria, ignoring
        }
    }

    /**
     * Writes default paging (LIMIT) of default size of 100.
     *
     * @param statement statement which body to write and which parameters to set
     * @throws IOException         if write to body failed
     * @throws QueryBuildException if query cannot be formatted
     */
    static void writeDefaultPaging(final QueryStatement statement) throws IOException, QueryBuildException {
        Writer body = statement.getBodyWriter();
        body.write(" ");
        PagingWriter paging = new PagingWriter();
        paging.writeByPageSizeAndNumber(statement, 1, PostgresSchema.DEFAULT_PAGE_SIZE);
    }
}
