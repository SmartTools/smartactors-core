package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.core.cached_collection.wrapper.CreateCachedCollectionQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class CreateCachedCollectionTaskTest {

    private IDatabaseTask task;
    private CreateCachedCollectionTask testTask;

    private IField keyNameField;
    private IField indexesField;

    private Key keyForGetOrAdd;

    private static final String ORDERED_INDEX = "ordered";
    private static final String DATE_TIME_INDEX = "datetime";

    @Before
    public void prepareTaskAndOthers() throws ResolutionException, CreateCachedCollectionTaskException {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        keyNameField = PowerMockito.mock(IField.class);
        indexesField = PowerMockito.mock(IField.class);
        IKey keyField = PowerMockito.mock(IKey.class);
        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(keyField);
        when(IOC.resolve(keyField, "keyName")).thenReturn(keyNameField);
        when(IOC.resolve(keyField, "indexes")).thenReturn(indexesField);

        task = mock(IDatabaseTask.class);

        testTask = new CreateCachedCollectionTask(task);

        keyForGetOrAdd = mock(Key.class);
        when(Keys.getOrAdd(CreateCachedCollectionQuery.class.getCanonicalName())).thenReturn(keyForGetOrAdd);
    }

    @Test
    public void MustCorrectPrepareQueryForCreateCollection() throws Exception {

        IObject startQuery = mock(IObject.class);
        String key = "key";
        when(keyNameField.in(startQuery)).thenReturn(key);

        Map<String, String> indexes = new HashMap<>();
        indexes.put(key, ORDERED_INDEX);
        indexes.put("isActive", ORDERED_INDEX);
        indexes.put("startDateTime", DATE_TIME_INDEX);

        testTask.prepare(startQuery);

        verify(indexesField).out(startQuery, indexes);
        verify(task).prepare(startQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustIncorrectPrepareQueryForCreateCollectionWhenIOCResolveThrowException() throws Exception {

        IObject startQuery = mock(IObject.class);
        when(keyNameField.in(startQuery)).thenThrow(new ReadValueException());

        testTask.prepare(startQuery);
    }

    @Test
    public void MustCorrectCallExecuteTargetTask() throws TaskExecutionException {
        testTask.execute();

        verify(task).execute();
    }

    @Test
    public void MustCorrectSetConnectionForTargetTask() throws TaskSetConnectionException {
        StorageConnection connection = mock(StorageConnection.class);

        testTask.setConnection(connection);

        verify(task).setConnection(connection);
    }

    @Test(expected = TaskExecutionException.class)
    public void MustInCorrectCallExecuteTargetTaskWhenTargetTaskThrowException() throws TaskExecutionException {
        doThrow(TaskExecutionException.class).when(task).execute();

        testTask.execute();
    }
}