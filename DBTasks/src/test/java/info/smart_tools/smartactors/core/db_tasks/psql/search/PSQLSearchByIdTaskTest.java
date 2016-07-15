package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.psql.delete.PSQLDeleteByIdTask;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.powermock.api.support.membermodification.MemberMatcher.fields;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
@SuppressWarnings("unchecked")
public class PSQLSearchByIdTaskTest {
    private IDatabaseTask searchByIdTask;
    private IStorageConnection connection;
    private ICompiledQuery compiledQuery;
    private IObject message;
    private IField collectionField;
    private IField documentIdField;
    private IField collectionIdField;
    private IField searchResultField;
    private IKey iObjectKey;

    @Before
    public void setUp() throws Exception {
        searchByIdTask = PSQLSearchByIdTask.create();

        connection = mock(IStorageConnection.class);
        compiledQuery = mock(ICompiledQuery.class);
        message = mock(IObject.class);
        collectionField = Mockito.mock(IField.class);
        documentIdField = Mockito.mock(IField.class);
        collectionIdField = mock(IField.class);
        searchResultField = mock(IField.class);

        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);

        CollectionName collectionName = mock(CollectionName.class);
        when(collectionName.toString()).thenReturn("testCollection");

        when(collectionField.in(message)).thenReturn(collectionName);
        when(documentIdField.in(message)).thenReturn(1123L);
        when(connection.getId()).thenReturn("testConnectionId");

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("collection"))).thenReturn(collectionField);
        when(IOC.resolve(eq(fieldKey), eq("documentId"))).thenReturn(documentIdField);
        when(IOC.resolve(eq(fieldKey), eq("testCollectionId"))).thenReturn(collectionIdField);
        when(IOC.resolve(eq(fieldKey), eq("searchResult"))).thenReturn(searchResultField);
    }

    // Bug with static fields mock in DBQueryFields, because prepare and execute tests in one.
    @Test
    public void should_PrepareSearchByIdQueryTest() throws Exception {
        // Prepare task.
        searchByIdTask.setConnection(connection);
        searchByIdTask.prepare(message);

        verify(collectionField).in(message);
        verify(documentIdField, times(2)).in(message);
        verify(connection).compileQuery(anyObject());

        Field[] fields = fields(PSQLDeleteByIdTask.class);
        assertEquals(getValue(fields, searchByIdTask, "query"), compiledQuery);
        assertEquals(getValue(fields, searchByIdTask, "message"), message);

        // Executes task.
        IObject searchResult = mock(IObject.class);
        iObjectKey = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iObjectKey);
        when(IOC.resolve(eq(iObjectKey), eq("'{\"name\":\"John\"}'"))).thenReturn(searchResult);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        doNothing().when(collectionIdField).out(eq(searchResult), idCaptor.capture());

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(eq("document"))).thenReturn("'{\"name\":\"John\"}'");
        when(resultSet.getLong("id")).thenReturn(11L);

        when(compiledQuery.executeQuery()).thenReturn(resultSet);
        ArgumentCaptor<IObject> searchResultCaptor = ArgumentCaptor.forClass(IObject.class);
        doNothing().when(searchResultField).out(eq(message), searchResultCaptor.capture());
        when(searchResultField.in(message)).thenReturn(Collections.singletonList(searchResult));

        searchByIdTask.execute();

        verify(compiledQuery).executeQuery();
        assertEquals(idCaptor.getValue(), Long.valueOf(11));
        assertEquals(searchResultCaptor.getValue(), searchResult);
    }

    @Test()
    public void should_ThrowsException_WithReason_Of_TaskDidNotPreparedBeforeExecute() throws Exception {
        field(PSQLDeleteByIdTask.class, "query").set(searchByIdTask, null);
        field(PSQLDeleteByIdTask.class, "message").set(searchByIdTask, null);

        try {
            searchByIdTask.setConnection(connection);
            searchByIdTask.execute();
        } catch (TaskExecutionException e) {
            assertEquals(e.getMessage(), "Prepare task before execution!");
        }
    }

    private Object getValue(final Field[] fields, final Object obj, final String name) throws IllegalAccessException {
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field.get(obj);
            }
        }

        return null;
    }
}