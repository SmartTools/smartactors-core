package info.smart_tools.smartactors.database_postgresql.postgres_upsert_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.PostgresSchema;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The database task which is able to upsert documents into Postgres database.
 */
public class PostgresUpsertTask implements IDatabaseTask {

    /**
     * Connection to the database.
     */
    private IStorageConnection connection;
    /**
     * Collection where the document should be upserted.
     */
    private CollectionName collection;
    /**
     * Document to be upserted.
     */
    private IObject document;
    /**
     * Name of the ID field in the document.
     */
    private IFieldName idField;
    /**
     * Name of the {@code createdAt} field in the document.
     */
    private IFieldName createdAtField;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    /**
     * Query, prepared during prepare(), to be compiled during execute().
     */
    private QueryStatement preparedQuery;

    /**
     * Interfaces to a method to be called during execution phase.
     */
    private interface Executor {
        void execute() throws TaskExecutionException;
    }
    /**
     * Method of execution.
     */
    private Executor executeMethod;

    /**
     * Creates the task
     * @param connection the database connection where to perform upserts
     */
    public PostgresUpsertTask(final IStorageConnection connection) throws TaskPrepareException {
        this.connection = connection;

        try {
            this.createdAtField = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "createdAt");
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Unable to resolve fields for database task", e);
        }
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            UpsertMessage message = IOC.resolve(Keys.getKeyByName(UpsertMessage.class.getCanonicalName()), query);
            collection = message.getCollectionName();
            idField = IOC.resolve(
                    Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                    String.format(PostgresSchema.ID_FIELD_PATTERN, collection.toString()));
            document = message.getDocument();

            preparedQuery = new QueryStatement();

            try {
                Object id = document.getValue(idField);
                if (null == id) {
                    prepareInsert();
                } else {
                    prepareUpdate(id);
                }
            } catch (ReadValueException e) {
                prepareInsert();
            }
        } catch (Exception e) {
            throw new TaskPrepareException(e);
        }
    }

    /**
     * Prepares the insert query.
     */
    private void prepareInsert() throws QueryBuildException {
        executeMethod = this::executeInsert;
        PostgresSchema.insert(preparedQuery, collection);

        preparedQuery.pushParameterSetter((statement, index) -> {
            try {
                String sqlDoc = document.serialize();
                statement.setString(index++, sqlDoc);
            } catch (SerializeException e) {
                throw new SQLException("Cannot serialize document", e);
            }
            return index;
        });
    }

    /**
     * Retrieves the next ID for the inserting document.
     * The ID is resolved from IOC using "db.collection.nextid" key.
     * @return the new ID for the document
     */
    private Object nextId() throws ResolutionException {
        return IOC.resolve(Keys.getKeyByName("db.collection.nextid"));
    }

    /**
     * Prepares the update query.
     * @param id id of the document to be updated
     */
    private void prepareUpdate(final Object id) throws QueryBuildException {
        executeMethod = this::executeUpdate;
        PostgresSchema.update(preparedQuery, collection);

        preparedQuery.pushParameterSetter((statement, index) -> {
            try {
                String sqlDoc = document.serialize();
                statement.setString(index++, sqlDoc);
                statement.setObject(index++, id);
            } catch (SerializeException e) {
                throw new SQLException("Cannot serialize document", e);
            }
            return index;
        });
    }

    @Override
    public void execute() throws TaskExecutionException {
        executeMethod.execute();
    }

    private void executeInsert() throws TaskExecutionException {
        try {
            Object id = nextId();
            document.setValue(idField, id);
            insertTimestampIntoDocForInsert();

            JDBCCompiledQuery compiledQuery = (JDBCCompiledQuery) connection.compileQuery(preparedQuery);
            PreparedStatement statement = compiledQuery.getPreparedStatement();
            statement.execute();
            connection.commit();
        } catch (Exception e) {
            try {
                document.deleteField(idField);
                document.deleteField(createdAtField);
                connection.rollback();
            } catch (Exception re) {
                // ignoring rollback failure
            }
            throw new TaskExecutionException("Insert to " + collection + " failed", e);
        }
    }

    private void insertTimestampIntoDocForInsert() throws ChangeValueException, InvalidArgumentException {
        String nowTimeInUTC = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).format(this.formatter);
        this.document.setValue(this.createdAtField, nowTimeInUTC);
    }

    private void executeUpdate() throws TaskExecutionException {
        try {
            JDBCCompiledQuery compiledQuery = (JDBCCompiledQuery) connection.compileQuery(preparedQuery);
            PreparedStatement statement = compiledQuery.getPreparedStatement();
            statement.execute();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception re) {
                // ignoring rollback failure
            }
            throw new TaskExecutionException("Update to " + collection + " failed", e);
        }
    }

}
