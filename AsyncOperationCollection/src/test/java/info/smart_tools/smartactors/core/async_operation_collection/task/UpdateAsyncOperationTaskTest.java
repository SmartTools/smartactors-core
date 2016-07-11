package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.update.UpdateAsyncOperationQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.update.UpdateItem;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import info.smart_tools.smartactors.core.wrapper_generator.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, UpdateAsyncOperationTask.class})
public class UpdateAsyncOperationTaskTest {
    private UpdateAsyncOperationTask testTask;
    private IDatabaseTask targetTask;

    private Field<IObject> updateIObjectField;
    private Field<Boolean> doneFlagField;

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        updateIObjectField = mock(Field.class);
        doneFlagField = mock(Field.class);

        Key fieldNameKey = mock(Key.class);
        when(Keys.getOrAdd(IFieldName.class.toString())).thenReturn(fieldNameKey);

        String updateItemFieldNameBindingPath = "updateItemFieldNameBindingPath";
        when(IOC.resolve(fieldNameKey, "updateItem")).thenReturn(updateItemFieldNameBindingPath);

        String doneFieldNameBindingPath = "doneFieldNameBindingPath";
        when(IOC.resolve(fieldNameKey, "done")).thenReturn(doneFieldNameBindingPath);

        whenNew(Field.class).withArguments(doneFieldNameBindingPath).thenReturn(doneFlagField);
        whenNew(Field.class).withArguments(updateItemFieldNameBindingPath).thenReturn(updateIObjectField);

        targetTask = mock(IDatabaseTask.class);

        testTask = new UpdateAsyncOperationTask(targetTask);

        verifyStatic(times(2));
        Keys.getOrAdd(IFieldName.class.toString());

        verifyStatic();
        IOC.resolve(fieldNameKey, "updateItem");
        verifyNew(Field.class).withArguments(updateItemFieldNameBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "done");
        verifyNew(Field.class).withArguments(doneFieldNameBindingPath);
    }

    @Test
    public void MustCorrectPrepareQuery() throws ReadValueException, InvalidArgumentException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);

        IObject updateItem = mock(IObject.class);

        when(updateIObjectField.out(query)).thenReturn(updateItem);

        testTask.prepare(query);

        verify(updateIObjectField).out(query);

        verify(doneFlagField).in(updateItem, true);

        verify(targetTask).prepare(query);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenMessageThrowReadValueException() throws ReadValueException, InvalidArgumentException {
        IObject query = mock(IObject.class);

        when(updateIObjectField.out(query)).thenThrow(new ReadValueException());

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verify(updateIObjectField).out(query);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenUpdateItemThrowChangeValueException() throws ReadValueException, InvalidArgumentException, ChangeValueException {
        IObject query = mock(IObject.class);

        IObject updateItem = mock(IObject.class);

        when(updateIObjectField.out(query)).thenReturn(updateItem);

        doThrow(new ChangeValueException()).when(doneFlagField).in(updateItem, true);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verify(updateIObjectField).out(query);

            verify(doneFlagField).in(updateItem, true);

            return;
        }
        assertTrue("Must throw exception", false);

    }

    @Test
    public void MustInCorrectPrepareQueryWhenTargetTaskThrowException() throws ReadValueException, InvalidArgumentException, ChangeValueException, TaskPrepareException {
        IObject query = mock(IObject.class);

        IObject updateItem = mock(IObject.class);

        when(updateIObjectField.out(query)).thenReturn(updateItem);

        doThrow(new TaskPrepareException("")).when(targetTask).prepare(query);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verify(updateIObjectField).out(query);

            verify(doneFlagField).in(updateItem, true);

            verify(targetTask).prepare(query);
            return;
        }
        assertTrue("Must throw exception", false);

    }

    @Test
    public void MustCorrectSetConnection() throws TaskSetConnectionException {

        StorageConnection connection = mock(StorageConnection.class);

        testTask.setConnection(connection);

        verify(targetTask).setConnection(connection);
    }

    @Test
    public void MustCorrectExecute() throws TaskExecutionException {
        testTask.execute();

        verify(targetTask).execute();
    }
}