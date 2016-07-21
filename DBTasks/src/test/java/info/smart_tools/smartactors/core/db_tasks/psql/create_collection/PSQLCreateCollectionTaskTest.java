package info.smart_tools.smartactors.core.db_tasks.psql.create_collection;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import utils.TestUtils;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.GeneralDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({ IOC.class, Keys.class })
@RunWith(PowerMockRunner.class)
@SuppressWarnings("unchecked")
public class PSQLCreateCollectionTaskTest {
    private static ICompiledQuery compiledQuery = mock(ICompiledQuery.class);
    private static IObject message = mock(IObject.class);
    private static IStorageConnection connection = mock(IStorageConnection.class);
    private static IField collectionField = mock(IField.class);
    private static IField indexesField = mock(IField.class);

    @BeforeClass
    public static void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(connection.compileQuery(any())).thenReturn(compiledQuery);
        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(connection.getId()).thenReturn("testConnectionId");
        when(IOC.resolve(eq(fieldKey), eq("collection"))).thenReturn(collectionField);
        when(IOC.resolve(eq(fieldKey), eq("indexes"))).thenReturn(indexesField);
        // Static block init.
        IField init = DBQueryFields.COLLECTION;
    }

    @Test
    public void should_PrepareCreateCollectionTask() throws Exception {
        reset(collectionField, indexesField, connection);

        IDatabaseTask createCollectionTask = PSQLCreateCollectionTask.create();
        ICollectionName collectionName = mock(ICollectionName.class);
        Map<String, String> indexes = new HashMap<>();
        indexes.put("meta.tags", "tags");

        when(indexesField.in(message)).thenReturn(indexes);
        when(collectionName.toString()).thenReturn("testCollection");
        when(collectionField.in(message)).thenReturn(collectionName);

        createCollectionTask.setConnection(connection);
        createCollectionTask.prepare(message);

        verify(collectionField, times(1)).in(message);
        verify(indexesField, times(1)).in(message);
        verify(connection).compileQuery(any());
    }

    @Test
    public void should_ExecuteCreateCollectionTask() throws Exception {
        IDatabaseTask createCollectionTask = PSQLCreateCollectionTask.create();

        field(PSQLCreateCollectionTask.class, "query").set(createCollectionTask, compiledQuery);
        field(PSQLCreateCollectionTask.class, "message").set(createCollectionTask, message);
        field(PSQLCreateCollectionTask.class, "executable").set(createCollectionTask, true);

        createCollectionTask.execute();

        verify(compiledQuery).execute();
        Field[] fields = fields(GeneralDatabaseTask.class);
        assertEquals(TestUtils.getValue(fields, createCollectionTask, "executable"), false);
    }

    @Test()
    public void should_ThrowsException_WithReason_InvalidMessage() throws Exception {
        IDatabaseTask createCollectionTask = PSQLCreateCollectionTask.create();

        when(collectionField.in(message)).thenReturn(null);
        when(indexesField.in(message)).thenReturn(null);

        try {
            createCollectionTask.setConnection(connection);
            createCollectionTask.prepare(message);
        } catch (TaskPrepareException e) {
            assertEquals(e.getMessage(), "Invalid query message!");
            return;
        }

        throw new Exception("Test failed: exception didn't invoked!");
    }

    @Test()
    public void should_ThrowsException_WithReason_Of_TaskDidNotPreparedBeforeExecute() throws Exception {
        try {
            IDatabaseTask createCollectionTask = PSQLDeleteByIdTask.create();
            createCollectionTask.setConnection(connection);
            createCollectionTask.execute();
        } catch (TaskExecutionException e) {
            assertEquals(e.getMessage(), "Prepare task before execution!");
            return;
        }

        throw new Exception("Test failed: exception didn't invoked!");
    }


    @Test
    public void should_SetConnection() throws Exception {
        IDatabaseTask createCollectionTask = PSQLCreateCollectionTask.create();

        when(connection.getId()).thenReturn("testConnectionId");

        IStorageConnection storageConnectionBefore =
                (IStorageConnection) field(PSQLCreateCollectionTask.class, "connection").get(createCollectionTask);
        createCollectionTask.setConnection(connection);
        IStorageConnection storageConnectionAfter =
                (IStorageConnection) field(PSQLCreateCollectionTask.class, "connection").get(createCollectionTask);

        assertNull(storageConnectionBefore);
        assertNotNull(storageConnectionAfter);
        assertEquals(connection, storageConnectionAfter);
    }
}
