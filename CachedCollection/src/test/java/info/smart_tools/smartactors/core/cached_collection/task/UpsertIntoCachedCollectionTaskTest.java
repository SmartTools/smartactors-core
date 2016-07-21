package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class UpsertIntoCachedCollectionTaskTest {

    private UpsertIntoCachedCollectionTask task;
    private IDatabaseTask upsertTask;

    private IField startDateTimeField;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        startDateTimeField = mock(IField.class);
        IKey keyField = mock(IKey.class);
        when(Keys.getOrAdd(IField.class.toString())).thenReturn(keyField);
        when(IOC.resolve(keyField, "document/startDateTime")).thenReturn(startDateTimeField);

        upsertTask = mock(IDatabaseTask.class);
        task = new UpsertIntoCachedCollectionTask(upsertTask);
    }

    @Test
    public void ShouldSetConnectionToNestedTask() throws TaskSetConnectionException {

        StorageConnection connection = mock(StorageConnection.class);
        task.setConnection(connection);
        verify(upsertTask).setConnection(eq(connection));
    }

    @Test
    public void ShouldExecuteNestedTask() throws TaskExecutionException {

        task.execute();
        verify(upsertTask).execute();
    }

    @Test
    public void ShouldPrepareUpsertQuery() throws Exception {

        IObject rawQuery = mock(IObject.class);

        when(startDateTimeField.in(rawQuery)).thenReturn(null);
        task.prepare(rawQuery);

        verify(upsertTask).prepare(eq(rawQuery));
        verify(startDateTimeField).out(eq(rawQuery), anyString());
    }

    @Test(expected = TaskPrepareException.class)
    public void ShouldThrowException_When_ResolutionExceptionIsThrown() throws Exception {

        IObject rawQuery = mock(IObject.class);
        when(startDateTimeField.in(rawQuery)).thenThrow(ChangeValueException.class);

        task.prepare(rawQuery);
    }
}
