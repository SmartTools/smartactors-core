package info.smart_tools.smartactors.database.cached_collection.task;

import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

//import info.smart_tools.smartactors.plugin.datetime_formatter_strategy.PluginDateTimeFormatter;
//import info.smart_tools.smartactors.plugin.postgres_db_tasks.PostgresDBTasksPlugin;

public class GetItemFromCachedCollectionTaskTest {

    private GetItemFromCachedCollectionTask testTask;

    @BeforeClass
    public static void prepareIOC() throws PluginException, ProcessExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new IFieldPlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
//        new PostgresDBTasksPlugin(bootstrap).load();
//        new PluginDateTimeFormatter(bootstrap).load();
        bootstrap.start();
    }

    @Before
    public void setUp() throws Exception {

        IStorageConnection connection = mock(IStorageConnection.class);
        testTask = new GetItemFromCachedCollectionTask(connection);
    }

    @Test
    @Ignore
    public void MustCorrectPrepareQueryForSelecting() throws Exception {

        IObject query = spy(new DSObject("{" +
            "\"keyName\": \"name\"," +
            " \"key\": \"keyValue\"," +
            " \"collectionName\": \"collection\"" +
            "}"
        ));

        testTask.prepare(query);

        FieldName keyNameFN = new FieldName("keyName");
        FieldName keyValueFN = new FieldName("key");
        FieldName collectionNameFN = new FieldName("collectionName");

        verify(query).getValue(eq(keyNameFN));
        verify(query).getValue(eq(keyValueFN));
        verify(query).getValue(eq(collectionNameFN));
    }

    @Test(expected = TaskPrepareException.class)
    @Ignore
    public void MustInCorrectPrepareQueryForSelectingWhenIOCThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        testTask.prepare(null);
    }
}