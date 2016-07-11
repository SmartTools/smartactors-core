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
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import info.smart_tools.smartactors.core.wrapper_generator.Field;
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

    private Field<Integer> pageNumberField;
    private Field<Integer> pageSizeField;
    private Field<IObject> queryField;
    private Field<String> eqField;
    private Field<String> tokenField;

    @Before
    public void prepare () throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        targetTask = mock(IDatabaseTask.class);


        pageNumberField = mock(Field.class);
        pageSizeField = mock(Field.class);
        queryField = mock(Field.class);
        eqField = mock(Field.class);
        tokenField = mock(Field.class);

        Key fieldNameKey = mock(Key.class);
        when(Keys.getOrAdd(IFieldName.class.toString())).thenReturn(fieldNameKey);

        String pageNumberBindingPath = "pageNumber";
        when(IOC.resolve(fieldNameKey, "pageNumber")).thenReturn(pageNumberBindingPath);
        whenNew(Field.class).withArguments(pageNumberBindingPath).thenReturn(pageNumberField);

        String pageSizeBindingPath = "pageSize";
        when(IOC.resolve(fieldNameKey, "pageSize")).thenReturn(pageSizeBindingPath);
        whenNew(Field.class).withArguments(pageSizeBindingPath).thenReturn(pageSizeField);

        String queryBindingPath = "query";
        when(IOC.resolve(fieldNameKey, "query")).thenReturn(queryBindingPath);
        whenNew(Field.class).withArguments(queryBindingPath).thenReturn(queryField);

        String eqBindingPath = "eq";
        when(IOC.resolve(fieldNameKey, "$eq")).thenReturn(eqBindingPath);
        whenNew(Field.class).withArguments(eqBindingPath).thenReturn(eqField);

        String tokenBindingPath = "token";
        when(IOC.resolve(fieldNameKey, "token")).thenReturn(tokenBindingPath);
        whenNew(Field.class).withArguments(tokenBindingPath).thenReturn(tokenField);


        testTask = new GetAsyncOperationTask(targetTask);

        verifyStatic(times(5));
        Keys.getOrAdd(IFieldName.class.toString());

        verifyStatic();
        IOC.resolve(fieldNameKey, "pageNumber");
        verifyNew(Field.class).withArguments(pageNumberBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "pageSize");
        verifyNew(Field.class).withArguments(pageSizeBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "query");
        verifyNew(Field.class).withArguments(queryBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "$eq");
        verifyNew(Field.class).withArguments(eqBindingPath);

        verifyStatic();
        IOC.resolve(fieldNameKey, "token");
        verifyNew(Field.class).withArguments(tokenBindingPath);
    }

    @Test
    public void MustCorrectPrepareQuery() throws ReadValueException, InvalidArgumentException, ResolutionException, TaskPrepareException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        IObject futureCriteriaObject = mock(IObject.class);
        Key ioBjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(ioBjectKey);
        when(IOC.resolve(ioBjectKey)).thenReturn(futureCriteriaObject);

        String token = "tiptoken";

        when(tokenField.out(testQuery)).thenReturn(token);

        testTask.prepare(testQuery);

        verifyStatic();
        Keys.getOrAdd(IObject.class.toString());
        verifyStatic();
        IOC.resolve(ioBjectKey);

        verify(tokenField).out(testQuery);
        verify(eqField).in(futureCriteriaObject, token);

        verify(pageNumberField).in(testQuery, 1);
        verify(pageSizeField).in(testQuery, 1);
        verify(queryField).in(testQuery, futureCriteriaObject);

        verify(targetTask).prepare(testQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryWhenKeysGetOrAddThrowException() throws ResolutionException, TaskPrepareException {

        IObject testQuery = mock(IObject.class);

        when(Keys.getOrAdd(IObject.class.toString())).thenThrow(new ResolutionException(""));

        testTask.prepare(testQuery);
    }

    @Test(expected = TaskPrepareException.class)
    public void MustInCorrectPrepareQueryWhenIOCResolveThrowException() throws ResolutionException, TaskPrepareException {

        IObject testQuery = mock(IObject.class);

        Key ioBjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(ioBjectKey);
        when(IOC.resolve(ioBjectKey)).thenThrow(new ResolutionException(""));

        testTask.prepare(testQuery);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenGetTokenThrowException() throws ResolutionException, ReadValueException, InvalidArgumentException {

        IObject testQuery = mock(IObject.class);

        IObject futureCriteriaObject = mock(IObject.class);
        Key ioBjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(ioBjectKey);
        when(IOC.resolve(ioBjectKey)).thenReturn(futureCriteriaObject);

        when(tokenField.out(testQuery)).thenThrow(new ReadValueException());

        try {
            testTask.prepare(testQuery);
        } catch (TaskPrepareException e) {

            verifyStatic();
            Keys.getOrAdd(IObject.class.toString());
            verifyStatic();
            IOC.resolve(ioBjectKey);

            verify(tokenField).out(testQuery);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectPrepareQueryWhenSetEqThrowException() throws ResolutionException, ReadValueException, InvalidArgumentException, ChangeValueException {

        IObject testQuery = mock(IObject.class);

        IObject futureCriteriaObject = mock(IObject.class);
        Key ioBjectKey = mock(Key.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(ioBjectKey);
        when(IOC.resolve(ioBjectKey)).thenReturn(futureCriteriaObject);

        String token = "tiptoken";

        when(tokenField.out(testQuery)).thenReturn(token);

        doThrow(new ChangeValueException()).when(eqField).in(futureCriteriaObject, token);

        try {
            testTask.prepare(testQuery);
        } catch (TaskPrepareException e) {

            verifyStatic();
            Keys.getOrAdd(IObject.class.toString());
            verifyStatic();
            IOC.resolve(ioBjectKey);

            verify(tokenField).out(testQuery);
            verify(eqField).in(futureCriteriaObject, token);
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