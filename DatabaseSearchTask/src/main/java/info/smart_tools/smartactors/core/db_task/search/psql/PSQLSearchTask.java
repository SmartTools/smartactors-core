package info.smart_tools.smartactors.core.db_task.search.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.search.DBSearchTask;
import info.smart_tools.smartactors.core.db_task.search.utils.IBufferedQuery;
import info.smart_tools.smartactors.core.db_task.search.utils.ISearchQueryWriter;
import info.smart_tools.smartactors.core.db_task.search.utils.sql.GeneralSQLOrderWriter;
import info.smart_tools.smartactors.core.db_task.search.utils.sql.GeneralSQLPagingWriter;
import info.smart_tools.smartactors.core.db_task.search.wrappers.ISearchQuery;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.QueryConditionWriterResolver;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Task for searching documents in database.
 */
public class PSQLSearchTask extends DBSearchTask {
    /** Current connection to database. */
    private StorageConnection connection;
    /** Compiled searching query. */
    private CompiledQuery query;
    /** {@see SearchQuery} {@link ISearchQuery} */
    private ISearchQuery message;

    /** Modules for building a searching query. */
    private QueryConditionWriterResolver conditionsWriterResolver;
    private ISearchQueryWriter orderWriter;
    private ISearchQueryWriter pagingWriter;

    /**
     * A single constructor for creation {@link PSQLSearchTask}
     */
    private PSQLSearchTask(
            final QueryConditionWriterResolver conditionsWriterResolver,
            final ISearchQueryWriter orderWriter,
            final ISearchQueryWriter pagingWriter
    ) {
        this.conditionsWriterResolver = conditionsWriterResolver;
        this.orderWriter = orderWriter;
        this.pagingWriter = pagingWriter;
    }

    public static PSQLSearchTask create() {
        return new PSQLSearchTask(
                ConditionsWriterResolver.create(),
                GeneralSQLOrderWriter.create(),
                GeneralSQLPagingWriter.create());
    }

    /**
     * Factory method for creation new instance of {@link PSQLSearchTask}.
     *
     * @param bufferMaxSize
     */
    public static PSQLSearchTask create(final int bufferMaxSize) {
        return new PSQLSearchTask(
                ConditionsWriterResolver.create(),
                GeneralSQLOrderWriter.create(),
                GeneralSQLPagingWriter.create());
    }

    /**
     * {@see IDatabaseTask} {@link info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask}
     * Prepare query for searching documents in database by some criteria.
     *
     * @param prepareMessage {@see SearchQuery} {@link ISearchQuery}
     *
     * @throws TaskPrepareException when:
     *                1. IOC resolution error;
     *                2. Error building a searching query statement.
     */
    @Override
    public void prepare(@Nonnull final IObject prepareMessage) throws TaskPrepareException {
        try {
            ISearchQuery queryMessage = IOC.resolve(Keys.getOrAdd(ISearchQuery.class.getCanonicalName()), prepareMessage);
            IBufferedQuery searchQuery = queryMessage
                    .getBufferedQuery()
                    .orElse(createSearchQuery(queryMessage));
            queryMessage.setBufferedQuery(searchQuery);

            query = setQueryParameters(searchQuery.getCompiledQuery(), searchQuery.getParametersSetters());
            message = queryMessage;
        } catch (ResolutionException e) {
            throw new TaskPrepareException(e.getMessage(), e);
        } catch (StorageException e) {
            throw new TaskPrepareException("Error compiled query statement" + e.getMessage(), e);
        }
    }

    /**
     * {@see ITask} {@link info.smart_tools.smartactors.core.itask.ITask}
     * Executes searching documents by criteria from database.
     *
     * @throws TaskExecutionException when error in execution process.
     */
    @Override
    public void execute() throws TaskExecutionException {
        super.execute(query, message);
    }

    @Override
    public void setConnection(@Nonnull final StorageConnection storageConnection) throws TaskSetConnectionException {
        this.connection = storageConnection;
    }

    private IBufferedQuery createSearchQuery(final ISearchQuery queryMessage)
            throws TaskPrepareException, StorageException, ResolutionException {
        List<SQLQueryParameterSetter> setters = new ArrayList<>();
        CompiledQuery compiledQuery = connection.compileQuery(createQueryStatement(queryMessage, setters));
        return IOC.resolve(Keys.getOrAdd(IBufferedQuery.class.getCanonicalName()), compiledQuery, setters);
    }

    private QueryStatement createQueryStatement(
            final ISearchQuery queryMessage,
            final List<SQLQueryParameterSetter> setters
    ) throws TaskPrepareException {
        QueryStatement queryStatement = new QueryStatement();
        try {
            queryStatement.getBodyWriter().write(String.format("SELECT * FROM %s WHERE",
                    CollectionName.fromString(queryMessage.getCollectionName()).toString()));

            conditionsWriterResolver
                    .resolve(null)
                    .write(queryStatement, conditionsWriterResolver, null, queryMessage.getCriteria(), setters);
            orderWriter.write(queryStatement, queryMessage, setters);
            pagingWriter.write(queryStatement, queryMessage, setters);
        } catch (QueryBuildException e) {
            throw new TaskPrepareException("Error writing query statement: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new TaskPrepareException(e.getMessage(), e);
        }

        return queryStatement;
    }

    private CompiledQuery setQueryParameters(
            final CompiledQuery compiledQuery,
            final List<SQLQueryParameterSetter> setters
    ) throws TaskPrepareException {
        try {
            compiledQuery.setParameters(setters);
        } catch (SQLException | QueryBuildException e) {
            throw new TaskPrepareException("Error setting query parameters: " + e.getMessage(), e);
        }

        return compiledQuery;
    }
}

