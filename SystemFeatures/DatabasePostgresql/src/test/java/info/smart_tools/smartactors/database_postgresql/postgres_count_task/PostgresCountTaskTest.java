package info.smart_tools.smartactors.database_postgresql.postgres_count_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.exception.StorageException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for PostgresCountTask.
 */
public class PostgresCountTaskTest extends IOCInitializer {

    private IDatabaseTask task;
    private CountMessage message;
    private IStorageConnection connection;
    private JDBCCompiledQuery compiledQuery;
    private PreparedStatement sqlStatement;
    private ResultSet resultSet;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        resultSet = mock(ResultSet.class);

        sqlStatement = mock(PreparedStatement.class);
        when(sqlStatement.getResultSet()).thenReturn(resultSet);

        compiledQuery = mock(JDBCCompiledQuery.class);
        when(compiledQuery.getPreparedStatement()).thenReturn(sqlStatement);

        connection = mock(IStorageConnection.class);
        when(connection.compileQuery(any())).thenReturn(compiledQuery);

        task = new PostgresCountTask(connection);

        message = mock(CountMessage.class);
        when(message.getCollectionName()).thenReturn(CollectionName.fromString("test"));
        when(message.getCriteria()).thenReturn(new DSObject("{ \"filter\": { } }"));

        IOC.register(
                Keys.getKeyByName(CountMessage.class.getCanonicalName()),
                new SingletonStrategy(message)
        );
    }

    @Test
    public void testCount() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        final Long[] result = new Long[1];
        when(message.getCallback()).thenReturn(count -> result[0] = count);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong(1)).thenReturn(42L);

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(resultSet).next();
        verify(resultSet).getLong(anyInt());
        verify(connection).commit();
        assertEquals(Long.valueOf(42L), result[0]);
    }

    @Test
    public void testCountFailure() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        IAction<Long> callback = mock(IAction.class);
        when(message.getCallback()).thenReturn(callback);
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
        verifyZeroInteractions(resultSet);
        verifyZeroInteractions(callback);
        verify(connection).rollback();
    }

    @Test
    public void testCountNoResultSet() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        IAction<Long> callback = mock(IAction.class);
        when(message.getCallback()).thenReturn(callback);
        when(resultSet.next()).thenReturn(false);
        when(resultSet.getLong(anyInt())).thenThrow(SQLException.class);

        task.prepare(null); // the message will be resolved by IOC
        try {
            task.execute();
            fail();
        } catch (TaskExecutionException e) {
            //pass
        }

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(resultSet).next();
        verify(resultSet, times(0)).getLong(anyInt());
        verify(connection).commit();
        verifyZeroInteractions(callback);
    }

    @Test
    public void testCountInvalidCriteria() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        IAction<Long> callback = mock(IAction.class);
        when(message.getCallback()).thenReturn(callback);
        when(message.getCriteria()).thenReturn(new DSObject("{ \"filter\": 123 }"));

        try {
            task.prepare(null); // the message will be resolved by IOC
            fail();
        } catch (TaskPrepareException e) {
            // pass
        }

        verifyZeroInteractions(connection);
        verifyZeroInteractions(sqlStatement);
        verifyZeroInteractions(callback);
    }

}
