package info.smart_tools.smartactors.core.db_tasks.psql.delete;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_storage.utils.QueryKey;
import info.smart_tools.smartactors.core.db_tasks.commons.CachedDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.DBDeleteQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.IDBQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;

/**
 * Task for deletion documents from database.
 */
public class PSQLDeleteByIdTask extends CachedDatabaseTask {
    private final QueryStatementBuilder queryStatementBuilder;
    private final IDBQueryExecutor taskExecutor;

    /**
     * A single constructor for creation {@link PSQLDeleteByIdTask}
     *
     */
    private PSQLDeleteByIdTask() {
        queryStatementBuilder = QueryStatementBuilder.create();
        taskExecutor = DBDeleteQueryExecutor.create();
    }

    /**
     * Factory method for creation new instance of {@link PSQLDeleteByIdTask}.
     */
    public static PSQLDeleteByIdTask create() {
        return new PSQLDeleteByIdTask();
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
                    PSQLDeleteByIdTask.class.toString(),
                    collection.toString());

            return takeCompiledQuery(
                    queryKey,
                    connection,
                    getQueryStatementBuilder(collection.toString()));
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        } catch (ClassCastException e) {
            throw new QueryBuildException("Document id field must be of 'ICollectionName' type!");
        }
    }

    @Nonnull
    @Override
    protected ICompiledQuery setParameters(@Nonnull final ICompiledQuery query,
                                           @Nonnull final IObject message
    ) throws QueryBuildException {
        try {
            Long documentId = DBQueryFields.DOCUMENT_ID.in(message);
            query.setParameters(statement -> statement.setLong(1, documentId));

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
        taskExecutor.execute(query, message);
    }

    @Override
    protected boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException {
        return taskExecutor.isExecutable(message);
    }

    private IQueryStatementBuilder getQueryStatementBuilder(final String collection) {
        return queryStatementBuilder.withCollection(collection);
    }
}