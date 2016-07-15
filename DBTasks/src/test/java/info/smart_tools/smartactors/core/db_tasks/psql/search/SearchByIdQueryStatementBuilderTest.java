package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@PrepareForTest(IOC.class)
@RunWith(PowerMockRunner.class)
@SuppressWarnings("unchecked")
public class SearchByIdQueryStatementBuilderTest {

    @Test
    public void buildQueryStatementTest() throws Exception {
        String collection = "testCollection";
        String validationStr = "SELECT * FROM " + collection + " WHERE id=?;";

        QueryStatement queryStatement = SearchByIdQueryStatementBuilder
                .create()
                .withCollection(collection)
                .build();

        assertNotEquals(queryStatement, null);
        assertEquals(queryStatement.getBodyWriter().toString(), validationStr);

        Field templateSizeField = SearchByIdQueryStatementBuilder.class.getDeclaredField("TEMPLATE_SIZE");
        templateSizeField.setAccessible(true);
        int expectedTemplateSize = (int) templateSizeField.get(null) + collection.length();
        int actualTemplateSize = queryStatement.getBodyWriter().toString().length();

        assertEquals(expectedTemplateSize, actualTemplateSize);
    }

    @Test(expected = QueryBuildException.class)
    public void should_ThrowsException_WithReason_CollectionFieldNotSet() throws QueryBuildException {
        SearchByIdQueryStatementBuilder
                .create()
                .build();
    }
}
