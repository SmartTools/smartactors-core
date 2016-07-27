package info.smart_tools.smartactors.core.postgres_connection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.verify;

@PrepareForTest({QueryStatement.class})
@RunWith(PowerMockRunner.class)
public class QueryStatementTest {

    private QueryStatement testQueryStatement;

    private StringWriter bodyWriter;
    private LinkedList<SQLQueryParameterSetter> parameterSetters;

    @Before
    public void before() throws Exception {
        bodyWriter = mock(StringWriter.class);
        parameterSetters = new LinkedList<>();

        parameterSetters.add(mock(SQLQueryParameterSetter.class));
        parameterSetters.add(mock(SQLQueryParameterSetter.class));
        parameterSetters.add(mock(SQLQueryParameterSetter.class));

        whenNew(StringWriter.class).withNoArguments().thenReturn(bodyWriter);
        whenNew(LinkedList.class).withNoArguments().thenReturn(parameterSetters);

        testQueryStatement = new QueryStatement();

        verifyNew(StringWriter.class).withNoArguments();
        verifyNew(LinkedList.class).withNoArguments();
    }

    @Test
    public void MustCorrectCompileQuery() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        String bodyWriterStringPresent = "asdadsad";
        when(bodyWriter.toString()).thenReturn(bodyWriterStringPresent);

        when(connection.prepareStatement(bodyWriterStringPresent)).thenReturn(preparedStatement);

        int index = 1;

        for (SQLQueryParameterSetter setter : parameterSetters) {
            when(setter.setParameters(preparedStatement, index)).thenReturn(index);
        }

        assertTrue(testQueryStatement.compile(connection) == preparedStatement);

        verify(connection).prepareStatement(bodyWriterStringPresent);

        for (SQLQueryParameterSetter setter : parameterSetters) {
            verify(setter).setParameters(preparedStatement, index);
        }
    }
}