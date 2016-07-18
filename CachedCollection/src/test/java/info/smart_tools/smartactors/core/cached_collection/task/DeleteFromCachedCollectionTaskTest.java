package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class DeleteFromCachedCollectionTaskTest {

    private DeleteFromCachedCollectionTask task;
    private IDatabaseTask upsertTask;
    private IField isActiveField;

    @Before
    public void setUp() throws ReadValueException, ChangeValueException, CreateCachedCollectionTaskException, ResolutionException {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        isActiveField = mock(IField.class);
        IKey keyField = mock(IKey.class);
        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(keyField);
        when(IOC.resolve(keyField, "document/isActive")).thenReturn(isActiveField);
        upsertTask = mock(IDatabaseTask.class);
        task = new DeleteFromCachedCollectionTask(upsertTask);
    }

    @Test
    public void ShouldCorrectPrepareObjectForDeleting() throws Exception {

        IObject query = mock(IObject.class);

        task.prepare(query);

        verify(isActiveField).out(query, false);
        verify(upsertTask).prepare(query);
    }

    @Test(expected = TaskPrepareException.class)
    public void ShouldInCorrectPrepareObjectForDeletingWhenNestedExceptionIsGiven() throws Exception {

        IObject srcQuery = mock(IObject.class);
        doThrow(new ChangeValueException("")).when(isActiveField).out(srcQuery, false);

        task.prepare(srcQuery);
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
}
