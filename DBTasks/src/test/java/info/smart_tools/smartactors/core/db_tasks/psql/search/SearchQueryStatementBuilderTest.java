package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.SQLOrderWriter;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.SQLPagingWriter;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.IDeclaredParam;
import info.smart_tools.smartactors.core.sql_commons.QueryConditionResolver;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import utils.SearchQueryHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
public class SearchQueryStatementBuilderTest {
    private static SQLOrderWriter orderWriter;
    private static SQLPagingWriter pagingWriter;
    private static QueryConditionResolver conditionResolver;

    @BeforeClass
    public static void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);
        IField fieldF = mock(IField.class);
        IField orderF = mock(IField.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("field"))).thenReturn(fieldF);
        when(IOC.resolve(eq(fieldKey), eq("order"))).thenReturn(orderF);

        conditionResolver = PSQLConditionsResolver.create();
        orderWriter = SQLOrderWriter.create();
        pagingWriter = SQLPagingWriter.create(1, 10000);
    }

    @Test
    public void should_BuildDefaultSearchQuery() throws Exception {
        SearchQueryStatementBuilder queryStatementBuilder = SearchQueryStatementBuilder.create(
                conditionResolver,
                orderWriter,
                pagingWriter);

        List<IDeclaredParam> declaredParams = new ArrayList<>();
        QueryStatement queryStatement = queryStatementBuilder
                .withCollection("testCollection")
                .withCriteria(null)
                .withOrderBy(null)
                .withDeclaredParams(declaredParams)
                .build();

        assertEquals(queryStatement.getBodyWriter().toString(),
                "SELECT * FROM testCollection WHERE(true)LIMIT(?)OFFSET(?);");
        assertEquals(declaredParams.size(), 0);
    }

    @Test
    public void should_BuildSearchQueryWithCriteriaWithoutOrder() throws QueryBuildException, ResolutionException {
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

        String expectedResult = "SELECT * FROM testCollection " + SearchQueryHelper.expectedResult + "LIMIT(?)OFFSET(?);";

        SearchQueryStatementBuilder queryStatementBuilder = SearchQueryStatementBuilder.create(
                conditionResolver,
                orderWriter,
                pagingWriter);

        List<IDeclaredParam> declaredParams = new ArrayList<>();

        QueryStatement queryStatement = queryStatementBuilder
                .withCollection("testCollection")
                .withCriteria(SearchQueryHelper.createComplexCriteria())
                .withOrderBy(null)
                .withDeclaredParams(declaredParams)
                .build();

        assertEquals(queryStatement.getBodyWriter().toString(), expectedResult);
        assertEquals(declaredParams.size(), 3);

        List<String> parametersNames = new ArrayList<>(3);
        parametersNames.add("aSingleParam");
        parametersNames.add("bSingleParam");
        parametersNames.add("cArrayParam");

        for (IDeclaredParam declaredParam : declaredParams) {
            assertTrue(parametersNames.contains(declaredParam.getName().toString()));
            IFieldName current = declaredParam.getName();
            if (current.equals(SearchQueryHelper.A_FN) || current.equals(SearchQueryHelper.B_FN)) {
                assertEquals(declaredParam.getCount(), 1);
            } else if (current.equals(SearchQueryHelper.C_FN)) {
                assertEquals(declaredParam.getCount(), 3);
            }
        }
    }
}
