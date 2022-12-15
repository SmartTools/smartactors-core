package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for operators.
 */
public class OperatorsTest {
    private PostgresQueryWriterResolver resolver;

    @Before
    public void setUp() {
        resolver = new PostgresQueryWriterResolver();
    }

    @Test
    public void should_AddsAllOperators() throws Exception {
        PostgresQueryWriterResolver resolverMock = mock(PostgresQueryWriterResolver.class);
        Operators.addAll(resolverMock);

        verify(resolverMock, times(12)).addQueryWriter(anyString(), anyObject());
    }

    @Test
    public void writeFieldCheckConditionTest() throws Exception {
        final int OPERATORS_NUMBER = 10;
        final String queryParam = "testQueryParam";

        List<String> operatorsNames = new ArrayList<>(OPERATORS_NUMBER);
        List<FieldPath> fieldsPaths = new ArrayList<>(OPERATORS_NUMBER);
        List<String> result = new ArrayList<>(OPERATORS_NUMBER);

        operatorsNames.addAll(
                Arrays.asList(
                        "$eq", "$ne", "$lt",
                        "$gt", "$lte", "$gte",
                        "$date-from", "$date-to",
                        "$hasTag", "$fulltext"));

        for (String name : operatorsNames)
                fieldsPaths.add(PostgresFieldPath.fromString(name.replace("$", "")));

        result.addAll(
                Arrays.asList(
                        "((document#>'{eq}')=to_json(?)::jsonb)", "((document#>'{ne}')!=to_json(?)::jsonb)",
                        "((document#>'{lt}')<to_json(?)::jsonb)", "((document#>'{gt}')>to_json(?)::jsonb)",
                        "((document#>'{lte}')<=to_json(?)::jsonb)", "((document#>'{gte}')>=to_json(?)::jsonb)",
                        "(parse_timestamp_immutable(document#>'{date-from}')>=(?)::timestampz)",
                        "(parse_timestamp_immutable(document#>'{date-to}')<=(?)::timestampz)",
                        "((document#>'{hasTag}')??(?))",
                        "fulltext_english@@(to_tsquery(?,?))"));

        for (int i = 0; i < OPERATORS_NUMBER; ++i) {
            StringWriter body = new StringWriter();
            QueryStatement queryStatement = mock(QueryStatement.class);
            when(queryStatement.getBodyWriter()).thenReturn(body);
            resolver
                    .resolve(operatorsNames.get(i))
                    .write(queryStatement, resolver, fieldsPaths.get(i), queryParam);
            assertEquals(result.get(i), body.toString().trim());
            if (i < (OPERATORS_NUMBER-1)) {
                verify(queryStatement).pushParameterSetter(any());
            }
        }
    }

    @Test
    public void writeFieldExistsCheckConditionTest() throws Exception {
        FieldPath fieldPath = PostgresFieldPath.fromString("isNull");

        QueryStatement queryStatementIsNull = new QueryStatement();
        resolver
                .resolve("$isNull")
                .write(queryStatementIsNull, resolver, fieldPath, "true");
        assertEquals(queryStatementIsNull.getBodyWriter().toString().trim(), "(document#>'{isNull}') is null");

        QueryStatement queryStatementIsNotNull = new QueryStatement();
        resolver
                .resolve("$isNull")
                .write(queryStatementIsNotNull, resolver, fieldPath, "false");
        assertEquals(queryStatementIsNotNull.getBodyWriter().toString().trim(), "(document#>'{isNull}') is not null");
    }

    @Test
    public void writeFieldInArrayCheckConditionTest() throws Exception {
        final String operatorName = "$in";

        FieldPath fieldPath = PostgresFieldPath.fromString(operatorName.replace("$", ""));
        List<Integer> queryParam = new ArrayList<>(Arrays.asList(1, 10, 100));

        StringWriter body = new StringWriter();
        QueryStatement queryStatement = mock(QueryStatement.class);
        when(queryStatement.getBodyWriter()).thenReturn(body);

        resolver
                .resolve(operatorName)
                .write(queryStatement, resolver, fieldPath, queryParam);
        assertEquals(queryStatement.getBodyWriter().toString().trim(),
                "((document#>'{in}')in(to_json(?)::jsonb,to_json(?)::jsonb,to_json(?)::jsonb))");
        verify(queryStatement).pushParameterSetter(any());
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldCheckCondition_ContextFieldPath_IsNull_Test() throws QueryBuildException {
        resolver
                .resolve("$eq")
                .write(new QueryStatement(), resolver, null, "param");
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldExistsCheckCondition_ContextFieldPath_IsNull_Test() throws QueryBuildException {
        resolver
                .resolve("$isNull")
                .write(new QueryStatement(), resolver, null, "true");
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldExistsCheckCondition_QueryParameter_Invalid_Test() throws QueryBuildException {
        resolver
                .resolve("$isNull")
                .write(new QueryStatement(), resolver,
                        PostgresFieldPath.fromString("fieldPath"), "invalidParam");
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldInArrayCheckCondition_ContextFieldPath_IsNull_Test() throws QueryBuildException {
        resolver
                .resolve("$in")
                .write(new QueryStatement(), resolver, null, "param");
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldInArrayCheckCondition_QueryParameter_Invalid_Test() throws QueryBuildException {
        resolver
                .resolve("$in")
                .write(new QueryStatement(), resolver,
                        PostgresFieldPath.fromString("fieldPath"), "invalidParam");
    }

}
