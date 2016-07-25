package info.smart_tools.smartactors.core.db_task.insert.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.exceptions.QueryExecutionException;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.insert.psql.wrapper.InsertMessage;
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
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Task for insert row to collection.
 */
public class DBInsertTask implements IDatabaseTask {

    private CompiledQuery compiledQuery;
    private String collectionName;
    private StorageConnection connection;
    private IFieldName idFieldName;

    public DBInsertTask() {

    }

    @Override
    public void prepare(final IObject insertQuery) throws TaskPrepareException {
        try {
            InsertMessage message = IOC.resolve(Keys.getOrAdd(InsertMessage.class.getName()), insertQuery);
            this.collectionName = message.getCollectionName();
            this.idFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), collectionName + "Id");

            String id = IOC.resolve(Keys.getOrAdd(String.class.getCanonicalName()), insertQuery.getValue(idFieldName));

            if (id == null) {
                QueryStatementFactory factory = getQueryStatementFactory();
                this.compiledQuery = IOC.resolve(Keys.getOrAdd(CompiledQuery.class.getCanonicalName()), connection,
                                                                    DBInsertTask.class.toString(), factory);

                List<SQLQueryParameterSetter> parameterSetters = new ArrayList<>();
                parameterSetters.add((statement, index) -> {
                    try {
                        statement.setString(index++ , message.toString());
                    } catch (NullPointerException | NumberFormatException e) {
                        throw new QueryBuildException("Error while write insert query statement: ", e);
                    }
                    return index;
                });
                compiledQuery.setParameters(parameterSetters);
            } else {
                throw new TaskPrepareException("This object already exists/was in a collection");
            }
        } catch (ChangeValueException | ReadValueException | ResolutionException | StorageException e) {
            throw new TaskPrepareException("Can't prepare insert query");
        } catch (SQLException e) {
            //TODO::
        } catch (InvalidArgumentException e) {  //TODO added by AKutalev, reason: now IObject can throw InvalidArgumentException
            throw new TaskPrepareException("Invalid argument exception", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        this.connection = connection;
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            try {
                ResultSet resultSet;
                try {
                    resultSet = ((JDBCCompiledQuery) compiledQuery).getPreparedStatement().executeQuery();
                } catch (SQLTimeoutException e) {
                    throw new Exception("Timeout DB access", e);
                } catch (SQLException e) {
                    throw new Exception("DataBase access error ", e);
                }

                if (resultSet == null || !resultSet.first()) {
                    throw new QueryExecutionException("Database returned not enough generated ids");
                }
            } catch (Exception e) {
                throw new StorageException("Collection creation query execution failed because of SQL exception.", e);
            }
        } catch (Exception e) {
            throw new TaskExecutionException("Query execution has been failed", e);
        }
    }

    private QueryStatementFactory getQueryStatementFactory() {
        QueryStatementFactory factory = () -> {
            QueryStatement updateQueryStatement = new QueryStatement();
            Writer writer = updateQueryStatement.getBodyWriter();
            try {
                writer.write(String.format("INSERT %s AS tab SET %s = docs.document FROM (VALUES",
                        CollectionName.fromString(CollectionName.fromString(collectionName).toString()),
                        Schema.DOCUMENT_COLUMN_NAME));
                writer.write("(?::jsonb)");
                writer.write(String.format(" RETURNING %s AS id;", Schema.ID_COLUMN_NAME));
            } catch (IOException | QueryBuildException e) {
                throw new QueryStatementFactoryException("Error while initialize update query.", e);
            }
            return updateQueryStatement;
        };
        return factory;
    }
}
