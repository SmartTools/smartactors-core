package info.smart_tools.smartactors.core.db_task.delete.psql;

import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_task.delete.DBDeleteTask;
import info.smart_tools.smartactors.core.db_task.delete.wrappers.DeletionQuery;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.JDBCCompiledQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class })
@SuppressWarnings("unchecked")
public class PSQLDeleteTaskTest {
    private String collectionName = "testCollection";
    DeletionQuery queryMessage;
    JDBCCompiledQuery compiledQuery;

    @Before
    public void setUp() throws ResolutionException {
        queryMessage = mock(DeletionQuery.class);
        when(queryMessage.getCollectionName()).thenReturn(collectionName);
        when(queryMessage.countDocumentIds()).thenReturn(3);
        when(queryMessage.getDocumentIds(0)).thenReturn(0L);
        when(queryMessage.getDocumentIds(1)).thenReturn(1L);
        when(queryMessage.getDocumentIds(2)).thenReturn(2L);

        compiledQuery = mock(JDBCCompiledQuery.class);

        mockStatic(IOC.class);
    }

    @Test
    public void prepareDeletionQueryTest() throws Exception {
        StorageConnection connection = mock(StorageConnection.class);
        DBDeleteTask deleteTask = PSQLDeleteTask.create();
        deleteTask.setConnection(connection);

        IKey wrapperKey = mock(IKey.class);
        IKey queryKey = mock(IKey.class);
        when(Keys.getOrAdd(DeletionQuery.class.toString())).thenReturn(wrapperKey);
        when(Keys.getOrAdd(CompiledQuery.class.toString())).thenReturn(queryKey);
        when(IOC.resolve(eq(wrapperKey), anyObject())).thenReturn(queryMessage);
        when(IOC.resolve(eq(queryKey), eq(connection), eq(PSQLDeleteTask.class.toString()), anyObject()))
                .thenReturn(compiledQuery);

        IObject deletionQuery = mock(IObject.class);
        deleteTask.prepare(deletionQuery);

        verify(queryMessage, times(2)).countDocumentIds();
        verifyStatic(times(1));
        IOC.resolve(eq(wrapperKey), eq(deletionQuery));

        Field queryField  = deleteTask.getClass().getDeclaredField("query");
        queryField.setAccessible(true);
        assertEquals(queryField.get(deleteTask), compiledQuery);

        Field messageField = deleteTask.getClass().getDeclaredField("message");
        messageField.setAccessible(true);
        assertEquals(messageField.get(deleteTask), queryMessage);
    }

    @Test
    public void executeDeletionQueryTest() throws Exception {
        JDBCCompiledQuery compiledQuery = mock(JDBCCompiledQuery.class);
        DBDeleteTask deleteTask = PSQLDeleteTask.create();
        PreparedStatement prepareStatement = mock(PreparedStatement.class);

        when(compiledQuery.getPreparedStatement()).thenReturn(prepareStatement);
        when(prepareStatement.executeUpdate()).thenReturn(3);

        Field queryField  = deleteTask.getClass().getDeclaredField("query");
        queryField.setAccessible(true);
        queryField.set(deleteTask, compiledQuery);

        Field messageField = deleteTask.getClass().getDeclaredField("message");
        messageField.setAccessible(true);
        messageField.set(deleteTask, queryMessage);

        deleteTask.execute();

        verify(compiledQuery, times(1)).getPreparedStatement();
        verify(prepareStatement, times(1)).executeUpdate();
    }

    @Test(expected = TaskPrepareException.class)
    public void should_ThrowsException_WithReasonOf_EmptyIdsList() throws Exception {
        StorageConnection connection = mock(StorageConnection.class);
        DBDeleteTask deleteTask = PSQLDeleteTask.create();
        deleteTask.setConnection(connection);

        DeletionQuery message = mock(DeletionQuery.class);
        when(message.getCollectionName()).thenReturn(collectionName);
        when(message.countDocumentIds()).thenReturn(0);

        IKey wrapperKey = mock(IKey.class);
        when(Keys.getOrAdd(DeletionQuery.class.toString())).thenReturn(wrapperKey);
        when(IOC.resolve(eq(wrapperKey), anyObject())).thenReturn(message);

        IObject deletionQuery = mock(IObject.class);
        deleteTask.prepare(deletionQuery);
    }

    @Test(expected = TaskExecutionException.class)
    public void should_ThrowsException_WithReason_Of_TaskDidNotPreparedBeforeExecute() throws Exception {
        StorageConnection connection = mock(StorageConnection.class);
        DBDeleteTask deleteTask = PSQLDeleteTask.create();
        deleteTask.setConnection(connection);

        deleteTask.execute();
    }

    @Test(expected = TaskExecutionException.class)
    public void should_ThrowsException_WithReason_Of_WrongCountOfDocumentsIsDeleted() throws Exception {
        JDBCCompiledQuery compiledQuery = mock(JDBCCompiledQuery.class);
        DBDeleteTask deleteTask = PSQLDeleteTask.create();
        PreparedStatement prepareStatement = mock(PreparedStatement.class);

        when(compiledQuery.getPreparedStatement()).thenReturn(prepareStatement);
        when(prepareStatement.executeUpdate()).thenReturn(2);

        Field queryField  = deleteTask.getClass().getDeclaredField("query");
        queryField.setAccessible(true);
        queryField.set(deleteTask, compiledQuery);

        Field messageField = deleteTask.getClass().getDeclaredField("message");
        messageField.setAccessible(true);
        messageField.set(deleteTask, queryMessage);

        deleteTask.execute();
    }
}

