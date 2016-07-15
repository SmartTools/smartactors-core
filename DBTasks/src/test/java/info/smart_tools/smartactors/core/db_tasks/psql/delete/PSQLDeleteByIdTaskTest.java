package info.smart_tools.smartactors.core.db_tasks.psql.delete;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import utils.TestUtils;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.GeneralDatabaseTask;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
@SuppressWarnings("unchecked")
public class PSQLDeleteByIdTaskTest {
    private static IStorageConnection connection = mock(IStorageConnection.class);
    private static ICompiledQuery compiledQuery = mock(ICompiledQuery.class);
    private static IObject message = mock(IObject.class);
    private static IField collectionField = Mockito.mock(IField.class);
    private static IField documentIdField = Mockito.mock(IField.class);

    @BeforeClass
    public static void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("collection"))).thenReturn(collectionField);
        when(IOC.resolve(eq(fieldKey), eq("documentId"))).thenReturn(documentIdField);
        // Static block init.
        IField init = DBQueryFields.COLLECTION;
    }

    @Test
    public void should_PrepareDeletionByIdTask() throws Exception {
        reset(collectionField, documentIdField, connection);

        IDatabaseTask deleteTask = PSQLDeleteByIdTask.create();
        CollectionName collectionName = mock(CollectionName.class);

        when(collectionName.toString()).thenReturn("testCollection");
        when(collectionField.in(message)).thenReturn(collectionName);
        when(documentIdField.in(message)).thenReturn(1123L);
        when(connection.getId()).thenReturn("testConnectionId");
        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);

        deleteTask.setConnection(connection);
        deleteTask.prepare(message);

        verify(collectionField).in(message);
        verify(documentIdField, times(2)).in(message);
        verify(connection).compileQuery(anyObject());
        verify(connection).getId();

        Field[] fields = fields(PSQLDeleteByIdTask.class);
        assertEquals(TestUtils.getValue(fields, deleteTask, "query"), compiledQuery);
        assertEquals(TestUtils.getValue(fields, deleteTask, "message"), message);
    }

    @Test
    public void should_ExecuteDeletionQueryTest() throws Exception {
        reset(compiledQuery);

        IDatabaseTask deleteTask = PSQLDeleteByIdTask.create();

        when(compiledQuery.executeUpdate()).thenReturn(1);

        field(PSQLDeleteByIdTask.class, "query").set(deleteTask, compiledQuery);
        field(PSQLDeleteByIdTask.class, "message").set(deleteTask, message);
        field(PSQLDeleteByIdTask.class, "executable").set(deleteTask, true);

        deleteTask.execute();

        verify(compiledQuery).executeUpdate();
        Field[] fields = fields(GeneralDatabaseTask.class);
        assertEquals(TestUtils.getValue(fields, deleteTask, "executable"), false);
    }

    @Test()
    public void should_ThrowsException_WithReason_Of_TaskDidNotPreparedBeforeExecute() throws Exception {
        try {
            IDatabaseTask deleteTask = PSQLDeleteByIdTask.create();
            deleteTask.setConnection(connection);
            deleteTask.execute();
        } catch (TaskExecutionException e) {
            assertEquals(e.getMessage(), "Prepare task before execution!");
            return;
        }

        throw new Exception("Test failed: exception didn't invoked!");
    }

    @Test()
    public void should_ThrowsException_WithReason_Of_WrongCountOfDocumentsIsDeleted() throws Exception {
        reset(compiledQuery);

        IDatabaseTask deleteTask = PSQLDeleteByIdTask.create();

        when(compiledQuery.executeUpdate()).thenReturn(2);

        field(PSQLDeleteByIdTask.class, "query").set(deleteTask, compiledQuery);
        field(PSQLDeleteByIdTask.class, "message").set(deleteTask, message);
        field(PSQLDeleteByIdTask.class, "executable").set(deleteTask, true);

        try {
            deleteTask.execute();
        } catch (TaskExecutionException e) {
            assertEquals(e.getMessage(), "Task execution has been failed because: " +
                    "'Delete query' execution has been failed: " +
                    "wrong count of documents is deleted.");
        }
    }
}

