package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.wrapper.get_item.DateToMessage;
import info.smart_tools.smartactors.core.cached_collection.wrapper.get_item.EQMessage;
import info.smart_tools.smartactors.core.cached_collection.wrapper.GetObjectFromCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.CriteriaCachedCollectionQuery;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.verify;


@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, LocalDateTime.class})
public class GetObjectFromCachedCollectionTaskTest {

    private GetObjectFromCachedCollectionTask testTask;
    private IDatabaseTask targetTask;

    private String key = "key";

    @Before
    public void prepare () throws ResolutionException, ReadValueException, ChangeValueException, InvalidArgumentException {
        mockStatic(IOC.class);
        mockStatic(Keys.class);
        mockStatic(LocalDateTime.class);

        targetTask = mock(IDatabaseTask.class);
        testTask = new GetObjectFromCachedCollectionTask(targetTask);
    }

    @Test
    public void MustCorrectPrepareQueryForSelecting() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        Key iobjectKey = mock(Key.class);
        IObject testIObject = mock(IObject.class);
        when(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.toString())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(testIObject);

        Key searchCachedCollectionKey = mock(Key.class);
        CriteriaCachedCollectionQuery criteriaQuery = mock(CriteriaCachedCollectionQuery.class);

        when(Keys.getOrAdd(CriteriaCachedCollectionQuery.class.toString())).thenReturn(searchCachedCollectionKey);
        when(IOC.resolve(searchCachedCollectionKey, testIObject)).thenReturn(criteriaQuery);

        EQMessage eqMessage = mock(EQMessage.class);
        Key eqKey = mock(Key.class);
        when(Keys.getOrAdd(EQMessage.class.toString())).thenReturn(eqKey);
        when(IOC.resolve(eqKey, testIObject)).thenReturn(eqMessage);

        DateToMessage dateToMessage = mock(DateToMessage.class);
        Key dateToKey = mock(Key.class);
        when(Keys.getOrAdd(DateToMessage.class.toString())).thenReturn(dateToKey);
        when(IOC.resolve(dateToKey, testIObject)).thenReturn(dateToMessage);

        GetObjectFromCachedCollectionQuery srcQueryObject = mock(GetObjectFromCachedCollectionQuery.class);
        IObject query = mock(IObject.class);
        when(srcQueryObject.getKey()).thenReturn(key);
        when(srcQueryObject.wrapped()).thenReturn(query);

        Key getObjectFromCachedGalleryQueryKey = mock(Key.class);

        when(Keys.getOrAdd(GetObjectFromCachedCollectionQuery.class.toString())).thenReturn(getObjectFromCachedGalleryQueryKey);
        when(IOC.resolve(getObjectFromCachedGalleryQueryKey, query)).thenReturn(srcQueryObject);

        testTask.prepare(query);

        verify(srcQueryObject).getKey();
        verify(eqMessage).setEq(key);
        verify(criteriaQuery).setKey(eqMessage);

        verify(eqMessage).setEq(Boolean.toString(true));
        verify(criteriaQuery).setIsActive(eqMessage);

        verify(dateToMessage).setDateTo(any());// FIXME: 6/23/16 must test time
        verify(criteriaQuery).setStartDateTime(dateToMessage);

        verify(srcQueryObject).setPageNumber(0);
        verify(srcQueryObject).setPageSize(100);// FIXME: 6/23/16 must test time with @GetObjectFromCachedCollectionTask
        verify(srcQueryObject).setCriteria(criteriaQuery);

        verify(targetTask).prepare(query);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryForSelectingWhenIOCThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        IObject query = mock(IObject.class);

        Key getObjectFromCachedGalleryQueryKey = mock(Key.class);
        when(Keys.getOrAdd(GetObjectFromCachedCollectionQuery.class.toString())).thenReturn(getObjectFromCachedGalleryQueryKey);
        when(IOC.resolve(getObjectFromCachedGalleryQueryKey, query)).thenThrow(new ResolutionException(""));

        testTask.prepare(query);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryForSelectingWhenWrapperThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        Key iobjectKey = mock(Key.class);
        IObject testIObject = mock(IObject.class);
        when(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.toString())).thenReturn(iobjectKey);
        when(IOC.resolve(iobjectKey)).thenReturn(testIObject);

        Key searchCachedCollectionKey = mock(Key.class);
        CriteriaCachedCollectionQuery criteriaQuery = mock(CriteriaCachedCollectionQuery.class);

        when(Keys.getOrAdd(CriteriaCachedCollectionQuery.class.toString())).thenReturn(searchCachedCollectionKey);
        when(IOC.resolve(searchCachedCollectionKey, testIObject)).thenReturn(criteriaQuery);

        EQMessage eqMessage = mock(EQMessage.class);
        Key eqKey = mock(Key.class);
        when(Keys.getOrAdd(EQMessage.class.toString())).thenReturn(eqKey);
        when(IOC.resolve(eqKey, testIObject)).thenReturn(eqMessage);

        doThrow(new ChangeValueException()).when(eqMessage).setEq(any());

        GetObjectFromCachedCollectionQuery srcQueryObject = mock(GetObjectFromCachedCollectionQuery.class);
        IObject query = mock(IObject.class);
        when(srcQueryObject.getKey()).thenReturn(key);
        when(srcQueryObject.wrapped()).thenReturn(query);

        Key getObjectFromCachedGalleryQueryKey = mock(Key.class);

        when(Keys.getOrAdd(GetObjectFromCachedCollectionQuery.class.toString())).thenReturn(getObjectFromCachedGalleryQueryKey);
        when(IOC.resolve(getObjectFromCachedGalleryQueryKey, query)).thenReturn(srcQueryObject);

        testTask.prepare(query);
    }

    @Test
    public void MustCorrectExecuteQuery() throws TaskExecutionException {
        testTask.execute();

        verify(targetTask).execute();
    }

    @Test
    public void MostCorrectlySetConnectionToNestedTask() throws TaskSetConnectionException {

        StorageConnection connection = mock(StorageConnection.class);

        testTask.setConnection(connection);

        verify(targetTask).setConnection(eq(connection));
    }

}