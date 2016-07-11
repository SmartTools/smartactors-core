package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.create_item.AsyncDocument;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.create_item.CreateOperationQuery;
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
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, CreateAsyncOperationTask.class})
public class CreateAsyncOperationTaskTest {

    private CreateAsyncOperationTask testTask;
    private IDatabaseTask targetTask;

    private Field<IObject> asyncDataField;
    private Field<Boolean> doneFlagField;
    private Field<String> tokenField;
    private Field<String> expiredTimeField;
    private Field<IObject> documentField;

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        targetTask = mock(IDatabaseTask.class);

        asyncDataField = mock(Field.class);
        doneFlagField = mock(Field.class);
        tokenField = mock(Field.class);
        expiredTimeField = mock(Field.class);
        documentField = mock(Field.class);

        Key fieldNameKey = mock(Key.class);
        when(Keys.getOrAdd(IFieldName.class.toString())).thenReturn(fieldNameKey);

        String asyncDataFieldNameBindingPath = "asyncDataFieldNameBindingPath";
        when(IOC.resolve(fieldNameKey, "asyncData")).thenReturn(asyncDataFieldNameBindingPath);

        String doneFieldNameBindingPath = "doneFieldNameBindingPath";
        when(IOC.resolve(fieldNameKey, "done")).thenReturn(doneFieldNameBindingPath);

        String tokenFieldNameBindingPath = "tokenFieldNameBindingPath";
        when(IOC.resolve(fieldNameKey, "token")).thenReturn(tokenFieldNameBindingPath);

        String expiredTimeFieldNameBindingPath = "expiredTimeFieldNameBindingPath";
        when(IOC.resolve(fieldNameKey, "expiredTime")).thenReturn(expiredTimeFieldNameBindingPath);

        String documentFieldNameBindingPath = "documentFieldNameBindingPath";
        when(IOC.resolve(fieldNameKey, "document")).thenReturn(documentFieldNameBindingPath);

        whenNew(Field.class).withArguments(asyncDataFieldNameBindingPath).thenReturn(asyncDataField);
        whenNew(Field.class).withArguments(doneFieldNameBindingPath).thenReturn(doneFlagField);
        whenNew(Field.class).withArguments(tokenFieldNameBindingPath).thenReturn(tokenField);
        whenNew(Field.class).withArguments(expiredTimeFieldNameBindingPath).thenReturn(expiredTimeField);
        whenNew(Field.class).withArguments(documentFieldNameBindingPath).thenReturn(documentField);

        testTask = new CreateAsyncOperationTask(targetTask);

        verifyStatic(times(5));
        Keys.getOrAdd(IFieldName.class.toString());

        verifyStatic();
        IOC.resolve(fieldNameKey, "asyncData");
        verifyNew(Field.class).withArguments(asyncDataFieldNameBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "done");
        verifyNew(Field.class).withArguments(doneFieldNameBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "token");
        verifyNew(Field.class).withArguments(tokenFieldNameBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "expiredTime");
        verifyNew(Field.class).withArguments(expiredTimeFieldNameBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "document");
        verifyNew(Field.class).withArguments(documentFieldNameBindingPath);
    }

    @Test
    public void MustCorrectPrepareQuery() throws ResolutionException, ReadValueException, InvalidArgumentException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);
        IObject documentIObject = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(documentIObject);

        IObject asyncData = mock(IObject.class);
        String token = "token";
        String expiredTime = "expTime";

        when(asyncDataField.out(query)).thenReturn(asyncData);
        when(tokenField.out(query)).thenReturn(token);
        when(expiredTimeField.out(query)).thenReturn(expiredTime);

        testTask.prepare(query);

        verifyStatic();
        Keys.getOrAdd(IObject.class.toString());
        verifyStatic();
        IOC.resolve(iobjectKey);

        verify(asyncDataField).out(query);
        verify(asyncDataField).in(documentIObject, asyncData);

        verify(doneFlagField).in(documentIObject, false);

        verify(tokenField).out(query);
        verify(tokenField).in(documentIObject, token);

        verify(expiredTimeField).out(query);
        verify(expiredTimeField).in(documentIObject, expiredTime);

        verify(documentField).in(query, documentIObject);

        verify(targetTask).prepare(query);
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

    @Test
    public void MustInCorrectPrepareQueryWhenKeysThrowException() throws ResolutionException {
        IObject query = mock(IObject.class);

        when(Keys.getOrAdd(IObject.class.toString())).thenThrow(new ResolutionException(""));

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verifyStatic();
            Keys.getOrAdd(IObject.class.toString());
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenIOCThrowException() throws ResolutionException {
        IObject query = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenThrow(new ResolutionException(""));

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e){

            verifyStatic();
            Keys.getOrAdd(IObject.class.toString());
            verifyStatic();
            IOC.resolve(iobjectKey);

            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenQueryThrowReadValueException() throws ResolutionException, ReadValueException, InvalidArgumentException {
        IObject query = mock(IObject.class);
        IObject documentIObject = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(documentIObject);

        when(asyncDataField.out(query)).thenThrow(new ReadValueException());

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verifyStatic();
            Keys.getOrAdd(IObject.class.toString());
            verifyStatic();
            IOC.resolve(iobjectKey);

            verify(asyncDataField).out(query);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenDocumentThrowChangeValueException() throws ResolutionException, ReadValueException, ChangeValueException, InvalidArgumentException {
        IObject query = mock(IObject.class);
        IObject documentIObject = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(documentIObject);

        IObject asyncData = mock(IObject.class);

        when(asyncDataField.out(query)).thenReturn(asyncData);

        doThrow(new ChangeValueException()).when(asyncDataField).in(documentIObject, asyncData);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verifyStatic();
            Keys.getOrAdd(IObject.class.toString());
            verifyStatic();
            IOC.resolve(iobjectKey);

            verify(asyncDataField).out(query);
            verify(asyncDataField).in(documentIObject, asyncData);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenTargetTaskThrowException() throws TaskPrepareException, ReadValueException, InvalidArgumentException, ResolutionException, ChangeValueException {
        IObject query = mock(IObject.class);
        IObject documentIObject = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(documentIObject);

        IObject asyncData = mock(IObject.class);
        String token = "token";
        String expiredTime = "expTime";

        when(asyncDataField.out(query)).thenReturn(asyncData);
        when(tokenField.out(query)).thenReturn(token);
        when(expiredTimeField.out(query)).thenReturn(expiredTime);

        doThrow(new TaskPrepareException("")).when(targetTask).prepare(query);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verifyStatic();
            Keys.getOrAdd(IObject.class.toString());
            verifyStatic();
            IOC.resolve(iobjectKey);

            verify(asyncDataField).out(query);
            verify(asyncDataField).in(documentIObject, asyncData);

            verify(doneFlagField).in(documentIObject, false);

            verify(tokenField).out(query);
            verify(tokenField).in(documentIObject, token);

            verify(expiredTimeField).out(query);
            verify(expiredTimeField).in(documentIObject, expiredTime);

            verify(documentField).in(query, documentIObject);

            verify(targetTask).prepare(query);
            return;
        }
        assertTrue("Must throw exception", false);
    }
}