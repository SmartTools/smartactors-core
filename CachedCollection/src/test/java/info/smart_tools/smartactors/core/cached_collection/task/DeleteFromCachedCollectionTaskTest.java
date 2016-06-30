package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.wrapper.delete.DeleteFromCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.delete.DeleteItem;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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

    @Before
    public void setUp() throws ReadValueException, ChangeValueException {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        upsertTask = mock(IDatabaseTask.class);
        task = new DeleteFromCachedCollectionTask(upsertTask);
    }

    @Test
    public void ShouldCorrectPrepareObjectForDeleting() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {
        Key deleteFromCachedCollectionQueryKey = mock(Key.class);
        when(Keys.getOrAdd(DeleteFromCachedCollectionQuery.class.toString())).thenReturn(deleteFromCachedCollectionQueryKey);

        IObject srcQuery = mock(IObject.class);

        DeleteFromCachedCollectionQuery query = mock (DeleteFromCachedCollectionQuery.class);
        when(IOC.resolve(deleteFromCachedCollectionQueryKey, srcQuery)).thenReturn(query);
        when(query.wrapped()).thenReturn(srcQuery);
        DeleteItem deleteItem = mock(DeleteItem.class);
        when(query.getDeleteItem()).thenReturn(deleteItem);

        task.prepare(srcQuery);

        verify(deleteItem).setIsActive(false);
        verify(query).wrapped();
        verify(upsertTask).prepare(srcQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void ShouldInCorrectPrepareObjectForDeletingWhenIOCThrowResolutionException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {
        Key deleteFromCachedCollectionQueryKey = mock(Key.class);
        when(Keys.getOrAdd(DeleteFromCachedCollectionQuery.class.toString())).thenReturn(deleteFromCachedCollectionQueryKey);

        IObject srcQuery = mock(IObject.class);

        when(IOC.resolve(deleteFromCachedCollectionQueryKey, srcQuery)).thenThrow(new ResolutionException(""));

        task.prepare(srcQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void ShouldInCorrectPrepareObjectForDeletingWhenMessageThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {
        Key deleteFromCachedCollectionQueryKey = mock(Key.class);
        when(Keys.getOrAdd(DeleteFromCachedCollectionQuery.class.toString())).thenReturn(deleteFromCachedCollectionQueryKey);

        IObject srcQuery = mock(IObject.class);

        DeleteFromCachedCollectionQuery query = mock (DeleteFromCachedCollectionQuery.class);
        when(IOC.resolve(deleteFromCachedCollectionQueryKey, srcQuery)).thenReturn(query);
        when(query.getDeleteItem()).thenThrow(new ReadValueException());

        task.prepare(srcQuery);
    }

    @Test
    public void ShouldInCorrectPrepareObjectForDeletingWhenDeleteItemThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {
        Key deleteFromCachedCollectionQueryKey = mock(Key.class);
        when(Keys.getOrAdd(DeleteFromCachedCollectionQuery.class.toString())).thenReturn(deleteFromCachedCollectionQueryKey);

        IObject srcQuery = mock(IObject.class);

        DeleteFromCachedCollectionQuery query = mock (DeleteFromCachedCollectionQuery.class);
        when(IOC.resolve(deleteFromCachedCollectionQueryKey, srcQuery)).thenReturn(query);
        when(query.wrapped()).thenReturn(srcQuery);
        DeleteItem deleteItem = mock(DeleteItem.class);
        when(query.getDeleteItem()).thenReturn(deleteItem);
        doThrow(new ChangeValueException()).when(deleteItem).setIsActive(any());

        try {
            task.prepare(srcQuery);
        } catch (TaskPrepareException e) {
            verify(query).getDeleteItem();
            return;
        }
        assertTrue("Must throw new exception", false);
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
