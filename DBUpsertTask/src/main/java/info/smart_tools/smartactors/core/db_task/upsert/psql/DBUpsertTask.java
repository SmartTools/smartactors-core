package info.smart_tools.smartactors.core.db_task.upsert.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.upsert.psql.wrapper.UpsertMessage;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.JDBCCompiledQuery;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import info.smart_tools.smartactors.core.sql_commons.QueryStatementFactory;
import info.smart_tools.smartactors.core.sql_commons.exception.QueryStatementFactoryException;
import info.smart_tools.smartactors.core.sql_commons.psql.Schema;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task for upsert row to collection:
 * Executes update operation if incoming query contains id
 * Executes insert operation otherwise
 */
public class DBUpsertTask implements IDatabaseTask {

    private static final String INSERT_MODE = "insert";
    private static final String UPDATE_MODE = "update";

    private String collectionName;
    private IDatabaseTask dbInsertTask;
    private IObject rawUpsertQuery;
    private JDBCCompiledQuery compiledQuery;
    private StorageConnection connection;
    private Map<String, UpsertExecution> executionMap;
    private String mode;
    private IFieldName idFieldName;

    private interface UpsertExecution {
        void upsert() throws TaskExecutionException;
    }

    public DBUpsertTask() {

        executionMap = new HashMap<>();
        try {
            this.dbInsertTask = IOC.resolve(Keys.getOrAdd(DBInsertTask.class.toString()));
        } catch (ResolutionException e) {
            //TODO:: throw smth like TaskCreateException("Error while resolving insert task.", e); when standard exception would be added
        }
        executionMap.put(UPDATE_MODE, () -> {
            try {
                int nUpdated = compiledQuery.getPreparedStatement().executeUpdate();
                if (nUpdated == 0) {
                    throw new TaskExecutionException("Update query failed: wrong count of documents is updated.");
                }
            } catch (SQLException e) {
                throw new TaskExecutionException("Transaction execution has been failed.", e);
            }
        });
        executionMap.put(INSERT_MODE, () -> {
            try {
                ResultSet resultSet = compiledQuery.getPreparedStatement().executeQuery();
                if (resultSet == null || !resultSet.first()) {
                    throw new TaskExecutionException("Database returned not enough of generated ids.");
                }
                try {
                    //TODO:: replace by field.inject()
                    rawUpsertQuery.setValue(idFieldName, resultSet.getLong("id"));
                } catch (ChangeValueException e) {
                    throw new TaskExecutionException("Could not set new id on inserted document.");
                } catch (Throwable e) { //TODO added by AKutalev, reason: now IObject can throw InvalidArgumentException
                    throw new TaskExecutionException("Invalid argument exception", e);
                }
            } catch (SQLException e) {
                throw new TaskExecutionException("Insertion query execution failed because of SQL exception.", e);
            }
        });
    }

    @Override
    public void prepare(final IObject upsertObject) throws TaskPrepareException {

        try {
            UpsertMessage upsertMessage = IOC.resolve(Keys.getOrAdd(UpsertMessage.class.toString()), upsertObject);
            this.collectionName = upsertMessage.getCollectionName();
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Error while resolving upsert message.", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Error while get collection name.", e);
        }
        try {
            this.idFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), collectionName + "Id");
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create idFieldName.", e);
        }

        this.rawUpsertQuery = upsertObject;
        try {
            String id = IOC.resolve(Keys.getOrAdd(String.class.toString()), upsertObject.getValue(idFieldName));
            if (id != null) {
                this.mode = UPDATE_MODE;
                QueryStatementFactory factory = () -> {
                    QueryStatement updateQueryStatement = new QueryStatement();
                    Writer writer = updateQueryStatement.getBodyWriter();
                    try {
                        writer.write(String.format(
                                "UPDATE %s AS tab SET %s = docs.document FROM (VALUES",
                                CollectionName.fromString(collectionName).toString(),
                                Schema.DOCUMENT_COLUMN_NAME
                        ));
                        writer.write("(?,?::jsonb)");
                        writer.write(String.format(
                                ") AS docs (id, document) WHERE tab.%s = docs.id;",
                                Schema.ID_COLUMN_NAME
                        ));
                    } catch (IOException | QueryBuildException e) {
                        throw new QueryStatementFactoryException("Error while initialize update query.", e);
                    }
                    return updateQueryStatement;
                };
                this.compiledQuery = IOC.resolve(
                        Keys.getOrAdd(CompiledQuery.class.toString()),
                        connection,
                        DBUpsertTask.class.toString().concat("update"),
                        factory
                );
                List<SQLQueryParameterSetter> parameterSetters = new ArrayList<>();
                parameterSetters.add((statement, index) -> {
                    try {
                        statement.setLong(index++, Long.parseLong(id));
                        statement.setString(index++, upsertObject.toString());
                    } catch (NullPointerException | NumberFormatException e) {
                        throw new QueryBuildException("Error while writing update query statement: ", e);
                    }
                    return index;
                });
                this.compiledQuery.setParameters(parameterSetters);
            } else {
                this.mode = INSERT_MODE;
                dbInsertTask.setConnection(connection);

                //TODO:: move to DBInsertTask prepare() or to the separate class
                QueryStatementFactory factory = () -> {
                    QueryStatement insertQueryStatement = new QueryStatement();
                    Writer writer = insertQueryStatement.getBodyWriter();
                    try {
                        writer.write(String.format(
                                "INSERT INTO %s (%s) VALUES",
                                CollectionName.fromString(collectionName).toString(),
                                Schema.DOCUMENT_COLUMN_NAME
                        ));
                        writer.write("(?::jsonb)");
                        writer.write(String.format(" RETURNING %s AS id;", Schema.ID_COLUMN_NAME));
                    } catch (IOException | QueryBuildException e) {
                        throw new QueryStatementFactoryException("Error while initialize insert query.", e);
                    }
                    return insertQueryStatement;
                };
                this.compiledQuery = IOC.resolve(
                        Keys.getOrAdd(CompiledQuery.class.toString()),
                        connection,
                        DBUpsertTask.class.toString().concat("insert"),
                        factory
                );
                List<SQLQueryParameterSetter> parameterSetters = new ArrayList<>();
                parameterSetters.add((statement, index) -> {
                    try {
                        statement.setString(index++, upsertObject.toString());
                    } catch (NullPointerException e) {
                        throw new QueryBuildException("Error while writing update query statement: ", e);
                    }
                    return index;
                });
                this.compiledQuery.setParameters(parameterSetters);
            }
        } catch (
                ReadValueException |
                StorageException |
                ResolutionException |
                TaskSetConnectionException |
                SQLException e
        ) {
            throw new TaskPrepareException("Error while writing update query statement.", e);
        } catch (InvalidArgumentException e) { //TODO added by AKutalev, reason: now IObject can throw InvalidArgumentException
            throw new TaskPrepareException("Invalid argument exception", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        this.connection = connection;
    }

    @Override
    public void execute() throws TaskExecutionException {

        executionMap.get(mode).upsert();
    }
}
