package info.smart_tools.smartactors.database.async_operation_collection.task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, UpdateAsyncOperationTask.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class UpdateAsyncOperationTaskTest {
    private UpdateAsyncOperationTask testTask;
    private IStorageConnection connection;
    private IDatabaseTask targetTask;

    private IField doneFlagField;
    private IField documentField;
    private IField collectionNameField;

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        doneFlagField = mock(IField.class);
        documentField = mock(IField.class);
        collectionNameField = mock(IField.class);

        Key fieldKey = mock(Key.class);
        when(Keys.getKeyByName(IField.class.getCanonicalName())).thenReturn(fieldKey);

        when(IOC.resolve(fieldKey, "done")).thenReturn(doneFlagField);
        when(IOC.resolve(fieldKey, "document")).thenReturn(documentField);
        when(IOC.resolve(fieldKey, "collectionName")).thenReturn(collectionNameField);

        connection = mock(IStorageConnection.class);
        targetTask = mock(IDatabaseTask.class);

        testTask = new UpdateAsyncOperationTask(connection);

        verifyStatic(times(3));
        Keys.getKeyByName(IField.class.getCanonicalName());

        verifyStatic();
        IOC.resolve(fieldKey, "done");

        verifyStatic();
        IOC.resolve(fieldKey, "document");

        verifyStatic();
        IOC.resolve(fieldKey, "collectionName");
    }

    @Test
    public void MustCorrectPrepare() throws ReadValueException, InvalidArgumentException, ResolutionException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);

        IObject document = mock(IObject.class);
        when(documentField.in(query)).thenReturn(document);

        String collectionName = "example";

        IKey upsertTaskKey = mock(IKey.class);
        when(Keys.getKeyByName("db.collection.upsert")).thenReturn(upsertTaskKey);
        when(collectionNameField.in(query)).thenReturn(collectionName);

        when(IOC.resolve(upsertTaskKey, connection, collectionName, document)).thenReturn(targetTask);

        testTask.prepare(query);

        verify(documentField).in(query);
        verify(doneFlagField).out(document, true);

        verifyStatic();
        Keys.getKeyByName("db.collection.upsert");

        verifyStatic();
        IOC.resolve(upsertTaskKey, connection, collectionName, document);
    }

    @Test
    public void MustInCorrectPrepareWhenDocumentFieldInThrowReadValueException() throws ReadValueException, InvalidArgumentException {
        IObject query = mock(IObject.class);

        when(documentField.in(query)).thenThrow(new ReadValueException());

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verify(documentField).in(query);
            return;
        }
        assertTrue(false);
    }

    @Test
    public void MustInCorrectPrepareWhenDocumentFieldInThrowInvalidArgumentException() throws ReadValueException, InvalidArgumentException {
        IObject query = mock(IObject.class);

        when(documentField.in(query)).thenThrow(new InvalidArgumentException(""));

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verify(documentField).in(query);
            return;
        }
        assertTrue(false);
    }

    @Test
    public void MustInCorrectPrepareWhenDoneFlagFieldOutThrowException() throws ReadValueException, InvalidArgumentException, ResolutionException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);

        IObject document = mock(IObject.class);
        when(documentField.in(query)).thenReturn(document);

        doThrow(new ChangeValueException()).when(doneFlagField).out(document, true);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verify(documentField).in(query);
            verify(doneFlagField).out(document, true);
            return;
        }
        assertTrue(false);
    }

    @Test
    public void MustInCorrectPrepareWhenKeysgetKeyByNameThrowException() throws ReadValueException, InvalidArgumentException, ResolutionException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);

        IObject document = mock(IObject.class);
        when(documentField.in(query)).thenReturn(document);

        when(Keys.getKeyByName("db.collection.upsert")).thenThrow(new ResolutionException(""));

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verify(documentField).in(query);
            verify(doneFlagField).out(document, true);

            verifyStatic();
            Keys.getKeyByName("db.collection.upsert");
            return;
        }
        assertTrue(false);
    }

    @Test
    public void MustInCorrectPrepareWhenIOCResolveThrowException() throws ReadValueException, InvalidArgumentException, ResolutionException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);

        IObject document = mock(IObject.class);
        when(documentField.in(query)).thenReturn(document);

        String collectionName = "example";

        IKey upsertTaskKey = mock(IKey.class);
        when(Keys.getKeyByName("db.collection.upsert")).thenReturn(upsertTaskKey);
        when(collectionNameField.in(query)).thenReturn(collectionName);

        when(IOC.resolve(upsertTaskKey, connection, collectionName, document)).thenThrow(new ResolutionException(""));

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {
            verify(documentField).in(query);
            verify(doneFlagField).out(document, true);

            verifyStatic();
            Keys.getKeyByName("db.collection.upsert");

            verifyStatic();
            IOC.resolve(upsertTaskKey, connection, collectionName, document);
            return;
        }
        assertTrue(false);
    }

    @Test
    public void MustCorrectExecute() throws TaskExecutionException, ChangeValueException, ReadValueException, InvalidArgumentException, TaskPrepareException, ResolutionException {
        MustCorrectPrepare();

        testTask.execute();

        verify(targetTask).execute();
    }
}
