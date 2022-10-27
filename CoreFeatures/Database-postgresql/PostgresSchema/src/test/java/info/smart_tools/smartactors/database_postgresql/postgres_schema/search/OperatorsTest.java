package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import org.junit.Before;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void prepareIOC() throws PluginException, ProcessExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
        bootstrap.start();
    }

    @Before
    public void setUp() throws Exception {
        resolver = new PostgresQueryWriterResolver();
        IOC.register(Keys.getKeyByName(IFieldName.class.getCanonicalName()), new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                                return null;
                            }
                        }
                )
        );
    }

    @Test
    public void should_AddsAllOperators() throws Exception {
        PostgresQueryWriterResolver resolverMock = mock(PostgresQueryWriterResolver.class);
        Operators.addAll(resolverMock);

        verify(resolverMock, times(12)).addQueryWriter(anyString(), anyObject());
    }

    private void testAndVerifyOperators(
            final int operatorNumber,
            final Object queryParam,
            final List<String> operatorsNames,
            final List<String> fieldsPaths,
            final List<String> result) throws Exception {
        for (int i = 0; i < operatorNumber; ++i) {
            StringWriter body = new StringWriter();
            QueryStatement queryStatement = mock(QueryStatement.class);
            when(queryStatement.getBodyWriter()).thenReturn(body);
            resolver
                    .resolve(operatorsNames.get(i))
                    .write(queryStatement, resolver, fieldsPaths.get(i), queryParam);
            assertEquals(result.get(i), body.toString().trim());
            if (i < (operatorNumber-1)) {
                verify(queryStatement).pushParameterSetter(any());
            }
        }
    }

    @Test
    public void writeFieldCheckConditionTest() throws Exception {
        final int OPERATORS_NUMBER = 10;
        final String queryParam = "testQueryParam";

        List<String> operatorsNames = new ArrayList<>(OPERATORS_NUMBER);
        List<String> fieldsPaths = new ArrayList<>(OPERATORS_NUMBER);
        List<String> result = new ArrayList<>(OPERATORS_NUMBER);

        operatorsNames.addAll(
                Arrays.asList(
                        "$eq", "$ne", "$lt",
                        "$gt", "$lte", "$gte",
                        "$date-from", "$date-to",
                        "$hasTag", "$fulltext"));

        for (String name : operatorsNames)
                fieldsPaths.add(name.replace("$", ""));

        result.addAll(
                Arrays.asList(
                        "((document#>'{eq}')=to_json(?)::jsonb)", "((document#>'{ne}')!=to_json(?)::jsonb)",
                        "((document#>'{lt}')<to_json(?)::jsonb)", "((document#>'{gt}')>to_json(?)::jsonb)",
                        "((document#>'{lte}')<=to_json(?)::jsonb)", "((document#>'{gte}')>=to_json(?)::jsonb)",
                        "(parse_timestamp_immutable(document#>'{date-from}')>=(?)::timestamptz)",
                        "(parse_timestamp_immutable(document#>'{date-to}')<=(?)::timestamptz)",
                        "((document#>'{hasTag}')??(?))",
                        "fulltext_english@@(to_tsquery(?,?))"));

        testAndVerifyOperators(OPERATORS_NUMBER, queryParam, operatorsNames, fieldsPaths, result);
    }

    @Test
    public void writeFieldCheckWithTypeCastConditionTest() throws Exception {
        final int OPERATORS_NUMBER = 6;
        final IObject queryParam = new DSObject("{ \"value\": \"testQueryParam\", \"type\": \"decimal\" }");

        List<String> operatorsNames = new ArrayList<>(OPERATORS_NUMBER);
        List<String> fieldsPaths = new ArrayList<>(OPERATORS_NUMBER);
        List<String> result = new ArrayList<>(OPERATORS_NUMBER);

        operatorsNames.addAll(
                Arrays.asList(
                        "$eq", "$ne", "$lt",
                        "$gt", "$lte", "$gte"));

        for (String name : operatorsNames)
                fieldsPaths.add(name.replace("$", ""));

        result.addAll(
                Arrays.asList(
                        "(((document#>>'{eq}')::decimal)=(?::decimal))", "(((document#>>'{ne}')::decimal)!=(?::decimal))",
                        "(((document#>>'{lt}')::decimal)<(?::decimal))", "(((document#>>'{gt}')::decimal)>(?::decimal))",
                        "(((document#>>'{lte}')::decimal)<=(?::decimal))", "(((document#>>'{gte}')::decimal)>=(?::decimal))"
                ));

        testAndVerifyOperators(OPERATORS_NUMBER, queryParam, operatorsNames, fieldsPaths, result);
    }

    @Test
    public void writeFieldExistsCheckConditionTest() throws Exception {
        String fieldPath = "isNull";

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

        String fieldPath = operatorName.replace("$", "");
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
                        "fieldPath", "invalidParam");
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
                        "fieldPath", "invalidParam");
    }

}
