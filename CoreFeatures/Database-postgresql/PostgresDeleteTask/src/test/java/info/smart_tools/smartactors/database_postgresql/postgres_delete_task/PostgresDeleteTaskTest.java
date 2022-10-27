package info.smart_tools.smartactors.database_postgresql.postgres_delete_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
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
import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
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
import java.sql.SQLException;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for PostgresDeleteTask.
 */
public class PostgresDeleteTaskTest {

    private IDatabaseTask task;
    private DeleteMessage message;
    private IFieldName idFieldName;
    private IStorageConnection connection;
    private QueryStatement preparedQuery;
    private JDBCCompiledQuery compiledQuery;
    private Connection sqlConnection;
    private PreparedStatement sqlStatement;

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
        sqlStatement = mock(PreparedStatement.class);

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

        task = new PostgresDeleteTask(connection);

        message = mock(DeleteMessage.class);
        when(message.getCollectionName()).thenReturn(CollectionName.fromString("test"));

        idFieldName = new FieldName("testID");

        IOC.register(
                Keys.getKeyByName(DeleteMessage.class.getCanonicalName()),
                new SingletonStrategy(message)
        );
    }

    @Test
    public void testDelete() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException, DeleteValueException {
        IObject document = mock(IObject.class);
        when(document.getValue(idFieldName)).thenReturn("123");
        when(message.getDocument()).thenReturn(document);

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).setObject(eq(1), eq("123"));
        verify(sqlStatement).execute();
        verify(connection).commit();
        verify(document).deleteField(idFieldName);
    }

    @Test
    public void testDeleteFailure() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException, DeleteValueException {
        IObject document = mock(IObject.class);
        when(document.getValue(idFieldName)).thenReturn("123");
        when(message.getDocument()).thenReturn(document);

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
        verify(connection).rollback();
        verify(document, times(0)).deleteField(any());
    }

    @Test
    public void testDeleteNoId() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException, DeleteValueException {
        IObject document = mock(IObject.class);
        when(message.getDocument()).thenReturn(document);

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verifyZeroInteractions(connection);
        verifyZeroInteractions(sqlStatement);
        verify(document).deleteField(idFieldName);
    }

}
