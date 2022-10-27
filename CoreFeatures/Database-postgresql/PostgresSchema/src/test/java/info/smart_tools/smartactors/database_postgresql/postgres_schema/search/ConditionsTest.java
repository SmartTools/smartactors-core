package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for Conditions
 */
public class ConditionsTest {

    private StringWriter body;
    private QueryStatement query;
    private QueryWriter writer;
    private QueryWriterResolver resolver;
    private String fieldPath;
    private Object queryParameter;

    @Before
    public void setUp() throws QueryBuildException {
        body = new StringWriter();

        query = mock(QueryStatement.class);
        when(query.getBodyWriter()).thenReturn(body);

        writer = mock(QueryWriter.class);
        List setters = new ArrayList();
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            String resolver1 = String.valueOf(args[1]);
            String path = String.valueOf(args[2]);
            String param = String.valueOf(args[3]);
            setters.add(param);
            body.write(String.format(" %s, %s, %s, %s ", resolver1, path, param, setters));
            return null;
        }).when(writer).write(same(query), any(), any(), any());

        resolver = mock(QueryWriterResolver.class);
        when(resolver.resolve(any())).thenReturn(writer);
        when(resolver.toString()).thenReturn("resolver");

        fieldPath = "fieldPath";
    }

    @Test
    public void testAndOnIObject() throws QueryBuildException, InvalidArgumentException {
        queryParameter = new DSObject("{ \"a\": \"b\", \"c\": \"d\" }");
        Conditions.writeAndCondition(query, resolver, fieldPath, queryParameter);
        assertEquals("( resolver, fieldPath, b, [b] AND resolver, fieldPath, d, [b, d] )", body.toString());
    }

    @Test
    public void testAndOnList() throws QueryBuildException, InvalidArgumentException {
        queryParameter = new ArrayList() {{ add("a"); add("b"); }};
        Conditions.writeAndCondition(query, resolver, fieldPath, queryParameter);
        assertEquals("( resolver, fieldPath, a, [a] AND resolver, fieldPath, b, [a, b] )", body.toString());
    }

    @Test
    public void testOrOnIObject() throws QueryBuildException, InvalidArgumentException {
        queryParameter = new DSObject("{ \"a\": \"b\", \"c\": \"d\" }");
        Conditions.writeOrCondition(query, resolver, fieldPath, queryParameter);
        assertEquals("( resolver, fieldPath, b, [b] OR resolver, fieldPath, d, [b, d] )", body.toString());
    }

    @Test
    public void testOrOnList() throws QueryBuildException, InvalidArgumentException {
        queryParameter = new ArrayList() {{ add("a"); add("b"); }};
        Conditions.writeOrCondition(query, resolver, fieldPath, queryParameter);
        assertEquals("( resolver, fieldPath, a, [a] OR resolver, fieldPath, b, [a, b] )", body.toString());
    }

    @Test
    public void testNotOnIObject() throws QueryBuildException, InvalidArgumentException {
        queryParameter = new DSObject("{ \"a\": \"b\", \"c\": \"d\" }");
        Conditions.writeNotCondition(query, resolver, fieldPath, queryParameter);
        assertEquals("(NOT( resolver, fieldPath, b, [b] AND resolver, fieldPath, d, [b, d] ))", body.toString());
    }

    @Test
    public void testNotOnList() throws QueryBuildException, InvalidArgumentException {
        queryParameter = new ArrayList() {{ add("a"); add("b"); }};
        Conditions.writeNotCondition(query, resolver, fieldPath, queryParameter);
        assertEquals("(NOT( resolver, fieldPath, a, [a] AND resolver, fieldPath, b, [a, b] ))", body.toString());
    }

}
