package info.smart_tools.smartactors.core.db_tasks.psql.search.utils;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOC.class, Keys.class })
public class GeneralSQLPagingWriterTest {
    private static IField pageNumberField = mock(IField.class);
    private static IField pageSizeField = mock(IField.class);
    private static IObject message = mock(IObject.class);

    @BeforeClass
    public static void setUp() throws ResolutionException {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);

        when(Keys.getOrAdd(IField.class.toString())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), eq("pageNumber"))).thenReturn(pageNumberField);
        when(IOC.resolve(eq(fieldKey), eq("pageSize"))).thenReturn(pageSizeField);
        // Static block init.
        IField init = DBQueryFields.PAGE_SIZE;
    }

    @Test
    public void should_WritesPAGINGIntoQueryStatement() throws QueryBuildException {
        SQLPagingWriter pagingWriter = SQLPagingWriter.create(1, 10000);
        QueryStatement queryStatement = new QueryStatement();

        pagingWriter.write(queryStatement);

        assertTrue("LIMIT(?)OFFSET(?)".equals(queryStatement.getBodyWriter().toString()));
    }

    @Test
    public void should_TakePageSize() throws Exception {
        reset(pageSizeField);

        SQLPagingWriter pagingWriter = SQLPagingWriter.create(1, 10000);

        when(pageSizeField.in(message)).thenReturn(12);

        int pageSize = pagingWriter.takePageSize(message);

        verify(pageSizeField).in(message);
        assertEquals(pageSize, 12);


    }

    @Test
    public void should_TakeMaxPageSize() throws Exception {
        reset(pageSizeField);

        SQLPagingWriter pagingWriter = SQLPagingWriter.create(1, 10000);

        when(pageSizeField.in(message)).thenReturn(10001);

        int pageSize = pagingWriter.takePageSize(message);

        verify(pageSizeField).in(message);
        assertEquals(pageSize, 10000);
    }

    @Test
    public void should_TakeMinPageSize() throws Exception {
        reset(pageSizeField);

        SQLPagingWriter pagingWriter = SQLPagingWriter.create(1, 10000);

        when(pageSizeField.in(message)).thenReturn(-1);

        int pageSize = pagingWriter.takePageSize(message);

        verify(pageSizeField).in(message);
        assertEquals(pageSize, 1);
    }

    @Test
    public void should_TakePageNumber() throws Exception {
        reset(pageNumberField);

        SQLPagingWriter pagingWriter = SQLPagingWriter.create(1, 10000);

        when(pageNumberField.in(message)).thenReturn(12);

        int pageNumber = pagingWriter.takePageNumber(message);

        verify(pageNumberField).in(message);
        assertEquals(pageNumber, 12);
    }

    @Test
    public void should_TakeStartPageNumber() throws Exception {
        reset(pageNumberField);

        SQLPagingWriter pagingWriter = SQLPagingWriter.create(1, 10000);

        when(pageNumberField.in(message)).thenReturn(-12);

        int pageNumber = pagingWriter.takePageNumber(message);

        verify(pageNumberField).in(message);
        assertEquals(pageNumber, 0);
    }
}
