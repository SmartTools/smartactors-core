package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_storage.utils.QueryKey;
import info.smart_tools.smartactors.core.db_tasks.commons.ComplexDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.DBSearchQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.IDBQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IComplexQueryStatementBuilder;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.SQLOrderWriter;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.SQLPagingWriter;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Task for searching documents in database.
 */
public class PSQLSearchTask extends ComplexDatabaseTask {
    private final SQLPagingWriter pagingWriter;
    private final SearchQueryStatementBuilder queryStatementBuilder;
    private final IDBQueryExecutor taskExecutor;

    private PSQLSearchTask(final int minPageSize, final int maxPageSize) {
        taskExecutor = DBSearchQueryExecutor.create();
        pagingWriter = SQLPagingWriter.create(minPageSize, maxPageSize);
        queryStatementBuilder = SearchQueryStatementBuilder.create(
                PSQLConditionsResolver.create(),
                SQLOrderWriter.create(),
                SQLPagingWriter.create(minPageSize, maxPageSize));
    }

    /**
     *
     * @param minPageSize
     * @param maxPageSize
     * @return
     */
    public static PSQLSearchTask create(final int minPageSize, final int maxPageSize) {
       return new PSQLSearchTask(minPageSize, maxPageSize);
    }

    @Override
    protected boolean isExecutable(@Nonnull final IObject queryMessage) throws InvalidArgumentException {
        return true;
    }

    @Nonnull
    @Override
    protected ICompiledQuery takeCompiledQuery(@Nonnull final IStorageConnection connection,
                                               @Nonnull final IObject message)
            throws QueryBuildException {
        try {
            ICollectionName collection = DBQueryFields.COLLECTION.in(message);
            IObject criteria = DBQueryFields.CRITERIA.in(message);
            List<IObject> order = DBQueryFields.ORDER_BY.in(message);

            IKey queryKey = QueryKey.create(
                    connection.getId(),
                    PSQLSearchTask.class.toString(),
                    collection.toString(),
                    criteria.hashCode());

            return takeCompiledQuery(
                    queryKey,
                    connection,
                    getQueryStatementBuilder(collection.toString(), criteria, order));
        } catch (NullPointerException e) {
            throw new QueryBuildException("Message and connection must not be a null!");
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }

    @Nonnull
    @Override
    protected ICompiledQuery setParameters(@Nonnull final ICompiledQuery query,
                                           @Nonnull final IObject message
    ) throws QueryBuildException {
        List<Object> sortedParams = sortParameters(query, message);
        query.setParameters(statement -> {
            int parametersSize = sortedParams.size();
            for (int i = 0; i < parametersSize; ++i) {
                statement.setObject(i + 1, sortedParams.get(i));
            }
        });

        return query;
    }

    @Override
    protected void execute(@Nonnull final ICompiledQuery query,
                           @Nonnull final IObject message
    ) throws TaskExecutionException {
        try {
            taskExecutor.execute(query, message);
        } catch (NullPointerException e) {
            throw new TaskExecutionException("Query and message must not be a null!");
        }
    }

    @Override
    protected List<Object> sortParameters(final ICompiledQuery query, final IObject message)
            throws QueryBuildException {
        try {
            List<Object> sortedParams = super.sortParameters(query, message);

            int pageSize = pagingWriter.takePageSize(message);
            int pageNumber = pagingWriter.takePageNumber(message);
            sortedParams.add(pageSize);
            sortedParams.add(pageSize * pageNumber);

            return sortedParams;
        } catch (NullPointerException e) {
            throw new QueryBuildException("Query and message must not be a null!");
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }

    private IComplexQueryStatementBuilder getQueryStatementBuilder(final String collection,
                                                                   final IObject criteria,
                                                                   final List<IObject> order
    ) {
        return queryStatementBuilder
                .withCollection(collection)
                .withCriteria(criteria)
                .withOrderBy(order);
    }
}

