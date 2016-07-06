package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.AsyncOperationTaskQuery;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.EQMessage;
import info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item.GetAsyncOperationQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
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

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class GetAsyncOperationTaskTest {
    private GetAsyncOperationTask testTask;
    private IDatabaseTask targetTask;

    @Before
    public void prepare () throws ResolutionException, ReadValueException, ChangeValueException, InvalidArgumentException {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        targetTask = mock(IDatabaseTask.class);
        testTask = new GetAsyncOperationTask(targetTask);
    }

    @Test
    public void MustCorrectPrepareQuery() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        GetAsyncOperationQuery query = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.toString())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey, testQuery)).thenReturn(query);

        AsyncOperationTaskQuery taskQuery = mock(AsyncOperationTaskQuery.class);
        Key asyncOperationTaskQueryKey = mock(Key.class);
        when(Keys.getOrAdd(AsyncOperationTaskQuery.class.toString())).thenReturn(asyncOperationTaskQueryKey);
        when(IOC.resolve(asyncOperationTaskQueryKey)).thenReturn(taskQuery);

        EQMessage eqMessage = mock(EQMessage.class);
        Key eqKey = mock(Key.class);
        when(Keys.getOrAdd(EQMessage.class.toString())).thenReturn(eqKey);
        when(IOC.resolve(eqKey)).thenReturn(eqMessage);

        String token = "tiptoken";

        when(query.getToken()).thenReturn(token);

        testTask.prepare(testQuery);

        verify(query).getToken();

        verify(eqMessage).setEq(token);
        verify(taskQuery).setToken(eqMessage);

        verify(query).setPageNumber(1);
        verify(query).setPageSize(1);
        verify(query).setQuery(taskQuery);

        verify(targetTask).prepare(testQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryWhenKeysGetOrAddThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        when(Keys.getOrAdd(GetAsyncOperationQuery.class.toString())).thenThrow(new ResolutionException(""));

        testTask.prepare(testQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryWhenIOCResolveThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.toString())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey, testQuery)).thenThrow(new ResolutionException(""));

        testTask.prepare(testQuery);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenGetTokenThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        GetAsyncOperationQuery query = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.toString())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey, testQuery)).thenReturn(query);

        EQMessage eqMessage = mock(EQMessage.class);
        Key eqKey = mock(Key.class);
        when(Keys.getOrAdd(EQMessage.class.toString())).thenReturn(eqKey);
        when(IOC.resolve(eqKey)).thenReturn(eqMessage);

        when(query.getToken()).thenThrow(new ReadValueException());

        try {
            testTask.prepare(testQuery);
        } catch (TaskPrepareException e) {
            verify(query).getToken();
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenSetEqThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        GetAsyncOperationQuery query = mock(GetAsyncOperationQuery.class);
        Key getAsyncOperationQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetAsyncOperationQuery.class.toString())).thenReturn(getAsyncOperationQueryKey);
        when(IOC.resolve(getAsyncOperationQueryKey, testQuery)).thenReturn(query);

        EQMessage eqMessage = mock(EQMessage.class);
        Key eqKey = mock(Key.class);
        when(Keys.getOrAdd(EQMessage.class.toString())).thenReturn(eqKey);
        when(IOC.resolve(eqKey)).thenReturn(eqMessage);

        String token = "tiptoken";

        when(query.getToken()).thenReturn(token);

        doThrow(new ChangeValueException()).when(eqMessage).setEq(token);

        try {
            testTask.prepare(testQuery);
        } catch (TaskPrepareException e) {
            verify(query).getToken();
            verify(eqMessage).setEq(token);
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