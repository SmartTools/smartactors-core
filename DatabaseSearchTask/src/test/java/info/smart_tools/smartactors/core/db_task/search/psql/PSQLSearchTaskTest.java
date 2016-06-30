package info.smart_tools.smartactors.core.db_task.search.psql;

import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_task.search.DBSearchTask;
import info.smart_tools.smartactors.core.db_task.search.utils.IBufferedQuery;
import info.smart_tools.smartactors.core.db_task.search.wrappers.ISearchQuery;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
//TODO commented by AKutalev, reason: now IObject doesn't contain iterator
//import info.smart_tools.smartactors.core.iobject.IObjectIterator;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.JDBCCompiledQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
@SuppressWarnings("unchecked")
public class PSQLSearchTaskTest {
    private StorageConnection connection;
    private IObject message;
    private ISearchQuery queryMessage;
    private JDBCCompiledQuery compiledQuery;
    private IBufferedQuery bufferedQuery;

    private IKey wrapperKey;
    private IKey bufferedQueryKey;

    @Before
    public void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        connection = mock(StorageConnection.class);
        message = mock(IObject.class);
        queryMessage = mock(ISearchQuery.class);
        when(queryMessage.getCollectionName()).thenReturn("testCollection");
        when(queryMessage.countOrderBy()).thenReturn(0);
        compiledQuery = mock(JDBCCompiledQuery.class);
        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);

        IObject criteria = mock(IObject.class);
//TODO commented by AKutalev, reason: now IObject doesn't contain iterator
//        IObjectIterator iterator = mock(IObjectIterator.class);
//        when(iterator.next()).thenReturn(false);
//        when(criteria.iterator()).thenReturn(iterator);

        when(queryMessage.getCriteria()).thenReturn(criteria);
        when(queryMessage.getPageNumber()).thenReturn(1);
        when(queryMessage.getPageSize()).thenReturn(2);

        wrapperKey = mock(IKey.class);
        when(Keys.getOrAdd(ISearchQuery.class.toString())).thenReturn(wrapperKey);
        when(IOC.resolve(eq(wrapperKey), eq(message))).thenReturn(queryMessage);

        bufferedQuery = mock(IBufferedQuery.class);
        when(bufferedQuery.getCompiledQuery()).thenReturn(compiledQuery);
        when(bufferedQuery.getParametersSetters()).thenReturn(new ArrayList<>());

        bufferedQueryKey = mock(IKey.class);
        when(Keys.getOrAdd(IBufferedQuery.class.toString())).thenReturn(bufferedQueryKey);
        when(IOC.resolve(eq(bufferedQueryKey), eq(compiledQuery), anyObject())).thenReturn(bufferedQuery);

        // For execute.

    }

    @Test
    public void prepareSearchQueryTest_WhenBufferedQueryIsNotExist() throws Exception {
        DBSearchTask searchTask = PSQLSearchTask.create();
        searchTask.setConnection(connection);
        when(queryMessage.getBufferedQuery()).thenReturn(Optional.empty());
        prepareSearchQueryTest(searchTask);
    }

    @Test
    public void prepareSearchQueryTest_WhenBufferedQueryIsExist() throws Exception {
        DBSearchTask searchTask = PSQLSearchTask.create();
        searchTask.setConnection(connection);
        when(queryMessage.getBufferedQuery()).thenReturn(Optional.of(bufferedQuery));
        prepareSearchQueryTest(searchTask);
    }

    @Test
    public void executeSearchQueryTest() throws Exception {
        DBSearchTask searchTask = prepareSearchTask();

        String json = "testJSON";
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(compiledQuery.getPreparedStatement()).thenReturn(preparedStatement);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(eq("document"))).thenReturn(json);
        when(resultSet.getLong(eq("id"))).thenReturn(123L);

        IKey iObjectKey = mock(IKey.class);
        IKey fieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iObjectKey);
        when(Keys.getOrAdd(IFieldName.class.toString())).thenReturn(fieldNameKey);

        IObject searchResultObj = mock(IObject.class);
        when(IOC.resolve(eq(iObjectKey), eq(json))).thenReturn(searchResultObj);

        IFieldName idFN = mock(IFieldName.class);
        when(IOC.resolve(eq(fieldNameKey), eq("id"))).thenReturn(idFN);

        searchTask.execute();

        verify(compiledQuery, times(1)).getPreparedStatement();
        verify(preparedStatement, times(1)).executeQuery();
        verify(resultSet, times(2)).next();
        verify(resultSet, times(1)).getString(eq("document"));
        verify(resultSet, times(1)).getLong(eq("id"));
        verify(queryMessage).setSearchResult(Collections.singletonList(searchResultObj));

        verifyStatic(times(1));
        IOC.resolve(eq(iObjectKey), eq(json));
        verifyStatic(times(1));
        IOC.resolve(eq(fieldNameKey), eq("id"));
    }

    private void prepareSearchQueryTest(DBSearchTask searchTask) throws Exception {
        searchTask.setConnection(connection);
        searchTask.prepare(message);

        verify(connection, times(1)).compileQuery(anyObject());
        verify(queryMessage, times(1)).getCollectionName();
        verify(queryMessage, times(1)).getCriteria();
        verify(queryMessage, times(1)).countOrderBy();
        verify(queryMessage, times(1)).setBufferedQuery(eq(bufferedQuery));

        verifyStatic(times(1));
        IOC.resolve(eq(wrapperKey), eq(message));
        verifyStatic(times(1));
        IOC.resolve(eq(bufferedQueryKey), eq(compiledQuery), anyObject());

        Field connectionField = searchTask.getClass().getDeclaredField("connection");
        connectionField.setAccessible(true);
        assertEquals(connectionField.get(searchTask), connection);

        Field queryField = searchTask.getClass().getDeclaredField("query");
        queryField.setAccessible(true);
        assertEquals(queryField.get(searchTask), compiledQuery);

        Field messageField = searchTask.getClass().getDeclaredField("message");
        messageField.setAccessible(true);
        assertEquals(messageField.get(searchTask), queryMessage);
    }

    private DBSearchTask prepareSearchTask() throws Exception {
        DBSearchTask searchTask = PSQLSearchTask.create();
        searchTask.setConnection(connection);

        Field queryField = searchTask.getClass().getDeclaredField("query");
        queryField.setAccessible(true);
        queryField.set(searchTask, compiledQuery);

        Field messageField = searchTask.getClass().getDeclaredField("message");
        messageField.setAccessible(true);
        messageField.set(searchTask, queryMessage);

        return searchTask;
    }
}
