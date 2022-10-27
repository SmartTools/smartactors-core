package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor_plugin;

import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.CreateCollectionActor;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.exception.CreateCollectionActorException;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_plugin.CreateCollectionPlugin;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
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

public class CreateCollectionActorPluginTest {
    @Before
    public void setUp() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new IFieldPlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
        new TestConnectionOptionsPlugin(bootstrap).load();
        new TestPostgresConnectionPoolPlugin(bootstrap).load();
        new CreateCollectionPlugin(bootstrap).load();
        new CreateCollectionActorPlugin(bootstrap).load();
        bootstrap.start();
    }

    @Test
    public void testCreateTaskInitializedWithoutOptions() throws ResolutionException, CreateCollectionActorException {
        Object actor = IOC.resolve(Keys.getKeyByName("CreateCollectionIfNotExistsActor"));
        assertTrue(actor instanceof CreateCollectionActor);
    }
}
