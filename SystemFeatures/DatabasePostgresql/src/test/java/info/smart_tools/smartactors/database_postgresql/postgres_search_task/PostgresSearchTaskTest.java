package info.smart_tools.smartactors.database_postgresql.postgres_search_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
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
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for PostgresSearchTask.
 */
public class PostgresSearchTaskTest extends IOCInitializer {

    private IDatabaseTask task;
    private SearchMessage message;
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

        task = new PostgresSearchTask(connection);

        message = mock(SearchMessage.class);
        when(message.getCollectionName()).thenReturn(CollectionName.fromString("test"));
        when(message.getCriteria()).thenReturn(new DSObject("{ \"filter\": { } }"));

        IOC.register(
                Keys.getKeyByName(SearchMessage.class.getCanonicalName()),
                new SingletonStrategy(message)
        );
    }

    @Test
    public void testSearch() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException, ActionExecutionException {
        IAction<IObject[]> callback = mock(IAction.class);
        when(message.getCallback()).thenReturn(callback);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("{ \"testID\": 123, \"test\": \"value\" }");

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(resultSet, times(3)).next();
        verify(resultSet, times(2)).getString(anyInt());
        verify(connection).commit();

        ArgumentCaptor<IObject[]> results = ArgumentCaptor.forClass(IObject[].class);
        verify(callback).execute(results.capture());
        assertEquals(2, results.getValue().length);
    }

    @Test
    public void testSearchFailure() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        IAction<IObject[]> callback = mock(IAction.class);
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
    public void testSearchNotFound() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException, ActionExecutionException {
        IAction<IObject[]> callback = mock(IAction.class);
        when(message.getCallback()).thenReturn(callback);
        when(resultSet.next()).thenReturn(false);
        when(resultSet.getString(anyInt())).thenThrow(SQLException.class);

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(resultSet, times(1)).next();
        verify(resultSet, times(0)).getString(anyInt());
        verify(connection).commit();

        ArgumentCaptor<IObject[]> results = ArgumentCaptor.forClass(IObject[].class);
        verify(callback).execute(results.capture());
        assertEquals(0, results.getValue().length);
    }

    @Test
    public void testSearchInvalidCriteria() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        IAction<IObject[]> callback = mock(IAction.class);
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
    }

}
