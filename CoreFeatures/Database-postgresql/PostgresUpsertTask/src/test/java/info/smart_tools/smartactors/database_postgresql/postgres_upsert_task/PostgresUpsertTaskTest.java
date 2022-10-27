package info.smart_tools.smartactors.database_postgresql.postgres_upsert_task;

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
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import info.smart_tools.smartactors.ioc_strategy_pack.uuid_nextid_strategy.UuidNextIdStrategy;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for PostgresUpsertTask.
 */
public class PostgresUpsertTaskTest {

    private IDatabaseTask task;
    private UpsertMessage message;
    private IObject document;
    private IFieldName idFieldName;
    private IStorageConnection connection;
    private QueryStatement preparedQuery;
    private JDBCCompiledQuery compiledQuery;
    private Connection sqlConnection;
    private PreparedStatement sqlStatement;

    @BeforeClass
    public static void prepareIOC() throws PluginException, ProcessExecutionException, ResolutionException, RegistrationException {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
        bootstrap.start();

        IOC.register(
                Keys.getKeyByName("db.collection.nextid"),
                new UuidNextIdStrategy()
        );
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

        task = new PostgresUpsertTask(connection);

        document = mock(IObject.class);
        message = mock(UpsertMessage.class);
        when(message.getCollectionName()).thenReturn(CollectionName.fromString("test"));
        when(message.getDocument()).thenReturn(document);

        idFieldName = new FieldName("testID");

        IOC.register(
                Keys.getKeyByName(UpsertMessage.class.getCanonicalName()),
                new SingletonStrategy(message)
        );
    }

    @Test
    public void testInsert() throws InvalidArgumentException, ReadValueException, TaskPrepareException, TaskSetConnectionException, TaskExecutionException, ChangeValueException, StorageException, SQLException, SerializeException {
        FieldName testFieldName = new FieldName("testField");
        when(document.getValue(testFieldName)).thenReturn("testValue");

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).setString(eq(1), anyString());
        verify(sqlStatement).execute();
        verify(connection).commit();
        verify(document).setValue(eq(idFieldName), anyString());

        InOrder inOrder = inOrder(document, sqlStatement);
        inOrder.verify(document).setValue(eq(idFieldName), anyString());
        inOrder.verify(document).serialize();
        inOrder.verify(sqlStatement).setString(eq(1), anyString());
    }

    @Test
    public void testInsertFailure() throws InvalidArgumentException, ReadValueException, SQLException, TaskPrepareException, TaskExecutionException, StorageException, ChangeValueException, DeleteValueException {
        FieldName testFieldName = new FieldName("testField");
        when(document.getValue(testFieldName)).thenReturn("testValue");
        when(sqlStatement.execute()).thenThrow(SQLException.class);

        task.prepare(null); // the message will be resolved by IOC
        try {
            task.execute();
            fail();
        } catch (TaskExecutionException e) {
            // pass
        }

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).setString(eq(1), any(String.class));
        verify(sqlStatement).execute();
        verify(document).setValue(eq(idFieldName), anyString());
        verify(connection).rollback();
        verify(document).deleteField(eq(idFieldName));
    }

    @Test
    public void testUpdate() throws InvalidArgumentException, ReadValueException, TaskSetConnectionException, TaskPrepareException, TaskExecutionException, StorageException, SQLException, ChangeValueException {
        FieldName testFieldName = new FieldName("testField");
        when(document.getValue(testFieldName)).thenReturn("testValue");
        when(document.getValue(idFieldName)).thenReturn(123L);

        task.prepare(null); // the message will be resolved by IOC
        task.execute();

        verify(connection).compileQuery(any(QueryStatement.class));
        verify(sqlStatement).setString(eq(1), anyString());
        verify(sqlStatement).setObject(eq(2), anyString());
        verify(sqlStatement).execute();
        verify(connection).commit();
        verify(document, never()).setValue(any(), any());
    }

}
