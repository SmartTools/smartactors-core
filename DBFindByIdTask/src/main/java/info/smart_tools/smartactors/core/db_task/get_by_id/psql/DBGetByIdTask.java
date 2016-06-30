package info.smart_tools.smartactors.core.db_task.get_by_id.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.get_by_id.psql.wrapper.SearchByIdQuery;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
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

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database task for search documents by id
 */
public class DBGetByIdTask implements IDatabaseTask {
    private StorageConnection connection;
    private CompiledQuery compiledQuery;
    private SearchByIdQuery message;

    /**
     * Constructor for DBGetByIdTask
     */
    public DBGetByIdTask() {}

    @Override
    public void prepare(final IObject object) throws TaskPrepareException {
        try {
            QueryStatementFactory factory = () -> {
                QueryStatement preparedQuery = new QueryStatement();
                Writer writer = preparedQuery.getBodyWriter();
                try {
                    message = IOC.resolve(Keys.getOrAdd(SearchByIdQuery.class.toString()), object);
                    CollectionName collectionName = CollectionName.fromString(message.getCollectionName());
                    writer.write(String.format("SELECT * FROM %s WHERE ", collectionName.toString()));
                    writer.write(String.format("token = \'%s\'", message.getId()));
                } catch (IOException | ResolutionException | ChangeValueException | ReadValueException e) {
                    throw new QueryStatementFactoryException("Error while initialize update query.", e);
                } catch (QueryBuildException e) {
                    e.printStackTrace();
                }
                return preparedQuery;
            };

            this.compiledQuery = IOC.resolve(Keys.getOrAdd(CompiledQuery.class.toString()), connection, factory);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Error while writing collection creation statement.", e);
        }

    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            ResultSet resultSet = ((JDBCCompiledQuery) compiledQuery).getPreparedStatement().executeQuery();
            if (resultSet != null && resultSet.first()) {
                try {
                    message.setSearchResult((IObject) resultSet.getObject(0));
                } catch (ChangeValueException e) {
                    throw new StorageException("Could not set the document.");
                }
            } else {
                throw new TaskExecutionException("Not found document with this id.");
            }
        } catch (ReadValueException | StorageException | SQLException e) {
            throw new TaskExecutionException("Insertion query execution failed because of SQL exception.", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) {
        this.connection = connection;
    }
}