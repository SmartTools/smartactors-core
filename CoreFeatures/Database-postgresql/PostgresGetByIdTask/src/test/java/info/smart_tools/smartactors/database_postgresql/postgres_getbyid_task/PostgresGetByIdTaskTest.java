package info.smart_tools.smartactors.database_postgresql.postgres_getbyid_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.exception.StorageException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for PostgresGetByIdTask.
 */
public class PostgresGetByIdTaskTest {

    private IDatabaseTask task;
    private GetByIdMessage message;
    private IFieldName idFieldName;
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
    public void setUp() throws QueryBuildException, InvalidArgumentException, ResolutionException, RegistrationException, ReadValueException, StorageException, SQLException {
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

        task = new PostgresGetByIdTask(connection);

        message = mock(GetByIdMessage.class);
        when(message.getCollectionName()).thenReturn(CollectionName.fromString("test"));

        idFieldName = new FieldName("testID");

        IOC.register(
                Keys.getKeyByName(GetByIdMessage.class.getCanonicalName()),
                new SingletonStrategy(message)
        );
    }

    @Test
    public void testGetById() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        when(message.getId()).thenReturn("123");
        final IObject[] result = new IObject[1];
        when(message.getCallback()).thenReturn(doc -> result[0] = doc);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("{ \"testID\": \"123\", \"test\": \"value\" }");

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
         verify(sqlStatement).setObject(eq(1), eq("123"));
        verify(sqlStatement).execute();
        verify(resultSet).next();
        verify(connection).commit();
        assertEquals("123", result[0].getValue(idFieldName));
        assertEquals("value", result[0].getValue(new FieldName("test")));
    }

    @Test
    public void testGetByIdFailure() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        when(message.getId()).thenReturn("123");
        IAction<IObject> callback = mock(IAction.class);
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
        verify(sqlStatement).setObject(eq(1), eq("123"));
        verify(sqlStatement).execute();
        verifyZeroInteractions(resultSet);
        verifyZeroInteractions(callback);
        verify(connection).rollback();
    }

    @Test
    public void testGetByIdNotFound() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException {
        when(message.getId()).thenReturn("123");
        IAction<IObject> callback = mock(IAction.class);
        when(message.getCallback()).thenReturn(callback);
        when(resultSet.next()).thenReturn(false);
        when(resultSet.getString(anyInt())).thenThrow(SQLException.class);

        task.prepare(null); // the message will be resolved by IOC
        try {
            task.execute();
            fail();
        } catch (TaskExecutionException e) {
            // pass
        }

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).setObject(eq(1), eq("123"));
        verify(sqlStatement).execute();
        verify(resultSet).next();
        verify(connection).commit();
        verifyZeroInteractions(callback);
    }

}
