package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.exception.StorageException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyZeroInteractions;

public class PostgresCreateIfNotExistTaskTest extends IOCInitializer {
    private IDatabaseTask task;
    private IStorageConnection connection;
    private CreateIfNotExistsCollectionMessage message;
    private JDBCCompiledQuery compiledQuery;
    private PreparedStatement sqlStatement;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        sqlStatement = mock(PreparedStatement.class);

        compiledQuery = mock(JDBCCompiledQuery.class);
        when(compiledQuery.getPreparedStatement()).thenReturn(sqlStatement);

        connection = mock(IStorageConnection.class);
        when(connection.compileQuery(any())).thenReturn(compiledQuery);

        task = new PostgresCreateIfNotExistsTask(connection);

        message = mock(CreateIfNotExistsCollectionMessage.class);
        when(message.getCollectionName()).thenReturn(CollectionName.fromString("test"));

        IOC.register(
                Keys.getKeyByName(CreateIfNotExistsCollectionMessage.class.getCanonicalName()),
                new SingletonStrategy(message)
        );
    }

    @Test
    public void testCreate() throws TaskPrepareException, TaskExecutionException, StorageException, SQLException, InvalidArgumentException, ReadValueException {
        when(message.getOptions()).thenReturn(new DSObject("{}"));

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(connection).commit();
    }

    @Test
    public void testCreateFailure() throws SQLException, TaskPrepareException, StorageException {
        when(sqlStatement.execute()).thenThrow(SQLException.class);

        task.prepare(null); // the message will be resolved by IOC
        try {
            task.execute();
            fail();
        } catch (TaskExecutionException e) {
            // pass
        }

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(connection).rollback();
    }

    @Test
    public void testCreateWithNullOptions() throws TaskPrepareException, StorageException, ReadValueException, TaskExecutionException, SQLException {
        when(message.getOptions()).thenReturn(null);

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(connection).commit();
    }

    @Test
    public void testCreateWithInvalidOptions() throws SQLException, TaskPrepareException, StorageException, InvalidArgumentException, ReadValueException {
        when(message.getOptions()).thenReturn(new DSObject("{ \"ordered\": 123 }"));

        try {
            task.prepare(null); // the message will be resolved by IOC
            fail();
        } catch (TaskPrepareException e) {
            // pass
        }

        verifyZeroInteractions(connection);
        verifyZeroInteractions(sqlStatement);
    }
}
