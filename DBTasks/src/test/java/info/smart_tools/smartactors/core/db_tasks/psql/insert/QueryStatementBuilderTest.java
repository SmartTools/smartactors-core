package info.smart_tools.smartactors.core.db_tasks.psql.insert;

import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class QueryStatementBuilderTest {
    private QueryStatementBuilder builder;
    private String collection;

    @Before
    public void setUp() throws Exception {
        collection = "testCollection";
        builder = QueryStatementBuilder
                .create()
                .withCollection(collection);
    }

    @Test
    public void buildQueryStatementTest() throws Exception {
        String validationStr = "INSERT INTO " + collection + "(document) VALUES(?::jsonb) RETURNING id;";

        QueryStatement queryStatement = builder.build();

        assertNotEquals(queryStatement, null);
        assertEquals(queryStatement.getBodyWriter().toString(), validationStr);

        Field templateSizeField = QueryStatementBuilder.class.getDeclaredField("TEMPLATE_SIZE");
        templateSizeField.setAccessible(true);
        int expectedTemplateSize = (int) templateSizeField.get(null) + collection.length();
        int actualTemplateSize = queryStatement.getBodyWriter().toString().length();

        assertEquals(expectedTemplateSize, actualTemplateSize);
    }
}
