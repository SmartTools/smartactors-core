package info.smart_tools.smartactors.core.db_insert_with_key_task;

import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.FieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.DateFormat;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;


@PrepareForTest(IOC.class)
@RunWith(PowerMockRunner.class)
public class DBInsertWIthKeyTaskTest {

    private IDatabaseTask task;

    private DBInsertWIthKeyTask testTask;

    private Key keyForKeyStorage;

    private Key fieldNameKey;


    private FieldName keyFieldName;
    private FieldName idKeyFieldName;
    private FieldName documentKeyFieldName;
    private FieldName startDateTimeFieldName;
    private FieldName isActiveFieldName;

    @Before
    public void prepare() throws ResolutionException {
        mockStatic(IOC.class);
        task = mock(IDatabaseTask.class);

        keyForKeyStorage = mock(Key.class);
        fieldNameKey = mock(Key.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(keyForKeyStorage);
        when(IOC.resolve(keyForKeyStorage, FieldName.class.toString())).thenReturn(fieldNameKey);

        keyFieldName = mock(FieldName.class);
        idKeyFieldName = mock(FieldName.class);
        documentKeyFieldName = mock(FieldName.class);
        startDateTimeFieldName = mock(FieldName.class);
        isActiveFieldName = mock(FieldName.class);

        String collectionName = "collection";
        String key = "key";

        String idKey = collectionName + "ID";

        String documentKey = "document";
        String startDateTimeKey = "startDateTime";
        String isActiveKey = "isActive";

        when(IOC.resolve(fieldNameKey, key)).thenReturn(keyFieldName);
        when(IOC.resolve(fieldNameKey, idKey)).thenReturn(idKeyFieldName);
        when(IOC.resolve(fieldNameKey, documentKey)).thenReturn(documentKeyFieldName);
        when(IOC.resolve(fieldNameKey, startDateTimeKey)).thenReturn(startDateTimeFieldName);
        when(IOC.resolve(fieldNameKey, isActiveKey)).thenReturn(isActiveFieldName);

        testTask = new DBInsertWIthKeyTask(task, collectionName, key);
    }

    @Test
    public void MustCorrectPrepareObject() throws ReadValueException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);
        IObject document = mock(IObject.class);

        Object notNullObject = mock(Object.class);

        when(query.getValue(documentKeyFieldName)).thenReturn(document);

        when(document.getValue(keyFieldName)).thenReturn(notNullObject);
        when(document.getValue(startDateTimeFieldName)).thenReturn(notNullObject);

        testTask.prepare(query);

        verify(query).getValue(documentKeyFieldName);

        verify(document).getValue(keyFieldName);
        verify(document).getValue(startDateTimeFieldName);
        verify(document).setValue(idKeyFieldName, null);

        verify(task).prepare(document);
    }

    @Test(expected = NullPointerException.class)
    public void MustInCorrectPrepareWhenObjectIsAbsent() throws ReadValueException, TaskPrepareException {
        IObject query = mock(IObject.class);

        testTask.prepare(query);

        verify(query).getValue(documentKeyFieldName);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareObjectWhenObjectIsNotContainingKey() throws ReadValueException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);
        IObject document = mock(IObject.class);

        when(query.getValue(documentKeyFieldName)).thenReturn(document);

        testTask.prepare(query);

        verify(query).getValue(documentKeyFieldName);

        verify(document).getValue(keyFieldName);
    }

    @Test
    public void MustCorrectPrepareObjectAndPutInDate() throws ReadValueException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);
        IObject document = mock(IObject.class);

        Object notNullObject = mock(Object.class);

        when(query.getValue(documentKeyFieldName)).thenReturn(document);

        when(document.getValue(keyFieldName)).thenReturn(notNullObject);

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String targetDate = dateFormat.format(new Date());//TODO: is it normal?

        testTask.prepare(query);

        verify(query).getValue(documentKeyFieldName);

        verify(document).getValue(keyFieldName);
        verify(document).getValue(startDateTimeFieldName);
        verify(document).setValue(eq(startDateTimeFieldName), eq(targetDate));
        verify(document).setValue(idKeyFieldName, null);

        verify(task).prepare(document);
    }

    @Test
    public void MustCorrectExecuteTask() throws ReadValueException, TaskPrepareException, ChangeValueException, TaskExecutionException {
        IObject query = mock(IObject.class);
        IObject document = mock(IObject.class);

        Object notNullObject = mock(Object.class);

        when(query.getValue(documentKeyFieldName)).thenReturn(document);

        when(document.getValue(keyFieldName)).thenReturn(notNullObject);
        when(document.getValue(startDateTimeFieldName)).thenReturn(notNullObject);

        testTask.prepare(query);//work of this method tested below this test

        Boolean isActiveOldValue = false;

        when(document.getValue(isActiveFieldName)).thenReturn(isActiveOldValue);

        testTask.execute();

        verify(document).getValue(isActiveFieldName);
        verify(document).setValue(isActiveFieldName, true);

        verify(task).execute();
    }

    @Test
    public void MustIncorrectExecuteTaskWhen() throws ReadValueException, TaskPrepareException, ChangeValueException, TaskExecutionException {
        IObject query = mock(IObject.class);
        IObject document = mock(IObject.class);

        Object notNullObject = mock(Object.class);

        when(query.getValue(documentKeyFieldName)).thenReturn(document);

        when(document.getValue(keyFieldName)).thenReturn(notNullObject);
        when(document.getValue(startDateTimeFieldName)).thenReturn(notNullObject);

        testTask.prepare(query);//work of this method tested below this test

        Boolean isActiveOldValue = false;

        when(document.getValue(isActiveFieldName)).thenReturn(isActiveOldValue);

        doThrow(new TaskExecutionException("")).when(task).execute();

        try {
            testTask.execute();
        } catch (TaskExecutionException e) {
            verify(document).getValue(isActiveFieldName);
            verify(document).setValue(isActiveFieldName, true);

            verify(task).execute();

            verify(document).setValue(isActiveFieldName, isActiveOldValue);
            return;
        }
        assertTrue("Execute must call exception, but it was not founded", false);//i'm sorry for my english
    }

    @Test
    public void MustCorrectExecuteTaskAndSetDefaultValueAsOlIsActive() throws ReadValueException, TaskPrepareException, ChangeValueException, TaskExecutionException {
        IObject query = mock(IObject.class);
        IObject document = mock(IObject.class);

        Object notNullObject = mock(Object.class);

        when(query.getValue(documentKeyFieldName)).thenReturn(document);

        when(document.getValue(keyFieldName)).thenReturn(notNullObject);
        when(document.getValue(startDateTimeFieldName)).thenReturn(notNullObject);

        testTask.prepare(query);//work of this method tested below this test

        Boolean isActiveOldValue = false;

        doThrow(new ReadValueException()).when(document).getValue(isActiveFieldName);

        doThrow(new TaskExecutionException("")).when(task).execute();

        try {
            testTask.execute();
        } catch (TaskExecutionException e) {
            verify(document).getValue(isActiveFieldName);
            verify(document).setValue(isActiveFieldName, true);

            verify(task).execute();

            verify(document).setValue(isActiveFieldName, isActiveOldValue);
            return;
        }
        assertTrue("Execute must call exception, but it was not founded", false);//i'm sorry for my english
    }
}