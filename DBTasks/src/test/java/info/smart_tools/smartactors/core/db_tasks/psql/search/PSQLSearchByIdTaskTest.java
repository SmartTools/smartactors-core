package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.psql.delete.PSQLDeleteByIdTask;
import info.smart_tools.smartactors.core.db_tasks.psql.insert.PSQLInsertTask;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import utils.TestUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.powermock.api.support.membermodification.MemberMatcher.fields;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
@SuppressWarnings("unchecked")
public class PSQLSearchByIdTaskTest {
    private static IStorageConnection connection = mock(IStorageConnection.class);
    private static ICompiledQuery compiledQuery = mock(ICompiledQuery.class);
    private static IObject message = mock(IObject.class);
    private static IField collectionField = mock(IField.class);
    private static IField documentIdField = mock(IField.class);
    private static IField collectionIdField = mock(IField.class);
    private static IField searchResultField = mock(IField.class);

    @BeforeClass
    public static void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("collection"))).thenReturn(collectionField);
        when(IOC.resolve(eq(fieldKey), eq("documentId"))).thenReturn(documentIdField);
        when(IOC.resolve(eq(fieldKey), eq("searchResult"))).thenReturn(searchResultField);

        // Static block init.
        IField init = DBQueryFields.COLLECTION;
        Thread.sleep(100);
    }

    @Test
    public void should_PrepareSearchByIdTask() throws Exception {
        reset(collectionField, documentIdField, connection, compiledQuery);

        IDatabaseTask searchByIdTask = PSQLSearchByIdTask.create();
        CollectionName collectionName = mock(CollectionName.class);

        when(collectionName.toString()).thenReturn("testCollection");
        when(collectionField.in(message)).thenReturn(collectionName);
        when(documentIdField.in(message)).thenReturn(1123L);
        when(connection.getId()).thenReturn("testConnectionId");
        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);

        searchByIdTask.setConnection(connection);
        searchByIdTask.prepare(message);

        verify(collectionField).in(message);
        verify(documentIdField, times(2)).in(message);
        verify(connection).getId();
        verify(connection).compileQuery(anyObject());

        Field[] fields = fields(PSQLDeleteByIdTask.class);
        assertEquals(TestUtils.getValue(fields, searchByIdTask, "query"), compiledQuery);
        assertEquals(TestUtils.getValue(fields, searchByIdTask, "message"), message);
    }

    @Test
    public void should_ExecuteSearchByIdTask() throws Exception {
        reset(compiledQuery, connection, collectionIdField, searchResultField);

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);
        IDatabaseTask searchByIdTask = PSQLSearchByIdTask.create();
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<IObject> searchResultCaptor = ArgumentCaptor.forClass(IObject.class);
        ResultSet resultSet = mock(ResultSet.class);
        IObject searchResult = mock(IObject.class);
        IKey iObjectKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("testCollectionId"))).thenReturn(collectionIdField);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iObjectKey);
        when(IOC.resolve(eq(iObjectKey), eq("'{\"name\":\"John\"}'"))).thenReturn(searchResult);
        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);
        doNothing().when(collectionIdField).out(eq(searchResult), idCaptor.capture());
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(eq("document"))).thenReturn("'{\"name\":\"John\"}'");
        when(resultSet.getLong("id")).thenReturn(11L);
        when(compiledQuery.executeQuery()).thenReturn(resultSet);
        doNothing().when(searchResultField).out(eq(message), searchResultCaptor.capture());
        when(searchResultField.in(message)).thenReturn(Collections.singletonList(searchResult));

        field(PSQLSearchByIdTask.class, "query").set(searchByIdTask, compiledQuery);
        field(PSQLSearchByIdTask.class, "message").set(searchByIdTask, message);
        field(PSQLSearchByIdTask.class, "executable").set(searchByIdTask, true);

        searchByIdTask.execute();

        verify(compiledQuery).executeQuery();
        assertEquals(idCaptor.getValue(), Long.valueOf(11));
        assertEquals(searchResultCaptor.getValue(), searchResult);
    }

    @Test()
    public void should_ThrowsException_WithReason_Of_TaskDidNotPreparedBeforeExecute() throws Exception {
        try {
            IDatabaseTask searchByIdTask = PSQLSearchByIdTask.create();

            field(PSQLDeleteByIdTask.class, "query").set(searchByIdTask, null);
            field(PSQLDeleteByIdTask.class, "message").set(searchByIdTask, null);

            searchByIdTask.setConnection(connection);
            searchByIdTask.execute();
        } catch (TaskExecutionException e) {
            assertEquals(e.getMessage(), "Prepare task before execution!");
            return;
        }

        throw new Exception("Test failed: exception didn't invoked!");
    }
}