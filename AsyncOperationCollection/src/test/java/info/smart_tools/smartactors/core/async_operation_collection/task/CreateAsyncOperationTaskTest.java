package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
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

    private IField asyncDataField;
    private IField doneFlagField;
    private IField tokenField;
    private IField expiredTimeField;
    private IField documentField;

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        targetTask = mock(IDatabaseTask.class);

        asyncDataField = mock(IField.class);
        doneFlagField = mock(IField.class);
        tokenField = mock(IField.class);
        expiredTimeField = mock(IField.class);
        documentField = mock(IField.class);

        Key fieldKey = mock(Key.class);
        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(fieldKey);

        when(IOC.resolve(fieldKey, "asyncData")).thenReturn(asyncDataField);

        when(IOC.resolve(fieldKey, "done")).thenReturn(doneFlagField);

        when(IOC.resolve(fieldKey, "token")).thenReturn(tokenField);

        when(IOC.resolve(fieldKey, "expiredTime")).thenReturn(expiredTimeField);

        when(IOC.resolve(fieldKey, "document")).thenReturn(documentField);

        testTask = new CreateAsyncOperationTask(targetTask);

        verifyStatic(times(5));
        Keys.getOrAdd(IField.class.getCanonicalName());

        verifyStatic();
        IOC.resolve(fieldKey, "asyncData");

        verifyStatic();
        IOC.resolve(fieldKey, "done");

        verifyStatic();
        IOC.resolve(fieldKey, "token");

        verifyStatic();
        IOC.resolve(fieldKey, "expiredTime");

        verifyStatic();
        IOC.resolve(fieldKey, "document");
    }

    @Test
    public void MustCorrectPrepareQuery() throws ResolutionException, ReadValueException, InvalidArgumentException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);
        IObject documentIObject = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(documentIObject);

        IObject asyncData = mock(IObject.class);
        String token = "token";
        String expiredTime = "expTime";

        when(asyncDataField.in(query)).thenReturn(asyncData);
        when(tokenField.in(query)).thenReturn(token);
        when(expiredTimeField.in(query)).thenReturn(expiredTime);

        testTask.prepare(query);

        verifyStatic();
        Keys.getOrAdd(IObject.class.getCanonicalName());
        verifyStatic();
        IOC.resolve(iobjectKey);

        verify(asyncDataField).in(query);
        verify(asyncDataField).out(documentIObject, asyncData);

        verify(doneFlagField).out(documentIObject, false);

        verify(tokenField).in(query);
        verify(tokenField).out(documentIObject, token);

        verify(expiredTimeField).in(query);
        verify(expiredTimeField).out(documentIObject, expiredTime);

        verify(documentField).out(query, documentIObject);

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

        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verifyStatic();
            Keys.getOrAdd(IObject.class.getCanonicalName());
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenIOCThrowException() throws ResolutionException {
        IObject query = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenThrow(new ResolutionException(""));

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e){

            verifyStatic();
            Keys.getOrAdd(IObject.class.getCanonicalName());
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
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(documentIObject);

        when(asyncDataField.in(query)).thenThrow(new ReadValueException());

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verifyStatic();
            Keys.getOrAdd(IObject.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(iobjectKey);

            verify(asyncDataField).in(query);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenDocumentThrowChangeValueException() throws ResolutionException, ReadValueException, ChangeValueException, InvalidArgumentException {
        IObject query = mock(IObject.class);
        IObject documentIObject = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(documentIObject);

        IObject asyncData = mock(IObject.class);

        when(asyncDataField.in(query)).thenReturn(asyncData);

        doThrow(new ChangeValueException()).when(asyncDataField).out(documentIObject, asyncData);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verifyStatic();
            Keys.getOrAdd(IObject.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(iobjectKey);

            verify(asyncDataField).in(query);
            verify(asyncDataField).out(documentIObject, asyncData);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenTargetTaskThrowException() throws TaskPrepareException, ReadValueException, InvalidArgumentException, ResolutionException, ChangeValueException {
        IObject query = mock(IObject.class);
        IObject documentIObject = mock(IObject.class);

        Key iobjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(documentIObject);

        IObject asyncData = mock(IObject.class);
        String token = "token";
        String expiredTime = "expTime";

        when(asyncDataField.in(query)).thenReturn(asyncData);
        when(tokenField.in(query)).thenReturn(token);
        when(expiredTimeField.in(query)).thenReturn(expiredTime);

        doThrow(new TaskPrepareException("")).when(targetTask).prepare(query);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verifyStatic();
            Keys.getOrAdd(IObject.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(iobjectKey);

            verify(asyncDataField).in(query);
            verify(asyncDataField).out(documentIObject, asyncData);

            verify(doneFlagField).out(documentIObject, false);

            verify(tokenField).in(query);
            verify(tokenField).out(documentIObject, token);

            verify(expiredTimeField).in(query);
            verify(expiredTimeField).out(documentIObject, expiredTime);

            verify(documentField).out(query, documentIObject);

            verify(targetTask).prepare(query);
            return;
        }
        assertTrue("Must throw exception", false);
    }
}