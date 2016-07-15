package info.smart_tools.smartactors.core.db_tasks.psql.update;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import utils.TestUtils;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.GeneralDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.psql.delete.PSQLDeleteByIdTask;
import info.smart_tools.smartactors.core.db_tasks.psql.insert.PSQLInsertTask;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.powermock.api.support.membermodification.MemberMatcher.fields;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
@SuppressWarnings("unchecked")
public class PSQLUpdateTaskTest {
    private static IStorageConnection connection = mock(IStorageConnection.class);
    private static ICompiledQuery compiledQuery = mock(ICompiledQuery.class);
    private static IObject message = mock(IObject.class);

    private static IField collectionField = Mockito.mock(IField.class);
    private static IField documentField = Mockito.mock(IField.class);
    private static IField documentIdField = Mockito.mock(IField.class);
    private static IField collectionIdField= mock(IField.class);


    @BeforeClass
    public static void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("collection"))).thenReturn(collectionField);
        when(IOC.resolve(eq(fieldKey), eq("document"))).thenReturn(documentField);
        when(IOC.resolve(eq(fieldKey), eq("documentId"))).thenReturn(documentIdField);
        when(IOC.resolve(eq(fieldKey), eq("testCollectionId"))).thenReturn(collectionIdField);
        // Static block init.
        IField init = DBQueryFields.COLLECTION;
    }

    @Test
    public void should_PrepareUpdateTask() throws Exception {
        reset(collectionField, collectionIdField, documentField, connection);

        IDatabaseTask updateTask = PSQLUpdateTask.create();
        CollectionName collectionName = mock(CollectionName.class);
        IObject document = mock(IObject.class);

        when(collectionName.toString()).thenReturn("testCollection");
        when(collectionField.in(message)).thenReturn(collectionName);
        when(document.serialize()).thenReturn("{\"name\":\"John\", \"status\":\"ok\"}");
        when(collectionIdField.in(document)).thenReturn(123L);
        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);
        when(documentField.in(message)).thenReturn(document);
        when(connection.getId()).thenReturn("testConnectionId");

        updateTask.setConnection(connection);
        updateTask.prepare(message);

        verify(collectionField, times(2)).in(message);
        verify(documentField, times(2)).in(message);
        verify(connection).compileQuery(anyObject());
        verify(connection).getId();

        Field[] fields = fields(GeneralDatabaseTask.class);
        assertEquals(TestUtils.getValue(fields, updateTask, "query"), compiledQuery);
        assertEquals(TestUtils.getValue(fields, updateTask, "message"), message);
        assertEquals(TestUtils.getValue(fields, updateTask, "executable"), true);
    }

    @Test
    public void should_ExecuteUpdateTask() throws Exception {
        reset(compiledQuery);

        IDatabaseTask updateTask = PSQLUpdateTask.create();

        when(compiledQuery.executeUpdate()).thenReturn(1);

        field(PSQLInsertTask.class, "query").set(updateTask, compiledQuery);
        field(PSQLInsertTask.class, "message").set(updateTask, message);
        field(PSQLInsertTask.class, "executable").set(updateTask, true);

        updateTask.execute();

        verify(compiledQuery).executeUpdate();
        Field[] fields = fields(GeneralDatabaseTask.class);
        assertEquals(TestUtils.getValue(fields, updateTask, "executable"), false);
    }

    @Test()
    public void should_ThrowsException_WithReason_Of_TaskDidNotPreparedBeforeExecute() throws Exception {
        IDatabaseTask updateTask = PSQLUpdateTask.create();

        field(PSQLDeleteByIdTask.class, "query").set(updateTask, null);
        field(PSQLDeleteByIdTask.class, "message").set(updateTask, null);
        field(PSQLInsertTask.class, "executable").set(updateTask, true);

        try {
            updateTask.setConnection(connection);
            updateTask.execute();
        } catch (TaskExecutionException e) {
            assertEquals(e.getMessage(), "Prepare task before execution!");
            return;
        }

        throw new Exception("Test failed.");
    }
}
