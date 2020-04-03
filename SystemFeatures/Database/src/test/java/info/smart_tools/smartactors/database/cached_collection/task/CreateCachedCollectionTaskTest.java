package info.smart_tools.smartactors.database.cached_collection.task;

import info.smart_tools.smartactors.database.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.database.cached_collection.wrapper.CreateCachedCollectionQuery;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class CreateCachedCollectionTaskTest {

    private IDatabaseTask task;
    private CreateCachedCollectionTask testTask;

    private IField keyNameField;
    private IField indexesField;

    private Key keyForGetKeyByName;

    private static final String ORDERED_INDEX = "ordered";
    private static final String DATE_TIME_INDEX = "datetime";

    @Before
    public void prepareTaskAndOthers() throws ResolutionException, CreateCachedCollectionTaskException {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        keyNameField = PowerMockito.mock(IField.class);
        indexesField = PowerMockito.mock(IField.class);
        IKey keyField = PowerMockito.mock(IKey.class);
        when(Keys.getKeyByName(IField.class.getCanonicalName())).thenReturn(keyField);
        when(IOC.resolve(keyField, "keyName")).thenReturn(keyNameField);
        when(IOC.resolve(keyField, "indexes")).thenReturn(indexesField);

        task = mock(IDatabaseTask.class);

        testTask = new CreateCachedCollectionTask(task);

        keyForGetKeyByName = mock(Key.class);
        when(Keys.getKeyByName(CreateCachedCollectionQuery.class.toString())).thenReturn(keyForGetKeyByName);
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

    @Test(expected = TaskExecutionException.class)
    public void MustInCorrectCallExecuteTargetTaskWhenTargetTaskThrowException() throws TaskExecutionException {
        doThrow(TaskExecutionException.class).when(task).execute();

        testTask.execute();
    }
}