package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_storage.utils.QueryKey;
import info.smart_tools.smartactors.core.db_tasks.commons.CachedDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.DBSearchQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.IDBQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Database task for search documents by id
 */
public class PSQLSearchByIdTask extends CachedDatabaseTask {
    /**  */
    private final SearchByIdQueryStatementBuilder queryStatementBuilder;
    private final IDBQueryExecutor taskExecutor;

    /**
     * Constructor for DBGetByIdTask
     */
    protected PSQLSearchByIdTask() {
        queryStatementBuilder = SearchByIdQueryStatementBuilder.create();
        taskExecutor = DBSearchQueryExecutor.create();
    }

    /**
     * Factory method for creation a new instance of {@link PSQLSearchByIdTask}.
     *
     * @return a new instance of {@link PSQLSearchByIdTask}.
     */
    public static PSQLSearchByIdTask create() {
        return new PSQLSearchByIdTask();
    }

    @Override
    protected boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException {
        try {
            return DBQueryFields.DOCUMENT_ID.in(message) != null;
        } catch (ReadValueException e) {
            throw new InvalidArgumentException(e.getMessage(), e);
        }
    }

    @Nonnull
    @Override
    protected ICompiledQuery takeCompiledQuery(@Nonnull final IStorageConnection connection,
                                               @Nonnull final IObject message
    ) throws QueryBuildException {
        try {
            ICollectionName collection = DBQueryFields.COLLECTION.in(message);
            IKey queryKey = QueryKey.create(
                    connection.getId(),
                    PSQLSearchByIdTask.class.toString(),
                    collection.toString());

            return takeCompiledQuery(
                    queryKey,
                    connection,
                    getQueryStatementBuilder(collection.toString()));
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }

    @Nonnull
    @Override
    protected ICompiledQuery setParameters(@Nonnull final ICompiledQuery query,
                                           @Nonnull final IObject message
    ) throws QueryBuildException {
        try {
            Long id = DBQueryFields.DOCUMENT_ID.in(message);
            query.setParameters((statement) -> statement.setLong(1, id));

            return query;
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        } catch (ClassCastException e) {
            throw new QueryBuildException("Document id field must be of 'java.lang.Long' type!");
        }
    }

    @Override
    protected void execute(@Nonnull final ICompiledQuery query,
                           @Nonnull final IObject message
    ) throws TaskExecutionException {
        try {
            taskExecutor.execute(query, message);
            List<IObject> result = DBQueryFields.SEARCH_RESULT.in(message);
            if (result.size() > 1) {
                throw new TaskExecutionException("'Search query' execution has been failed: " +
                        "the given id correspond to multiple users!");
            }
            DBQueryFields.SEARCH_RESULT.out(message, result.get(0));
        } catch (ChangeValueException | ReadValueException | InvalidArgumentException e) {
            throw new TaskExecutionException("'Search query' execution has been failed: " + e.getMessage(), e);
        }
    }

    private IQueryStatementBuilder getQueryStatementBuilder(final String collection) {
        return queryStatementBuilder.withCollection(collection);
    }
}