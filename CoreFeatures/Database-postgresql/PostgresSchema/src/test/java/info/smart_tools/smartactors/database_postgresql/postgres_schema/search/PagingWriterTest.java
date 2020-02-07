package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.SQLQueryParameterSetter;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PagingWriterTest {

    private PagingWriter pagingWriter;
    private StringWriter body;
    private List<SQLQueryParameterSetter> setters;
    private QueryStatement query;

    @Before
    public void setUp() {
        pagingWriter = new PagingWriter();
        body = new StringWriter();
        setters = new ArrayList<>();

        query = mock(QueryStatement.class);
        when(query.getBodyWriter()).thenReturn(body);
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            SQLQueryParameterSetter setter = (SQLQueryParameterSetter) args[0];
            setters.add(setter);
            return null;
        }).when(query).pushParameterSetter(any());
    }

    @Test
    public void should_WritesPAGINGClauseIntoQueryStatement() throws QueryBuildException, SQLException {
        pagingWriter.writeByPageSizeAndNumber(query, 3, 8);
        assertEquals("LIMIT(?)OFFSET(?)", body.toString());
        verify(query, times(1)).pushParameterSetter(any());
        for (SQLQueryParameterSetter setter : setters) {
            PreparedStatement statement = mock(PreparedStatement.class);
            int finalIndex = setter.setParameters(statement, 1);
            assertEquals(3, finalIndex);
            verify(statement).setInt(eq(1), eq(8));
            verify(statement).setInt(eq(2), eq(16));
        }
    }

    @Test
    public void should_WritesPAGINGClauseIntoQueryStatementByLimitAndOffset() throws QueryBuildException, SQLException {
        pagingWriter.writeByLimitAndOffset(query, 3, 10L);
        assertEquals("LIMIT(?)OFFSET(?)", body.toString());
        verify(query, times(1)).pushParameterSetter(any());
        for (SQLQueryParameterSetter setter : setters) {
            PreparedStatement statement = mock(PreparedStatement.class);
            int finalIndex = setter.setParameters(statement, 1);
            assertEquals(3, finalIndex);
            verify(statement).setInt(eq(1), eq(3));
            verify(statement).setLong(eq(2), eq(10L));
        }
    }

    @Test
    public void should_WritesPAGINGClauseIntoQueryStatementByLimitAndDefaultOffset() throws QueryBuildException, SQLException {
        pagingWriter.writeByLimitAndOffset(query, 3, null);
        assertEquals("LIMIT(?)OFFSET(?)", body.toString());
        verify(query, times(1)).pushParameterSetter(any());
        for (SQLQueryParameterSetter setter : setters) {
            PreparedStatement statement = mock(PreparedStatement.class);
            int finalIndex = setter.setParameters(statement, 1);
            assertEquals(3, finalIndex);
            verify(statement).setInt(eq(1), eq(3));
            verify(statement).setLong(eq(2), eq(0L));
        }
    }

}
