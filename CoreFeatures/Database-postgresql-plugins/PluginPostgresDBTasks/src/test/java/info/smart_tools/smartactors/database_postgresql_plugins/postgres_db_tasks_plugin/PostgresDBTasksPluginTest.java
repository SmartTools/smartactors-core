package info.smart_tools.smartactors.database_postgresql_plugins.postgres_db_tasks_plugin;

import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.database_postgresql.postgres_count_task.CountMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_count_task.PostgresCountTask;
import info.smart_tools.smartactors.database_postgresql.postgres_create_task.CreateCollectionMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_create_task.PostgresCreateTask;
import info.smart_tools.smartactors.database_postgresql.postgres_delete_task.DeleteMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_delete_task.PostgresDeleteTask;
import info.smart_tools.smartactors.database_postgresql.postgres_getbyid_task.GetByIdMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_getbyid_task.PostgresGetByIdTask;
import info.smart_tools.smartactors.database_postgresql.postgres_insert_task.InsertMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_insert_task.PostgresInsertTask;
import info.smart_tools.smartactors.database_postgresql.postgres_search_task.PostgresSearchTask;
import info.smart_tools.smartactors.database_postgresql.postgres_search_task.SearchMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_upsert_task.PostgresUpsertTask;
import info.smart_tools.smartactors.database_postgresql.postgres_upsert_task.UpsertMessage;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Tests for the plugin
 */
public class PostgresDBTasksPluginTest {

    private IObject message;
    private IStorageConnection connection;
    private CollectionName collection;

    @Before
    public void setUp() throws PluginException, ProcessExecutionException, QueryBuildException {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new IFieldPlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
        new PostgresDBTasksPlugin(bootstrap).load();
        bootstrap.start();

        message = mock(IObject.class);
        connection = mock(IStorageConnection.class);
        collection = CollectionName.fromString("test");
    }

    @Test
    public void testCreateTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getOrAdd(CreateCollectionMessage.class.getCanonicalName()), message)
                instanceof CreateCollectionMessage);
        IObject options = mock(IObject.class);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.create"), connection, collection, options)
                instanceof PostgresCreateTask);
    }

    @Test
    public void testCreateTaskInitializedWithoutOptions() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.create"), connection, collection)
                instanceof PostgresCreateTask);
    }

    @Test
    public void testUpsertTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getOrAdd(UpsertMessage.class.getCanonicalName()), message)
                instanceof UpsertMessage);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.nextid")) instanceof String);
        assertNotEquals(IOC.resolve(Keys.getOrAdd("db.collection.nextid")), IOC.resolve(Keys.getOrAdd("db.collection.nextid")));
        IObject document = mock(IObject.class);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.upsert"), connection, collection, document)
                instanceof PostgresUpsertTask);
    }

    @Test
    public void testGetByIdTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getOrAdd(GetByIdMessage.class.getCanonicalName()), message)
                instanceof GetByIdMessage);
        Object id = new Object();
        IAction callback = mock(IAction.class);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.getbyid"), connection, collection, id, callback)
                instanceof PostgresGetByIdTask);
    }

    @Test
    public void testSearchTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getOrAdd(SearchMessage.class.getCanonicalName()), message)
                instanceof SearchMessage);
        IObject criteria = mock(IObject.class);
        IAction callback = mock(IAction.class);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.search"), connection, collection, criteria, callback)
                instanceof PostgresSearchTask);
    }

    @Test
    public void testDeleteTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getOrAdd(DeleteMessage.class.getCanonicalName()), message)
                instanceof DeleteMessage);
        IObject document = mock(IObject.class);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.delete"), connection, collection, document)
                instanceof PostgresDeleteTask);
    }

    @Test
    public void testInsertTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getOrAdd(InsertMessage.class.getCanonicalName()), message)
                instanceof InsertMessage);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.nextid")) instanceof String);
        IObject document = mock(IObject.class);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.insert"), connection, collection, document)
                instanceof PostgresInsertTask);
    }

    @Test
    public void testCountTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getOrAdd(CountMessage.class.getCanonicalName()), message)
                instanceof CountMessage);
        IObject criteria = mock(IObject.class);
        IAction callback = mock(IAction.class);
        assertTrue(IOC.resolve(Keys.getOrAdd("db.collection.count"), connection, collection, criteria, callback)
                instanceof PostgresCountTask);
    }

}
