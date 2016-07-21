package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, UpdateAsyncOperationTask.class})
public class UpdateAsyncOperationTaskTest {
    private UpdateAsyncOperationTask testTask;
    private IDatabaseTask targetTask;

    private IField doneFlagField;

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        doneFlagField = mock(IField.class);

        Key fieldKey = mock(Key.class);
        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);

        when(IOC.resolve(fieldKey, "document/done")).thenReturn(doneFlagField);

        targetTask = mock(IDatabaseTask.class);

        testTask = new UpdateAsyncOperationTask(targetTask);

        verifyStatic();
        Keys.getOrAdd(IField.class.toString());

        verifyStatic();
        IOC.resolve(fieldKey, "document/done");
    }

    @Test
    public void MustCorrectPrepareQuery() throws InvalidArgumentException, TaskPrepareException, ChangeValueException {
        IObject query = mock(IObject.class);

        testTask.prepare(query);

        verify(doneFlagField).out(query, true);

        verify(targetTask).prepare(query);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenUpdateItemThrowChangeValueException() throws InvalidArgumentException, ChangeValueException {
        IObject query = mock(IObject.class);

        doThrow(new ChangeValueException()).when(doneFlagField).out(query, true);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verify(doneFlagField).out(query, true);

            return;
        }
        assertTrue("Must throw exception", false);

    }

    @Test
    public void MustInCorrectPrepareQueryWhenTargetTaskThrowException() throws InvalidArgumentException, ChangeValueException, TaskPrepareException {
        IObject query = mock(IObject.class);

        doThrow(new TaskPrepareException("")).when(targetTask).prepare(query);

        try {
            testTask.prepare(query);
        } catch (TaskPrepareException e) {

            verify(doneFlagField).out(query, true);

            verify(targetTask).prepare(query);
            return;
        }
        assertTrue("Must throw exception", false);

    }

    @Test
    public void MustCorrectSetConnection() {

        IStorageConnection connection = mock(IStorageConnection.class);

        testTask.setConnection(connection);

        verify(targetTask).setConnection(connection);
    }

    @Test
    public void MustCorrectExecute() throws TaskExecutionException {
        testTask.execute();

        verify(targetTask).execute();
    }
}