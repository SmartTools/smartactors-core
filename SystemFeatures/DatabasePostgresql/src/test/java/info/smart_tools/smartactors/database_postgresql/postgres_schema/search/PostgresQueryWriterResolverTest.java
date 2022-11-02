package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.SQLQueryParameterSetter;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * A complex test for all criteria and operators.
 */
public class PostgresQueryWriterResolverTest {

    private PostgresQueryWriterResolver resolver;
    private StringWriter body;
    private List<SQLQueryParameterSetter> setters;
    private QueryStatement query;
    private List<Object> parameters;
    private PreparedStatement statement;

    @Before
    public void setUp() throws SQLException {
        resolver = new PostgresQueryWriterResolver();

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

        parameters = new ArrayList<>();
        statement = mock(PreparedStatement.class);
        Answer paramSetter = invocation -> {
            Object[] args = invocation.getArguments();
            int index = (int) args[0];
            Object param = args[1];
            parameters.add(param);
            return null;
        };
        doAnswer(paramSetter).when(statement).setInt(anyInt(), anyInt());
        doAnswer(paramSetter).when(statement).setString(anyInt(), anyString());
        doAnswer(paramSetter).when(statement).setObject(anyInt(), any());
    }

    private void testAndVerify(String jsonFilter, String expectedBody, Object[] expectedParameters)
            throws InvalidArgumentException, QueryBuildException, SQLException {
        IObject filter = new DSObject(jsonFilter);
        resolver.resolve(null).write(query, resolver, null, filter);
        assertEquals(expectedBody, body.toString());

        int index = 1;
        for (SQLQueryParameterSetter setter : setters) {
            index = setter.setParameters(statement, index);
        }
        assertArrayEquals(expectedParameters, parameters.toArray());
    }

    @Test
    public void testEmptyQuery() throws InvalidArgumentException, QueryBuildException, SQLException {
        testAndVerify(
                "{}",
                "(TRUE)",
                new Object[] {}
        );
    }

    @Test
    @Ignore("Equality test without explicit $eq operator is not supported")
    public void testOneFieldEqualityQuery() throws InvalidArgumentException, QueryBuildException, SQLException {
        testAndVerify(
                "{ \"a\": \"b\" }",
                "document#>'{a}'=?",
                new Object[] { "b" }
        );
    }

    @Test
    public void testEqualityQuery() throws InvalidArgumentException, QueryBuildException, SQLException {
        testAndVerify(
                "{ \"a\": { \"$eq\": \"b\" } }",
                "((((document#>'{a}')=to_json(?)::jsonb)))",
                new Object[] { "b" }
        );
    }

    @Test
    public void testInQuery() throws InvalidArgumentException, QueryBuildException, SQLException {
        testAndVerify(
                "{ \"status\": { \"$in\": [ \"P\", \"D\" ] } }",
                "((((document#>'{status}')in(to_json(?)::jsonb,to_json(?)::jsonb))))",
                new Object[] { "P", "D" }
        );
    }

    @Test
    public void testImplicitAndCondition() throws InvalidArgumentException, QueryBuildException, SQLException {
        testAndVerify(
                "{ \"status\": { \"$eq\": \"A\" }, \"age\": { \"$lt\": 30 } }",
                "((((document#>'{status}')=to_json(?)::jsonb))AND(((document#>'{age}')<to_json(?)::jsonb)))",
                new Object[] { "A", 30 }
        );
    }

    @Test
    public void testOrCondition() throws InvalidArgumentException, QueryBuildException, SQLException {
        testAndVerify(
                "{ \"$or\": [ { \"status\": { \"$eq\": \"A\" } }, { \"age\": { \"$lt\": 30 } } ] }",
                "((((((document#>'{status}')=to_json(?)::jsonb)))OR((((document#>'{age}')<to_json(?)::jsonb)))))",
                new Object[] { "A", 30 }
        );
    }

    @Test
    public void testImplicitAndAndOrCondition() throws InvalidArgumentException, QueryBuildException, SQLException {
        testAndVerify(
                "{ \"status\": { \"$eq\": \"A\" }, \"$or\": [ { \"age\": { \"$lt\": 30 } }, { \"type\": { \"$eq\": 1 } } ] }",
                "((((document#>'{status}')=to_json(?)::jsonb))AND(((((document#>'{age}')<to_json(?)::jsonb)))OR((((document#>'{type}')=to_json(?)::jsonb)))))",
                new Object[] { "A", 30, 1 }
        );
    }

    @Test
    public void testNestedField() throws QueryBuildException, InvalidArgumentException, SQLException {
        testAndVerify(
                "{ \"favorites.artist\": { \"$eq\": \"Picasso\" } }",
                "((((document#>'{favorites,artist}')=to_json(?)::jsonb)))",
                new Object[] { "Picasso" }
        );
    }

    @Test
    public void testMatchCombination() throws QueryBuildException, InvalidArgumentException, SQLException {
        testAndVerify(
                "{ \"finished\": { \"$gt\": 15, \"$lt\": 20 } }",
                "((((document#>'{finished}')<to_json(?)::jsonb)AND((document#>'{finished}')>to_json(?)::jsonb)))",
                new Object[] { 20, 15 }
        );
    }

}
