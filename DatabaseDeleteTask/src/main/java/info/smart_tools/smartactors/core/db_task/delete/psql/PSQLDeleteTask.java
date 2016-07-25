package info.smart_tools.smartactors.core.db_task.delete.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.delete.DBDeleteTask;
import info.smart_tools.smartactors.core.db_task.delete.wrappers.DeletionQuery;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import info.smart_tools.smartactors.core.sql_commons.QueryStatementFactory;
import info.smart_tools.smartactors.core.sql_commons.exception.QueryStatementFactoryException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Collections;

/**
 * Task for deletion documents from database.
 */
public class PSQLDeleteTask extends DBDeleteTask {
    /** Current connection to database. */
    private StorageConnection connection;
    /** Compiled deletion query. */
    private CompiledQuery query;
    /** {@see DeletionQuery} {@link DeletionQuery} */
    private DeletionQuery message;

    /**
     * A single constructor for creation {@link PSQLDeleteTask}
     *
     */
    private PSQLDeleteTask() {}

    /**
     * Factory method for creation new instance of {@link PSQLDeleteTask}.
     */
    public static PSQLDeleteTask create() {
        return new PSQLDeleteTask();
    }

    /**
     * {@see IDatabaseTask} {@link info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask}
     * Prepare query for deletion documents from database by ids.
     *
     * @param message contains parameters for deletion query.
     *                Should contains list of documents ids deleted objects.
     *
     * @throws TaskPrepareException when:
     *                1. IOC resolution error;
     *                2. List of documents ids is null or empty;
     *                3. Error writing deletion query statement.
     */
    @Override
    public void prepare(@Nonnull final IObject message) throws TaskPrepareException {
        CollectionName collectionName;
        try {
            DeletionQuery queryMessage = IOC.resolve(Keys.getOrAdd(DeletionQuery.class.getCanonicalName()), message);

            if (queryMessage.countDocumentIds() == 0) {
                throw new TaskPrepareException("List of id's to delete should not be empty.");
            }

            collectionName = CollectionName.fromString(queryMessage.getCollectionName());
            CompiledQuery compiledQuery = IOC.resolve(
                    Keys.getOrAdd(CompiledQuery.class.getCanonicalName()),
                    connection,
                    //TODO: please, check this (was toString())
                    PSQLDeleteTask.class.getCanonicalName(),
                    getQueryStatementFactory(collectionName));

            this.query = formatQuery(compiledQuery, queryMessage);
            this.message = queryMessage;
        } catch (ResolutionException | QueryBuildException e) {
            throw new TaskPrepareException(e.getMessage(), e);
        }
    }

    /**
     * {@see ITask} {@link info.smart_tools.smartactors.core.itask.ITask}
     * Executes deletion documents by ids from database.
     *
     * @throws TaskExecutionException when error in execution process.
     */
    @Override
    public void execute() throws TaskExecutionException {
        if (query == null || message == null) {
            throw new TaskExecutionException("Should first prepare the task.");
        }

        super.execute(query, message);
    }

    @Override
    public void setConnection(@Nonnull final StorageConnection connection) {
        this.connection = connection;
    }

    private QueryStatementFactory getQueryStatementFactory(final CollectionName collectionName) {
        return () -> {
            QueryStatement queryStatement = new QueryStatement();
            Writer writer = queryStatement.getBodyWriter();
            try {
                int documentsIdsSize = message.countDocumentIds();
                queryStatement.getBodyWriter().write(String.format("DELETE FROM %s WHERE %s IN (",
                        collectionName, "id"));
                for (int i = documentsIdsSize; i > 0; --i) {
                    writer.write("?" + ((i == 1) ? "" : ","));
                }
                writer.write(")");
            } catch (IOException e) {
                throw new QueryStatementFactoryException("Error while writing deletion query statement.", e);
            } catch (Exception e) {
                throw new QueryStatementFactoryException(e.getMessage(), e);
            }

            return queryStatement;
        };
    }

    private CompiledQuery formatQuery(final CompiledQuery compiledQuery, final DeletionQuery queryMessage)
            throws QueryBuildException {

        int documentsIdsSize = queryMessage.countDocumentIds();
        try {
            compiledQuery.setParameters(Collections.singletonList((statement, index) -> {
                for (int i = 0; i < documentsIdsSize; ++i) {
                    statement.setLong(index++, queryMessage.getDocumentIds(i));
                }
                return index;
            }));
        } catch (SQLException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }

        return compiledQuery;
    }
}