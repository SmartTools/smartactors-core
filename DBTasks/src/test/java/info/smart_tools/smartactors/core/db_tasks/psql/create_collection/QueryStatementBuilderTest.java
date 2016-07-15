package info.smart_tools.smartactors.core.db_tasks.psql.create_collection;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest(IOC.class)
@RunWith(PowerMockRunner.class)
@SuppressWarnings("unchecked")
public class QueryStatementBuilderTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(IOC.class);
    }

    @Test
    public void buildQueryStatementWithDefaultIdIndexTest() throws Exception {
        String collection = "testCollection";
        Map<String, String> indexes = new HashMap<>(2);
        String validationStr = "CREATE TABLE testCollection (id BIGSERIAL PRIMARY KEY, document JSONB NOT NULL);\n" +
                "CREATE INDEX ON testCollection USING BTREE ((bigint_to_jsonb_immutable(id)));\n" +
                "CREATE INDEX ON testCollection USING HASH ((bigint_to_jsonb_immutable(id)));\n";

        QueryStatement queryStatement = QueryStatementBuilder
                .create()
                .withCollection(collection)
                .withIndexes(indexes)
                .build();

        assertNotEquals(queryStatement, null);
        assertEquals(queryStatement.getBodyWriter().toString(), validationStr);
    }

    @Test
    public void buildQueryStatementWithDifferentIndexTest() throws Exception {
        String collection = "testCollection";
        Map<String, String> indexes = new HashMap<>(2);
        indexes.put("name", "tags");
        indexes.put("specialText", "fulltext");
        indexes.put("surname", "ordered");
        indexes.put("time", "datetime");
        String validationStr = "CREATE TABLE testCollection (id BIGSERIAL PRIMARY KEY, document JSONB NOT NULL);\n" +
                "CREATE INDEX ON testCollection USING GIN ((document#>'{name}'));\n" +
                "CREATE INDEX ON testCollection USING GIN ((to_tsvector('russian',(document#>'{specialText}')::text)));\n" +
                "CREATE INDEX ON testCollection USING BTREE ((parse_timestamp_immutable(document#>'{time}')));\n" +
                "CREATE INDEX ON testCollection USING BTREE ((bigint_to_jsonb_immutable(id)));\n" +
                "CREATE INDEX ON testCollection USING HASH ((bigint_to_jsonb_immutable(id)));\n" +
                "CREATE INDEX ON testCollection USING BTREE ((document#>'{surname}'));\n";

        QueryStatement queryStatement = QueryStatementBuilder
                .create()
                .withCollection(collection)
                .withIndexes(indexes)
                .build();

        assertNotEquals(queryStatement, null);
        assertEquals(queryStatement.getBodyWriter().toString(), validationStr);
    }

    @Test
    public void checkSizeCreateCollectionQueryTest() throws Exception {
        String collection = "testCollection";
        QueryStatementBuilder builder = QueryStatementBuilder
                .create()
                .withCollection(collection);

        Field templateSizeField = QueryStatementBuilder.class.getDeclaredField("TEMPLATE_SIZE");
        templateSizeField.setAccessible(true);
        int expectedTemplateSize = (int) templateSizeField.get(null) + collection.length();

        Field queryField = QueryStatementBuilder.class.getDeclaredField("createCollectionQuery");
        queryField.setAccessible(true);
        int actualTemplateSize = queryField.get(builder).toString().length();

        assertEquals(expectedTemplateSize, actualTemplateSize);
    }

    @Test(expected = QueryBuildException.class)
    public void should_ThrowsException_WithReason_CollectionFieldNotSet() throws QueryBuildException {
        QueryStatementBuilder
                .create()
                .withIndexes(Collections.emptyMap())
                .build();
    }

    @Test(expected = QueryBuildException.class)
    public void should_ThrowsException_WithReason_IndexesFieldNotSet() throws QueryBuildException {
        QueryStatementBuilder
                .create()
                .withCollection("collection")
                .build();
    }
}
