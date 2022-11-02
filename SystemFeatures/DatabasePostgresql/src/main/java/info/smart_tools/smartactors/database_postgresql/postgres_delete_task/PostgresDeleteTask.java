package info.smart_tools.smartactors.database_postgresql.postgres_delete_task;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.PostgresSchema;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.sql.PreparedStatement;

/**
 * The database task which is to delete the document from Postgres database by the document ID.
 */
public class PostgresDeleteTask implements IDatabaseTask {

    /**
     * Connection to the database.
     */
    private IStorageConnection connection;
    /**
     * Name of the collection.
     */
    private CollectionName collection;
    /**
     * Document to delete.
     */
    private IObject document;
    /**
     * Field name to delete from document
     */
    private IFieldName idField;
    /**
     * Id of the document to search.
     */
    private Object id;
    /**
     * Query, prepared during prepare(), to be compiled during execute().
     */
    private QueryStatement preparedQuery;

    /**
     * What to run during execute() stage.
     */
    private interface Executor {
        void execute() throws TaskExecutionException;
    }
    /**
     * Method of execution.
     */
    private Executor executeMethod = this::deleteFromDb;

    /**
     * Creates the task
     * @param connection the database connection where to perform upserts
     */
    public PostgresDeleteTask(final IStorageConnection connection) {
        this.connection = connection;
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            DeleteMessage message = IOC.resolve(Keys.getKeyByName(DeleteMessage.class.getCanonicalName()), query);
            collection = message.getCollectionName();

            document = message.getDocument();
            idField = IOC.resolve(
                    Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                    String.format(PostgresSchema.ID_FIELD_PATTERN, collection.toString()));
            id = document.getValue(idField);

            if (id == null) {
                executeMethod = this::removeIdOnly;
            } else {
                executeMethod = this::deleteFromDb;
                preparedQuery = new QueryStatement();
                PostgresSchema.delete(preparedQuery, collection);
                preparedQuery.pushParameterSetter((statement, index) -> {
                    statement.setObject(index++, id);
                    return index;
                });
            }
        } catch (Exception e) {
            throw new TaskPrepareException(e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        executeMethod.execute();
    }

    private void deleteFromDb() throws TaskExecutionException {
        try {
            JDBCCompiledQuery compiledQuery = (JDBCCompiledQuery) connection.compileQuery(preparedQuery);
            PreparedStatement statement = compiledQuery.getPreparedStatement();
            statement.execute();
            document.deleteField(idField);
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception re) {
                // ignoring rollback failure
            }
            throw new TaskExecutionException("Delete in " + collection + " failed: id = " + id, e);
        }
    }

    private void removeIdOnly() throws TaskExecutionException {
        try {
            document.deleteField(idField);
        } catch (Exception e) {
            throw new TaskExecutionException("Delete in " + collection + " failed: id = " + id, e);
        }
    }

}
