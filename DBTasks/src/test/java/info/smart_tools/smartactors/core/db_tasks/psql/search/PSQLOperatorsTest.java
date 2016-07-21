package info.smart_tools.smartactors.core.db_tasks.psql.search;


import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.PSQLFieldPath;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.ConditionsResolverBase;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.IDeclaredParam;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
public class PSQLOperatorsTest {
    private ConditionsResolverBase conditionsResolver;

    @Before
    public void setUp() throws ResolutionException {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IFieldName.class.toString())).thenReturn(fieldNameKey);

        when(IOC.resolve(eq(fieldNameKey), any())).thenAnswer((Answer) invocation -> {
            Object[] args = invocation.getArguments();
            IFieldName fieldName = mock(IFieldName.class);
            when(fieldName.toString()).thenReturn(args[1].toString());

            return fieldName;
        });

        conditionsResolver = PSQLConditionsResolver.create();
    }

    @Test
    public void should_AddsAllOperators() throws Exception {
        ConditionsResolverBase conditionsResolverBase = mock(ConditionsResolverBase.class);
        PSQLOperators.addAll(conditionsResolverBase);

        verify(conditionsResolverBase, times(12)).addOperator(anyString(), anyObject());
        verifyPrivate(PSQLOperators.class, times(10)).invoke("formattedCheckWriter", anyString(), anyString());
    }

    @Test
    public void writeFieldCheckConditionTest() throws Exception {
        final int OPERATORS_NUMBER = 10;
        final String queryParam = "testQueryParam";

        List<String> operatorsNames = new ArrayList<>(OPERATORS_NUMBER);
        List<FieldPath> fieldsPaths = new ArrayList<>(OPERATORS_NUMBER);
        List<IDeclaredParam> order = new ArrayList<>(OPERATORS_NUMBER);
        List<String> result = new ArrayList<>(OPERATORS_NUMBER);

        operatorsNames.addAll(
                Arrays.asList(
                        "$eq", "$ne", "$lt",
                        "$gt", "$lte", "$gte",
                        "$date-from", "$date-to",
                        "$hasTag", "$fulltext"));

        for (String name : operatorsNames)
                fieldsPaths.add(PSQLFieldPath.fromString(name.replace("$", "")));

        result.addAll(
                Arrays.asList(
                        "((document#>'{eq}')=to_json(?)::jsonb)", "((document#>'{ne}')!=to_json(?)::jsonb)",
                        "((document#>'{lt}')<to_json(?)::jsonb)", "((document#>'{gt}')>to_json(?)::jsonb)",
                        "((document#>'{lte}')<=to_json(?)::jsonb)", "((document#>'{gte}')>=to_json(?)::jsonb)",
                        "(parse_timestamp_immutable(document#>'{date-from}')>=(?)::timestamp)",
                        "(parse_timestamp_immutable(document#>'{date-to}')<=(?)::timestamp)",
                        "((document#>'{hasTag}')??(?))",
                        "(to_tsvector('russian',(document#>'{fulltext}')::text))@@(to_tsquery(russian,?))"));

        for (int i = 0; i < OPERATORS_NUMBER; ++i) {
            QueryStatement queryStatement = new QueryStatement();
            conditionsResolver
                    .resolve(operatorsNames.get(i))
                    .write(queryStatement, conditionsResolver, fieldsPaths.get(i), queryParam + i, order);
            assertEquals(queryStatement.getBodyWriter().toString().trim(), result.get(i));
        }
        assertEquals(order.size(), 10);
    }

    @Test
    public void writeFieldExistsCheckConditionTest() throws Exception {
        final int OPERATORS_NUMBER = 1;

        List<IDeclaredParam> order = new ArrayList<>(OPERATORS_NUMBER);
        FieldPath fieldPath = PSQLFieldPath.fromString("isNull");

        QueryStatement queryStatementIsNull = new QueryStatement();
        conditionsResolver
                .resolve("$isNull")
                .write(queryStatementIsNull, conditionsResolver, fieldPath, "true", order);
        assertEquals(queryStatementIsNull.getBodyWriter().toString().trim(), "(document#>'{isNull}') is null");

        QueryStatement queryStatementIsNotNull = new QueryStatement();
        conditionsResolver
                .resolve("$isNull")
                .write(queryStatementIsNotNull, conditionsResolver, fieldPath, "false", order);
        assertEquals(queryStatementIsNotNull.getBodyWriter().toString().trim(), "(document#>'{isNull}') is not null");
    }

    @Test
    public void writeFieldInArrayCheckConditionTest() throws Exception {
        final String operatorName = "$in";
        final int OPERATORS_NUMBER = 1;

        List<IDeclaredParam> order = new ArrayList<>(OPERATORS_NUMBER);
        FieldPath fieldPath = PSQLFieldPath.fromString(operatorName.replace("$", ""));
        List<Object> queryParam = new ArrayList<>(Arrays.asList("testInName", 3));

        QueryStatement queryStatement = new QueryStatement();
        conditionsResolver
                .resolve(operatorName)
                .write(queryStatement, conditionsResolver, fieldPath, queryParam, order);
        assertEquals(queryStatement.getBodyWriter().toString().trim(),
                "((document#>'{in}')in(to_json(?)::jsonb,to_json(?)::jsonb,to_json(?)::jsonb))");
        assertEquals(order.size(), 1);
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldCheckCondition_ContextFieldPath_IsNull_Test() throws QueryBuildException {
        conditionsResolver
                .resolve("$eq")
                .write(new QueryStatement(), conditionsResolver, null, "param", new ArrayList<>());
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldExistsCheckCondition_ContextFieldPath_IsNull_Test() throws QueryBuildException {
        conditionsResolver
                .resolve("$isNull")
                .write(new QueryStatement(), conditionsResolver, null, "true", new ArrayList<>());
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldExistsCheckCondition_QueryParameter_Invalid_Test() throws QueryBuildException {
        conditionsResolver
                .resolve("$isNull")
                .write(new QueryStatement(), conditionsResolver,
                        PSQLFieldPath.fromString("fieldPath"), "invalidParam", new ArrayList<>());
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldInArrayCheckCondition_ContextFieldPath_IsNull_Test() throws QueryBuildException {
        conditionsResolver
                .resolve("$in")
                .write(new QueryStatement(), conditionsResolver, null, "param", new ArrayList<>());
    }

    @Test(expected = QueryBuildException.class)
    public void writeFieldInArrayCheckCondition_QueryParameter_Invalid_Test() throws QueryBuildException {
        conditionsResolver
                .resolve("$in")
                .write(new QueryStatement(), conditionsResolver,
                        PSQLFieldPath.fromString("fieldPath"), "invalidParam", new ArrayList<>());
    }
}
