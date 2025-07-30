package info.smart_tools.smartactors.database.cached_collection.task;

import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

public class GetItemFromCachedCollectionTaskTest extends IOCInitializer {

    private GetItemFromCachedCollectionTask testTask;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
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