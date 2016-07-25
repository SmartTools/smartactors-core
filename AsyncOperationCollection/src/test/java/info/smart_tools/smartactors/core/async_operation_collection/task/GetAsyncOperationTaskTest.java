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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class, GetAsyncOperationTask.class})
public class GetAsyncOperationTaskTest {
    private GetAsyncOperationTask testTask;
    private IDatabaseTask targetTask;

    private IField pageNumberField;
    private IField pageSizeField;
    private IField queryField;
    private IField eqField;
    private IField tokenField;

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        targetTask = mock(IDatabaseTask.class);


        pageNumberField = mock(IField.class);
        pageSizeField = mock(IField.class);
        queryField = mock(IField.class);
        eqField = mock(IField.class);
        tokenField = mock(IField.class);

        Key fieldKey = mock(Key.class);
        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(fieldKey);

        when(IOC.resolve(fieldKey, "pageNumber")).thenReturn(pageNumberField);

        when(IOC.resolve(fieldKey, "pageSize")).thenReturn(pageSizeField);

        when(IOC.resolve(fieldKey, "query")).thenReturn(queryField);

        when(IOC.resolve(fieldKey, "$eq")).thenReturn(eqField);

        when(IOC.resolve(fieldKey, "token")).thenReturn(tokenField);


        testTask = new GetAsyncOperationTask(targetTask);

        verifyStatic(times(5));
        Keys.getOrAdd(IField.class.getCanonicalName());

        verifyStatic();
        IOC.resolve(fieldKey, "pageNumber");

        verifyStatic();
        IOC.resolve(fieldKey, "pageSize");

        verifyStatic();
        IOC.resolve(fieldKey, "query");

        verifyStatic();
        IOC.resolve(fieldKey, "$eq");

        verifyStatic();
        IOC.resolve(fieldKey, "token");
    }

    @Test
    public void MustCorrectPrepareQuery() throws ReadValueException, InvalidArgumentException, ResolutionException, TaskPrepareException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        IObject futureCriteriaObject = mock(IObject.class);
        Key ioBjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(ioBjectKey);
        when(IOC.resolve(ioBjectKey)).thenReturn(futureCriteriaObject);

        String token = "tiptoken";

        when(tokenField.in(testQuery)).thenReturn(token);

        testTask.prepare(testQuery);

        verifyStatic();
        Keys.getOrAdd(IObject.class.getCanonicalName());
        verifyStatic();
        IOC.resolve(ioBjectKey);

        verify(tokenField).in(testQuery);
        verify(eqField).out(futureCriteriaObject, token);

        verify(pageNumberField).out(testQuery, 1);
        verify(pageSizeField).out(testQuery, 1);
        verify(queryField).out(testQuery, futureCriteriaObject);

        verify(targetTask).prepare(testQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryWhenKeysGetOrAddThrowException() throws ResolutionException, TaskPrepareException {

        IObject testQuery = mock(IObject.class);

        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        testTask.prepare(testQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryWhenIOCResolveThrowException() throws ResolutionException, TaskPrepareException {

        IObject testQuery = mock(IObject.class);

        Key ioBjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(ioBjectKey);
        when(IOC.resolve(ioBjectKey)).thenThrow(new ResolutionException(""));

        testTask.prepare(testQuery);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenGetTokenThrowException() throws ResolutionException, ReadValueException, InvalidArgumentException {

        IObject testQuery = mock(IObject.class);

        IObject futureCriteriaObject = mock(IObject.class);
        Key ioBjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(ioBjectKey);
        when(IOC.resolve(ioBjectKey)).thenReturn(futureCriteriaObject);

        when(tokenField.in(testQuery)).thenThrow(new ReadValueException());

        try {
            testTask.prepare(testQuery);
        } catch (TaskPrepareException e) {

            verifyStatic();
            Keys.getOrAdd(IObject.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(ioBjectKey);

            verify(tokenField).in(testQuery);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenSetEqThrowException() throws ResolutionException, ReadValueException, InvalidArgumentException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        IObject futureCriteriaObject = mock(IObject.class);
        Key ioBjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.getCanonicalName())).thenReturn(ioBjectKey);
        when(IOC.resolve(ioBjectKey)).thenReturn(futureCriteriaObject);

        String token = "tiptoken";

        when(tokenField.in(testQuery)).thenReturn(token);

        doThrow(new ChangeValueException()).when(eqField).out(futureCriteriaObject, token);

        try {
            testTask.prepare(testQuery);
        } catch (TaskPrepareException e) {

            verifyStatic();
            Keys.getOrAdd(IObject.class.getCanonicalName());
            verifyStatic();
            IOC.resolve(ioBjectKey);

            verify(tokenField).in(testQuery);
            verify(eqField).out(futureCriteriaObject, token);
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