package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_plugin;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task.CreateIfNotExistsCollectionMessage;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task.PostgresCreateIfNotExistsTask;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CreateCollectionPluginTest {

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
        new CreateCollectionPlugin(bootstrap).load();
        bootstrap.start();

        message = mock(IObject.class);
        connection = mock(IStorageConnection.class);
        collection = CollectionName.fromString("test");
    }

    @Test
    public void testCreateTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getKeyByName(CreateIfNotExistsCollectionMessage.class.getCanonicalName()), message)
                instanceof CreateIfNotExistsCollectionMessage);
        IObject options = mock(IObject.class);
        assertTrue(IOC.resolve(Keys.getKeyByName("db.collection.create-if-not-exists"), connection, collection, options)
                instanceof PostgresCreateIfNotExistsTask);
    }

    @Test
    public void testCreateTaskInitializedWithoutOptions() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getKeyByName("db.collection.create-if-not-exists"), connection, collection)
                instanceof PostgresCreateIfNotExistsTask);
    }
}
