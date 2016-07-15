package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.JDBCCompiledQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import utils.SearchQueryHelper;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.reset;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
@SuppressWarnings("unchecked")
public class PSQLSearchTaskTest {
    private static IStorageConnection connection = mock(IStorageConnection.class);
    private static IObject message = mock(IObject.class);
    private static ICompiledQuery compiledQuery = mock(ICompiledQuery.class);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void should_prepareSearchTask() throws Exception {
        reset(compiledQuery, connection);

        IDatabaseTask searchTask = PSQLSearchTask.create(1, 10000);
        ICollectionName collectionName = mock(ICollectionName.class);
        IObject criteria = SearchQueryHelper.createComplexCriteria();

        when(collectionName.toString()).thenReturn("testCollection");

        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);

        searchTask.setConnection(connection);
        searchTask.prepare();
    }

//    @Test
//    public void executeSearchQueryTest() throws Exception {
//        DBSearchTask searchTask = prepareSearchTask();
//
//        String json = "testJSON";
//        PreparedStatement preparedStatement = mock(PreparedStatement.class);
//        ResultSet resultSet = mock(ResultSet.class);
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(compiledQuery.execute()).thenReturn(true);
//        when(resultSet.next()).thenReturn(true).thenReturn(false);
//        when(resultSet.getString(eq("document"))).thenReturn(json);
//        when(resultSet.getLong(eq("id"))).thenReturn(123L);
//
//        IKey iObjectKey = mock(IKey.class);
//        IKey fieldNameKey = mock(IKey.class);
//        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iObjectKey);
//        when(Keys.getOrAdd(IFieldName.class.toString())).thenReturn(fieldNameKey);
//
//        IObject searchResultObj = mock(IObject.class);
//        when(IOC.resolve(eq(iObjectKey), eq(json))).thenReturn(searchResultObj);
//
//        IFieldName idFN = mock(IFieldName.class);
//        when(IOC.resolve(eq(fieldNameKey), eq("id"))).thenReturn(idFN);
//
//        searchTask.execute();
//
//        verify(compiledQuery, times(1)).execute();
//        verify(preparedStatement, times(1)).executeQuery();
//        verify(resultSet, times(2)).next();
//        verify(resultSet, times(1)).getString(eq("document"));
//        verify(resultSet, times(1)).getLong(eq("id"));
//        verify(queryMessage).setSearchResult(Collections.singletonList(searchResultObj));
//
//        verifyStatic(times(1));
//        IOC.resolve(eq(iObjectKey), eq(json));
//        verifyStatic(times(1));
//        IOC.resolve(eq(fieldNameKey), eq("id"));
//    }

/*    private void prepareSearchQueryTest(DBSearchTask searchTask) throws Exception {
        searchTask.setConnection(connection);
        searchTask.prepare(message);

        verify(queryMessage, times(1)).getCollection();
        verify(queryMessage, times(1)).getCriteria();
        verify(queryMessage, times(1)).getOrderBy();
        verify(queryMessage, times(1)).setCachedQuery(eq(bufferedQuery));

        verifyStatic(times(1));
        IOC.resolve(eq(wrapperKey), eq(message));
        verifyStatic(times(1));
        IOC.resolve(eq(cachedQueryKey), eq(compiledQuery), anyObject());

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

        field(PSQLSearchTask.class, "query").set(searchTask, compiledQuery);
        field(PSQLSearchTask.class, "message").set(searchTask, queryMessage);

        return searchTask;
    }*/
}
