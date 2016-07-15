package info.smart_tools.smartactors.core.db_tasks.psql.insert;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_storage.utils.QueryKey;
import info.smart_tools.smartactors.core.db_tasks.commons.CachedDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.DBInsertQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.IDBQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;

/**
 * Task for insert documents in postgres database.
 */
public class PSQLInsertTask extends CachedDatabaseTask {

    private final QueryStatementBuilder queryStatementBuilder;
    private final IDBQueryExecutor taskExecutor;

    /**
     * Default constructor.
     *              Creates a new instance of {@link PSQLInsertTask}.
     */
    private PSQLInsertTask() {
        queryStatementBuilder = QueryStatementBuilder.create();
        taskExecutor = DBInsertQueryExecutor.create();
    }

    /**
     * Factory method for creation a new instance of {@link PSQLInsertTask}.
     *
     * @return a new instance of {@link PSQLInsertTask}.
     */
    public static PSQLInsertTask create() {
        return new PSQLInsertTask();
    }

    @Nonnull
    @Override
    protected ICompiledQuery takeCompiledQuery(
            @Nonnull final IStorageConnection connection,
            @Nonnull final IObject message
    ) throws QueryBuildException {
        try {
            ICollectionName collection = DBQueryFields.COLLECTION.in(message);
            IKey queryKey = QueryKey.create(
                    connection.getId(),
                    PSQLInsertTask.class.toString(),
                    collection.toString());

            return takeCompiledQuery(
                    queryKey,
                    connection,
                    getQueryStatementBuilder(collection.toString()));
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param query
     * @param message
     * @return
     * @throws QueryBuildException
     */
    @Nonnull
    @Override
    protected ICompiledQuery setParameters(@Nonnull final ICompiledQuery query, @Nonnull final IObject message)
            throws QueryBuildException {
        try {
            IObject document = DBQueryFields.DOCUMENT.in(message);
            String documentJSON = document.serialize();
            query.setParameters(statement -> statement.setString(1, documentJSON));

            return query;
        } catch (ReadValueException | InvalidArgumentException | SerializeException e) {
            throw new QueryBuildException(e.getMessage(), e);
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

    /**
     *
     * @param collection
     * @return
     */
    private IQueryStatementBuilder getQueryStatementBuilder(final String collection) {
        return queryStatementBuilder.withCollection(collection);
    }
}
