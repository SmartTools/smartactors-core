package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IComplexCompiledQuery;
import info.smart_tools.smartactors.core.db_tasks.psql.delete.PSQLDeleteByIdTask;
import info.smart_tools.smartactors.core.db_tasks.utils.IDContainer;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.IDeclaredParam;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import utils.SearchQueryHelper;
import utils.TestUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.fields;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class, IDContainer.class})
@SuppressWarnings("unchecked")
public class PSQLSearchTaskTest {
    private static IStorageConnection connection = mock(IStorageConnection.class);
    private static IObject message = mock(IObject.class);
    private static ICompiledQuery compiledQuery = mock(ICompiledQuery.class);

    private static IField collectionF = Mockito.mock(IField.class);
    private static IField parametersF = Mockito.mock(IField.class);
    private static IField criteriaF = Mockito.mock(IField.class);
    private static IField orderByF = Mockito.mock(IField.class);
    private static IField pageSizeF = Mockito.mock(IField.class);
    private static IField pageNumberF = Mockito.mock(IField.class);
    private static IField searchResultF = Mockito.mock(IField.class);

    private static IField fieldF = mock(IField.class);
    private static IField orderF = mock(IField.class);

    @BeforeClass
    public static void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);
        mockStatic(IDContainer.class);

        IKey fieldKey = Mockito.mock(IKey.class);
        IKey cacheMaxSize = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(Keys.getOrAdd(eq("COMPLEX_QUERY_CACHE_SIZE"))).thenReturn(cacheMaxSize);

        when(IOC.resolve(eq(cacheMaxSize))).thenReturn(10);
        when(IOC.resolve(eq(fieldKey), eq("collection"))).thenReturn(collectionF);
        when(IOC.resolve(eq(fieldKey), eq("parameters"))).thenReturn(parametersF);
        when(IOC.resolve(eq(fieldKey), eq("criteria"))).thenReturn(criteriaF);
        when(IOC.resolve(eq(fieldKey), eq("orderBy"))).thenReturn(orderByF);
        when(IOC.resolve(eq(fieldKey), eq("pageNumber"))).thenReturn(pageNumberF);
        when(IOC.resolve(eq(fieldKey), eq("pageSize"))).thenReturn(pageSizeF);
        when(IOC.resolve(eq(fieldKey), eq("searchResult"))).thenReturn(searchResultF);

        when(IOC.resolve(eq(fieldKey), eq("field"))).thenReturn(fieldF);
        when(IOC.resolve(eq(fieldKey), eq("order"))).thenReturn(orderF);

        // Static block init.
        IField init = DBQueryFields.COLLECTION;
        IDatabaseTask searchTask = PSQLSearchTask.create(1, 10000);
    }

    @Test
    public void should_prepareSearchTask() throws Exception {
        reset(compiledQuery, connection, collectionF,
                criteriaF, pageNumberF, orderByF, parametersF, pageSizeF);

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IFieldName.class.toString())).thenReturn(fieldNameKey);

        when(IOC.resolve(eq(fieldNameKey), any())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            IFieldName fieldName = mock(IFieldName.class);
            when(fieldName.toString()).thenReturn(args[1].toString());

            return fieldName;
        });

        IDatabaseTask searchTask = PSQLSearchTask.create(1, 10000);
        ICollectionName collectionName = mock(ICollectionName.class);
        IObject criteria = SearchQueryHelper.createComplexCriteria();
        IObject aOrder = mock(IObject.class);
        IObject bOrder = mock(IObject.class);
        List<IObject> order = new ArrayList<>(2);
        order.add(aOrder);
        order.add(bOrder);
        IObject parameters = mock(IObject.class);

        when(parameters.getValue(anyObject()))
                .thenReturn(50)
                .thenReturn(95)
                .thenReturn(Arrays.asList("asd","qwe","zxc"));
        when(collectionF.in(message)).thenReturn(collectionName);
        when(criteriaF.in(message)).thenReturn(criteria);
        when(parametersF.in(message)).thenReturn(parameters);
        when(orderByF.in(message)).thenReturn(order);
        when(pageNumberF.in(message)).thenReturn(1);
        when(pageSizeF.in(message)).thenReturn(100);
        when(collectionName.toString()).thenReturn("testCollection");
        when(connection.compileQuery(anyObject())).thenReturn(compiledQuery);

        when(connection.getId()).thenReturn("testConnectionId");

        when(fieldF.in(aOrder)).thenReturn("a");
        when(fieldF.in(bOrder)).thenReturn("b");
        when(orderF.in(aOrder)).thenReturn("DESC");
        when(orderF.in(bOrder)).thenReturn(null); //equals ASC.

        searchTask.setConnection(connection);
        searchTask.prepare(message);

        verify(connection).getId();
        verify(connection).compileQuery(anyObject());
        verify(parameters, times(3)).getValue(anyObject());

        Field[] fields = fields(PSQLDeleteByIdTask.class);
        // Task prepared query must be a IComplexCompiledQuery type another failed.
        assertTrue(IComplexCompiledQuery.class.isAssignableFrom(
                TestUtils.getValue(fields, searchTask, "query").getClass()));
        IComplexCompiledQuery preparedCompiledQuery =
                (IComplexCompiledQuery) TestUtils.getValue(fields, searchTask, "query");
        assertEquals(preparedCompiledQuery, compiledQuery);
        assertEquals(preparedCompiledQuery.getDeclaredParams().size(), 3);

        List<String> parametersNames = new ArrayList<>(3);
        parametersNames.add("aSingleParam");
        parametersNames.add("bSingleParam");
        parametersNames.add("cArrayParam");
        for (IDeclaredParam declaredParam : preparedCompiledQuery.getDeclaredParams()) {
            if (!parametersNames.contains(declaredParam.getName().toString())) {
                throw new Exception("Prepared invalid declared parameters!");
            }
        }

        assertEquals(TestUtils.getValue(fields, searchTask, "message"), message);
        assertEquals(TestUtils.getValue(fields, searchTask, "executable"), true);
    }
}
