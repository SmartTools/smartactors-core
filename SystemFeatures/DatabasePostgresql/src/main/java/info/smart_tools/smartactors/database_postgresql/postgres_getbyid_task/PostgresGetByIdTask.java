package info.smart_tools.smartactors.database_postgresql.postgres_getbyid_task;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.PostgresSchema;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The database task which is able to select documents from Postgres database by the document ID.
 */
public class PostgresGetByIdTask implements IDatabaseTask {

    /**
     * Connection to the database.
     */
    private IStorageConnection connection;
    /**
     * Name of the collection.
     */
    private CollectionName collection;
    /**
     * Id of the document to search.
     */
    private Object id;
    /**
     * Callback function to call when the object is found.
     */
    private IAction<IObject> callback;
    /**
     * Query, prepared during prepare(), to be compiled during execute().
     */
    private QueryStatement preparedQuery;

    /**
     * Creates the task
     * @param connection the database connection where to perform upserts
     */
    public PostgresGetByIdTask(final IStorageConnection connection) {
        this.connection = connection;
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            GetByIdMessage message = IOC.resolve(Keys.getKeyByName(GetByIdMessage.class.getCanonicalName()), query);
            collection = message.getCollectionName();
            id = message.getId();
            callback = message.getCallback();

            preparedQuery = new QueryStatement();
            PostgresSchema.getById(preparedQuery, collection);
            preparedQuery.pushParameterSetter((statement, index) -> {
                statement.setObject(index++, id);
                return index;
            });
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
                String sqlDoc = resultSet.getString(1);
                IObject document = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), sqlDoc);
                callback.execute(document);
                connection.commit();
            } else {
                connection.commit();
                throw new TaskExecutionException("Not found in " + collection + ": id = " + id);
            }
        } catch (TaskExecutionException te) {
            throw te;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception re) {
                // ignoring rollback failure
            }
            throw new TaskExecutionException("Select in " + collection + " failed: id = " + id, e);
        }
    }

}
