package info.smart_tools.smartactors.database_postgresql.postgres_percentile_search_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.exception.StorageException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PostgresPercentileSearchTaskTest {

    private IDatabaseTask task;
    private PercentileSearchMessage message;
    private IStorageConnection connection;
    private QueryStatement preparedQuery;
    private JDBCCompiledQuery compiledQuery;
    private Connection sqlConnection;
    private PreparedStatement sqlStatement;
    private ResultSet resultSet;

    @BeforeClass
    public static void prepareIOC() throws PluginException, ProcessExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
        bootstrap.start();
    }

    @Before
    public void setUp() throws Exception {
        resultSet = mock(ResultSet.class);
        sqlStatement = mock(PreparedStatement.class);
        when(sqlStatement.getResultSet()).thenReturn(resultSet);

        compiledQuery = mock(JDBCCompiledQuery.class);
        when(compiledQuery.getPreparedStatement()).thenReturn(sqlStatement);

        sqlConnection = mock(Connection.class);
        when(sqlConnection.prepareStatement(any())).thenReturn(sqlStatement);

        connection = mock(IStorageConnection.class);
        doAnswer(invocation -> {
            preparedQuery = (QueryStatement) invocation.getArguments()[0];
            preparedQuery.compile(sqlConnection);
            return compiledQuery;
        }).when(connection).compileQuery(any());

        task = new PostgresPercentileSearchTask(connection);

        message = mock(PercentileSearchMessage.class);
        when(message.getCollectionName()).thenReturn(CollectionName.fromString("test"));

        IOC.register(
                Keys.getKeyByName(PercentileSearchMessage.class.getCanonicalName()),
                new SingletonStrategy(message)
        );
    }

    @Test
    public void testGetPercentiles() throws TaskPrepareException, TaskExecutionException, StorageException, SQLException, ReadValueException, InvalidArgumentException {
        final Number[] result = new Number[1];
        when(message.getPercentileCriteria()).thenReturn(new DSObject("{\"field\":\"a\",\"values\":[0.5]}"));
        when(message.getCallback()).thenReturn(res -> result[0] = res[0]);
        when(resultSet.next()).thenReturn(true);

        Array array = mock(Array.class);
        when(array.getArray()).thenReturn(new Number[] { 25 });
        when(resultSet.getArray(1)).thenReturn(array);

        task.prepare(null);
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(resultSet).next();
        verify(connection).commit();

        assertEquals(25, result[0]);
    }

    @Test
    public void testGetPercentilesFailure() throws ReadValueException, SQLException, TaskPrepareException, StorageException, InvalidArgumentException {
        when(message.getPercentileCriteria()).thenReturn(new DSObject("{\"field\":\"a\",\"values\":[0.5]}"));
        IAction<Number[]> action = mock(IAction.class);
        when(message.getCallback()).thenReturn(action);
        when(sqlStatement.execute()).thenThrow(new SQLException());

        Array array = mock(Array.class);
        when(array.getArray()).thenReturn(new Number[] { 25 });
        when(resultSet.getArray(1)).thenReturn(array);

        task.prepare(null);
        try {
            task.execute();
            fail();
        } catch (TaskExecutionException e) {
            // pass
        }

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verifyZeroInteractions(resultSet);
        verifyZeroInteractions(action);
        verify(connection).rollback();
    }

    @Test
    public void testGetPercentilesNotFound() throws ReadValueException, SQLException, TaskPrepareException, InvalidArgumentException, StorageException {
        when(message.getPercentileCriteria()).thenReturn(new DSObject("{\"field\":\"a\",\"values\":[0.5]}"));
        IAction<Number[]> action = mock(IAction.class);
        when(message.getCallback()).thenReturn(action);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getArray(any())).thenThrow(new SQLException());

        task.prepare(null);
        try {
            task.execute();
            fail();
        } catch (TaskExecutionException e) {
            // pass
        }

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).execute();
        verify(resultSet).next();
        verify(connection).rollback();
        verifyZeroInteractions(action);
    }
}
