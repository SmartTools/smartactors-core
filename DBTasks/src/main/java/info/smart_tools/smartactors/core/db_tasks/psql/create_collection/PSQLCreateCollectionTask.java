package info.smart_tools.smartactors.core.db_tasks.psql.create_collection;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.GeneralDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.DBCreateCollectionQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.IDBQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Task for create collection with predefined indexes in psql database.
 */
public class PSQLCreateCollectionTask extends GeneralDatabaseTask {
    private final QueryStatementBuilder queryStatementBuilder;
    private final IDBQueryExecutor taskExecutor;

    /**
     * Default constructor.
     *              Creates a new instance of {@link PSQLCreateCollectionTask}.
     */
    private PSQLCreateCollectionTask() {
        queryStatementBuilder =  QueryStatementBuilder.create();
        taskExecutor = DBCreateCollectionQueryExecutor.create();
    }

    /**
     * Factory method for creation a new instance of {@link PSQLCreateCollectionTask}.
     *
     * @return a new instance of {@link PSQLCreateCollectionTask}.
     */
    public static PSQLCreateCollectionTask create() {
        return new PSQLCreateCollectionTask();
    }

    @Override
    protected boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException {
        return taskExecutor.isExecutable(message);
    }

    @Nonnull
    @Override
    protected ICompiledQuery takeCompiledQuery(@Nonnull final IStorageConnection connection,
                                               @Nonnull final IObject message
    ) throws QueryBuildException {
        try {
            ICollectionName collection = DBQueryFields.COLLECTION.in(message);
            return compileQuery(
                    connection,
                    getQueryStatementBuilder(
                            collection.toString(),
                            DBQueryFields.INDEXES.in(message)));
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }

    @Nonnull
    @Override
    protected ICompiledQuery setParameters(@Nonnull final ICompiledQuery query,
                                           @Nonnull final IObject message
    ) throws QueryBuildException {
        return query;
    }

    @Override
    protected void execute(@Nonnull final ICompiledQuery query,
                           @Nonnull final IObject message
    ) throws TaskExecutionException {
        taskExecutor.execute(query, message);
    }

    private IQueryStatementBuilder getQueryStatementBuilder(final String collection,
                                                            final Map<String, String> indexes
    ) {
        return queryStatementBuilder
                .withCollection(collection)
                .withIndexes(indexes);
    }
}
