package info.smart_tools.smartactors.core.cached_collection.task;

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

import java.time.LocalDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, LocalDateTime.class})
public class GetObjectFromCachedCollectionTaskTest {

    private GetObjectFromCachedCollectionTask testTask;
    private IDatabaseTask targetTask;

    private IField pageSizeField;
    private IField pageNumberField;
    private IField keyNameField;
    private IField keyValueField;
    private IField criteriaEqualsIsActiveField;
    private IField criteriaDateToStartDateTimeField;
    private IKey mockKeyField = mock(IKey.class);

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);
        mockStatic(LocalDateTime.class);

        IField collectionNameField = mock(IField.class);
        keyNameField = mock(IField.class);
        keyValueField = mock(IField.class);
        pageSizeField = mock(IField.class);
        pageNumberField = mock(IField.class);
        criteriaEqualsIsActiveField = mock(IField.class);
        criteriaDateToStartDateTimeField = mock(IField.class);


        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(mockKeyField);
        when(IOC.resolve(mockKeyField, "collectionName")).thenReturn(collectionNameField);
        when(IOC.resolve(mockKeyField, "keyName")).thenReturn(keyNameField);
        when(IOC.resolve(mockKeyField, "keyValue")).thenReturn(keyValueField);
        when(IOC.resolve(mockKeyField, "pageSize")).thenReturn(pageSizeField);
        when(IOC.resolve(mockKeyField, "pageNumber")).thenReturn(pageNumberField);
        when(IOC.resolve(mockKeyField, "criteria/isActive/$eq")).thenReturn(criteriaEqualsIsActiveField);
        when(IOC.resolve(mockKeyField, "criteria/startDateTime/$date-to")).thenReturn(criteriaDateToStartDateTimeField);

        targetTask = mock(IDatabaseTask.class);
        testTask = new GetObjectFromCachedCollectionTask(targetTask);
    }

    @Test
    public void MustCorrectPrepareQueryForSelecting() throws Exception {

        IObject query = mock(IObject.class);
        when(keyNameField.in(query)).thenReturn("keyName");
        when(keyValueField.in(query)).thenReturn("keyValue");

        IObject queryForNestedTask = mock(IObject.class);
        IKey keyIObject = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(keyIObject);
        when(IOC.resolve(keyIObject)).thenReturn(queryForNestedTask);

        IField criteriaEqualsKeyField = mock(IField.class);
        when(IOC.resolve(mockKeyField, "keyName/$eq/keyValue")).thenReturn(criteriaEqualsKeyField);

        testTask.prepare(query);

        verify(targetTask).prepare(queryForNestedTask);
        verify(criteriaEqualsKeyField).in(queryForNestedTask);
        verify(pageSizeField).out(queryForNestedTask, 100);
        verify(pageNumberField).out(queryForNestedTask, 1);
        verify(criteriaEqualsIsActiveField).out(queryForNestedTask, true);
        verify(criteriaDateToStartDateTimeField).out(eq(queryForNestedTask), anyString());
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryForSelectingWhenIOCThrowException() throws ResolutionException, TaskPrepareException, ReadValueException, ChangeValueException {

        when(IOC.resolve(any())).thenThrow(new ResolutionException(""));

        testTask.prepare(mock(IObject.class));
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