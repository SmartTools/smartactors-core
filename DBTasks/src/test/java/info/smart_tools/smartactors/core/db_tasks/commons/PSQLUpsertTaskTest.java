package info.smart_tools.smartactors.core.db_tasks.commons;

import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import utils.TestUtils;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.powermock.api.support.membermodification.MemberMatcher.fields;

@PrepareForTest({ IOC.class, Keys.class })
@RunWith(PowerMockRunner.class)
public class PSQLUpsertTaskTest {
    private static IDatabaseTask insertTask = mock(IDatabaseTask.class);
    private static IDatabaseTask updateTask = mock(IDatabaseTask.class);
    private static IStorageConnection connection = mock(IStorageConnection.class);
    private static IObject message = mock(IObject.class);

    private static IField collectionField = mock(IField.class);
    private static IField documentIdField = mock(IField.class);
    private static IField documentField = mock(IField.class);
    private static IField collectionIdField = mock(IField.class);


    public static class TestDBUpsertTask extends DBUpsertTask {
        public TestDBUpsertTask() {
            super();
            setInsertTask(insertTask);
            setUpdatetTask(updateTask);
        }
    }


    @BeforeClass
    public static void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("collection"))).thenReturn(collectionField);
        when(IOC.resolve(eq(fieldKey), eq("document"))).thenReturn(documentField);
        when(IOC.resolve(eq(fieldKey), eq("documentId"))).thenReturn(documentIdField);

        // Static block init.
        IField init = DBQueryFields.COLLECTION;
    }

    @Test
    public void should_PrepareInsertTaskTest() throws Exception {
        reset(collectionField, collectionIdField, connection, documentField);

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);
        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("testCollectionId"))).thenReturn(collectionIdField);

        IDatabaseTask upsertTask = new TestDBUpsertTask();

        IObject document = mock(IObject.class);
        CollectionName collectionName = Mockito.mock(CollectionName.class);

        when(collectionName.toString()).thenReturn("testCollection");
        when(collectionField.in(message)).thenReturn(collectionName);
        when(collectionIdField.in(document)).thenReturn(null);
        when(documentField.in(message)).thenReturn(document);

        upsertTask.setConnection(connection);
        upsertTask.prepare(message);

        verify(collectionField).in(message);
        verify(documentField).in(message);
        verify(collectionIdField).in(document);

        Field[] fields = fields(DBUpsertTask.class);
        assertEquals(TestUtils.getValue(fields, upsertTask, "currentTask"), insertTask);
    }

    @Test
    public void should_PrepareUpdateTask() throws Exception {
        reset(documentField, collectionField, collectionField);

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);
        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("testCollectionId"))).thenReturn(collectionIdField);

        IDatabaseTask upsertTask = new TestDBUpsertTask();

        IObject document = mock(IObject.class);
        CollectionName collectionName = Mockito.mock(CollectionName.class);

        when(collectionName.toString()).thenReturn("testCollection");
        when(collectionField.in(message)).thenReturn(collectionName);
        when(collectionIdField.in(document)).thenReturn("12");
        when(documentField.in(message)).thenReturn(document);

        upsertTask.setConnection(connection);
        upsertTask.prepare(message);

        verify(collectionField).in(message);
        verify(documentField).in(message);

        Field[] fields = fields(DBUpsertTask.class);
        assertEquals(TestUtils.getValue(fields, upsertTask, "currentTask"), updateTask);
    }

    @Test
    public void should_ExecuteUpsertTaskTest() throws Exception {
        reset(collectionField);

        IDatabaseTask upsertTask = new TestDBUpsertTask();
        IDatabaseTask currentTask = mock(IDatabaseTask.class);
        CollectionName collectionName = Mockito.mock(CollectionName.class);

        field(DBUpsertTask.class, "currentTask").set(upsertTask, currentTask);

        when(collectionName.toString()).thenReturn("testCollection");
        when(collectionField.in(message)).thenReturn(collectionName);

        upsertTask.execute();

        verify(currentTask).execute();
        Field[] fields = fields(DBUpsertTask.class);
        assertEquals(TestUtils.getValue(fields, upsertTask, "currentTask"), null);
    }

    @Test
    public void should_ThrowsException_WithReason_Of_TaskDidNotPreparedBeforeExecute() throws Exception {
        try {
            IDatabaseTask upsertTask = new TestDBUpsertTask();
            upsertTask.setConnection(connection);
            upsertTask.execute();
        } catch (TaskExecutionException e) {
            assertEquals(e.getMessage(), "Prepare task before execution!");
            return;
        }

        throw new Exception("Test failed: exception didn't invoked!");
    }
}
