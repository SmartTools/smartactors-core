package info.smart_tools.smartactors.core.db_tasks.psql.search.utils;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
@SuppressWarnings("unchecked")
public class GeneralSQLOrderWriterTest {
    private static SQLOrderWriter orderWriter;
    private static IField field;
    private static IField orderField;

    @BeforeClass
    public static void setUp() throws ResolutionException {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);
        field = mock(IField.class);
        orderField = mock(IField.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("field"))).thenReturn(field);
        when(IOC.resolve(eq(fieldKey), eq("order"))).thenReturn(orderField);

        orderWriter = SQLOrderWriter.create();
    }

    @Test
    public void should_Write_DESC_ORDER_IntoQueryStatement() throws Exception {
        reset(orderField, field);

        QueryStatement queryStatement = new QueryStatement();
        IObject orderItem = mock(IObject.class);
        List<IObject> orderByItems = new ArrayList<>(1);
        orderByItems.add(orderItem);

        when(field.in(orderItem)).thenReturn("testOrderField");
        when(orderField.in(orderItem)).thenReturn("DESC");

        orderWriter.write(queryStatement, orderByItems);

        assertEquals("ORDER BY(document#>'{testOrderField}')DESC,(1)", queryStatement.getBodyWriter().toString());
        verify(orderField).in(orderItem);
        verify(field).in(orderItem);
    }

    @Test
    public void should_WriteDefaultORDERIntoQueryStatement() throws Exception {
        reset(orderField, field);

        QueryStatement queryStatement = new QueryStatement();
        IObject orderItem = mock(IObject.class);
        List<IObject> orderByItems = new ArrayList<>(1);
        orderByItems.add(orderItem);

        when(field.in(orderItem)).thenReturn("testOrderField");
        when(orderField.in(orderItem)).thenReturn(null);

        orderWriter.write(queryStatement, orderByItems);

        assertEquals("ORDER BY(document#>'{testOrderField}')ASC,(1)", queryStatement.getBodyWriter().toString());
        verify(orderField).in(orderItem);
        verify(field).in(orderItem);
    }

    @Test
    public void should_NotWriteOrder_BecauseOrderIsNull() throws QueryBuildException {
        QueryStatement queryStatement = new QueryStatement();

        orderWriter.write(queryStatement, null);

        assertEquals(queryStatement.getBodyWriter().toString(), "");
    }

    @Test
    public void should_NotWriteOrder_BecauseOrderIsEmpty() throws QueryBuildException {
        QueryStatement queryStatement = new QueryStatement();
        List<IObject> orderByItems = new ArrayList<>(1);

        orderWriter.write(queryStatement, orderByItems);

        assertEquals(queryStatement.getBodyWriter().toString(), "");
    }
}
