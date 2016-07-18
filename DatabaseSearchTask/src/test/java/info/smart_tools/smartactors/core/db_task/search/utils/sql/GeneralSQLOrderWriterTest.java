package info.smart_tools.smartactors.core.db_task.search.utils.sql;

import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.core.db_task.search.psql.PSQLFieldPath;
import info.smart_tools.smartactors.core.db_task.search.utils.ISearchQueryWriter;
import info.smart_tools.smartactors.core.db_task.search.wrappers.ISearchQuery;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
@SuppressWarnings("unchecked")
public class GeneralSQLOrderWriterTest {
    private ISearchQueryWriter orderWriter;

    @Before
    public void setUp() {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        orderWriter = GeneralSQLOrderWriter.create();
    }

    @Test
    public void should_WritesORDERClauseIntoQueryStatement() throws Exception {
        ISearchQuery ISearchQuery = mock(ISearchQuery.class);
        IObject orderItem = mock(IObject.class);

        FieldPath fieldPath = mock(FieldPath.class);
        String sortDirection = "testSQLStrDirection";

        when(ISearchQuery.countOrderBy()).thenReturn(1);
        when(ISearchQuery.getOrderBy(0)).thenReturn(orderItem);
        when(orderItem.getValue(anyObject())).thenReturn("testOrderField").thenReturn("testOrderDirection");

        when(fieldPath.getSQLRepresentation()).thenReturn("testSQLStrField");

        IKey fieldPathKey = mock(IKey.class);
        IKey strKey = mock(IKey.class);

        when(Keys.getOrAdd(PSQLFieldPath.class.getCanonicalName())).thenReturn(fieldPathKey);
        when(Keys.getOrAdd(String.class.getCanonicalName())).thenReturn(strKey);

        when(IOC.resolve(eq(fieldPathKey), anyString())).thenReturn(fieldPath).thenReturn(sortDirection);

        QueryStatement queryStatement = new QueryStatement();
        List<SQLQueryParameterSetter> setters = new LinkedList<>();
        orderWriter.write(queryStatement, ISearchQuery, setters);

        assertTrue("ORDER BY(testSQLStrField)ASC,(1)".equals(queryStatement.getBodyWriter().toString()));

        verify(orderItem, times(2)).getValue(anyObject());
        verify(fieldPath, times(1)).getSQLRepresentation();
        verifyStatic(times(1));
        Keys.getOrAdd(PSQLFieldPath.class.getCanonicalName());
        verifyStatic(times(1));
        Keys.getOrAdd(String.class.getCanonicalName());
        verifyStatic(times(1));
        IOC.resolve(eq(fieldPathKey), anyObject());
        verifyStatic(times(1));
        IOC.resolve(eq(strKey), anyObject());
    }
}
