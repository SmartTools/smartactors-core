package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OrderWriterTest {

    private OrderWriter orderWriter;
    private StringWriter body;
    private QueryStatement query;

    @Before
    public void setUp() {
        orderWriter = new OrderWriter();
        body = new StringWriter();

        query = mock(QueryStatement.class);
        when(query.getBodyWriter()).thenReturn(body);
    }

    @Test
    public void should_WritesORDERClauseIntoQueryStatement() throws Exception {
        IObject criteriaMessage = new DSObject("{ \"sort\": [ { \"testField\": \"desc\" }, { \"anotherTestField\": \"asc\" } ] }");
        List<IObject> sortMessage = (List<IObject>) criteriaMessage.getValue(new FieldName("sort"));
        orderWriter.write(query, sortMessage);
        assertEquals("ORDER BY(document#>'{testField}')DESC,(document#>'{anotherTestField}')ASC", body.toString());
        verify(query, times(0)).pushParameterSetter(any());
    }

    @Test(expected = QueryBuildException.class)
    public void should_FailOnWrongDirection() throws Exception {
        IObject criteriaMessage = new DSObject("{ \"sort\": [ { \"testField\": 1 } ] }");
        List<IObject> sortMessage = (List<IObject>) criteriaMessage.getValue(new FieldName("sort"));
        orderWriter.write(query, sortMessage);
        fail();
    }

}
