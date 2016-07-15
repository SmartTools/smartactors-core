package info.smart_tools.smartactors.core.db_tasks.psql.insert;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import utils.TestUtils;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.GeneralDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.psql.delete.PSQLDeleteByIdTask;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.sql.ResultSet;

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
public class PSQLInsertTaskTest {
    private static IStorageConnection connection = mock(IStorageConnection.class);
    private static ICompiledQuery compiledQuery = mock(ICompiledQuery.class);
    private static IObject message = mock(IObject.class);
    private static IField collectionField = Mockito.mock(IField.class);
    private static IField documentField = Mockito.mock(IField.class);
    private static IField collectionIdField = mock(IField.class);

    private static final String COLLECTION_NAME = "testCollection";

    @BeforeClass
    public static void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("collection"))).thenReturn(collectionField);
        when(IOC.resolve(eq(fieldKey), eq("document"))).thenReturn(documentField);
        when(IOC.resolve(eq(fieldKey), eq("documentId"))).thenReturn(documentField);
        // Static block init.
        IField init = DBQueryFields.COLLECTION;
    }

    @Test
    public void should_PrepareInsertTask() throws Exception {
        reset(connection, collectionField, documentField);

        IDatabaseTask insertTask = PSQLInsertTask.create();
        CollectionName collectionName = mock(CollectionName.class);
        IObject document = mock(IObject.class);

        when(connection.getId()).thenReturn("testConnectionId");
        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);
        when(collectionName.toString()).thenReturn(COLLECTION_NAME);
        when(collectionField.in(message)).thenReturn(collectionName);
        when(document.serialize()).thenReturn("{\"name\":\"John\", \"status\":\"ok\"}");
        when(documentField.in(message)).thenReturn(document);

        insertTask.setConnection(connection);
        insertTask.prepare(message);

        verify(collectionField).in(message);
        verify(documentField, times(2)).in(message);
        verify(connection).compileQuery(anyObject());
        verify(connection).getId();

        Field[] fields = fields(PSQLDeleteByIdTask.class);
        assertEquals(TestUtils.getValue(fields, insertTask, "query"), compiledQuery);
        assertEquals(TestUtils.getValue(fields, insertTask, "message"), message);
        assertEquals(TestUtils.getValue(fields, insertTask, "executable"), true);
    }

    @Test
    public void should_ExecuteInsertTask() throws Exception {
        reset(connection, collectionField, collectionIdField, compiledQuery, documentField);

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq(COLLECTION_NAME + "Id"))).thenReturn(collectionIdField);

        IDatabaseTask insertTask = PSQLInsertTask.create();
        CollectionName collectionName = mock(CollectionName.class);
        IObject document = mock(IObject.class);
        ResultSet resultSet = mock(ResultSet.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        Long id = 123L;

        when(collectionName.toString()).thenReturn(COLLECTION_NAME);
        when(collectionField.in(message)).thenReturn(collectionName);
        when(document.serialize()).thenReturn("{\"name\":\"John\", \"status\":\"ok\"}");
        when(documentField.in(message)).thenReturn(document);
        when(resultSet.first()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getLong("id")).thenReturn(id);
        when(compiledQuery.executeQuery()).thenReturn(resultSet);
        doNothing().when(collectionIdField).out(eq(document), idCaptor.capture());

        field(PSQLInsertTask.class, "query").set(insertTask, compiledQuery);
        field(PSQLInsertTask.class, "message").set(insertTask, message);
        field(PSQLInsertTask.class, "executable").set(insertTask, true);

        insertTask.execute();

        verify(compiledQuery).executeQuery();
        assertEquals(idCaptor.getValue(), id);
        Field[] fields = fields(GeneralDatabaseTask.class);
        assertEquals(TestUtils.getValue(fields, insertTask, "executable"), false);
    }

    @Test()
    public void should_ThrowsException_WithReason_Of_TaskDidNotPreparedBeforeExecute() throws Exception {
        try {
            IDatabaseTask insertTask = PSQLInsertTask.create();
            insertTask.setConnection(connection);
            insertTask.execute();
        } catch (TaskExecutionException e) {
            assertEquals(e.getMessage(), "Prepare task before execution!");
            return;
        }

        throw new Exception("Test failed: exception didn't invoked!");
    }
}
