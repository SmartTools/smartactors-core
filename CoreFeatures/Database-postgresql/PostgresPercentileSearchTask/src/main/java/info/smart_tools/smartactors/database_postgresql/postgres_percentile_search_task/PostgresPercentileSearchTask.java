package info.smart_tools.smartactors.database_postgresql.postgres_percentile_search_task;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.PostgresSchema;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PostgresPercentileSearchTask implements IDatabaseTask {

    /**
     * Connection to the database.
     */
    private IStorageConnection connection;
    /**
     * Name of the collection.
     */
    private CollectionName collection;
    /**
     * Criteria to search the document.
     */
    private IObject criteria;
    /**
     * Criteria for percentiles
     */
    private IObject percentileCriteria;
    /**
     * Callback function to call when the object is found.
     */
    private IAction<Number[]> callback;
    /**
     * Query, prepared during prepare(), to be compiled during execute().
     */
    private QueryStatement preparedQuery;

    /**
     * Creates the task
     * @param connection the database connection where to perform search
     */
    public PostgresPercentileSearchTask(final IStorageConnection connection) {
        this.connection = connection;
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            PercentileSearchMessage message = IOC.resolve(Keys.getKeyByName(PercentileSearchMessage.class.getCanonicalName()), query);
            collection = message.getCollectionName();
            criteria = message.getCriteria();
            percentileCriteria = message.getPercentileCriteria();
            callback = message.getCallback();

            preparedQuery = new QueryStatement();
            PostgresSchema.percentileSearch(preparedQuery, collection, percentileCriteria, criteria);
        } catch (Exception e) {
            throw new TaskPrepareException(e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            JDBCCompiledQuery compiledQuery = (JDBCCompiledQuery) connection.compileQuery(preparedQuery);
            PreparedStatement statement = compiledQuery.getPreparedStatement();
            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                Number[] sqlArray = (Number[]) resultSet.getArray(1).getArray();
                connection.commit();
                callback.execute(sqlArray);
            } else {
                connection.commit();
                throw new TaskExecutionException("No percentile found in " + collection + " for provided field");
            }
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception re) {
                // ignoring rollback failure
            }
            try {
                throw new TaskExecutionException("Select in " + collection + " failed: criteria = " +
                        (criteria != null ? criteria.serialize() : "null") + ", percentileCriteria = " +
                        (percentileCriteria != null ? percentileCriteria.serialize() : "null"), e);
            } catch (SerializeException se) {
                throw new TaskExecutionException("Select in " + collection + " failed", e);
            }
        }
    }
}
