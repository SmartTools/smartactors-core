package info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Test for different index dropping statements.
 */
public class IndexDroppersTest extends IOCInitializer {

    private CollectionName collection;
    private StringWriter body;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        collection = CollectionName.fromString("test_collection");
        body = new StringWriter();
    }

    @Test
    public void testOrderedIndex() throws Exception {
        IObject options = new DSObject("{ \"ordered\": [ \"a\", \"b\" ] }");
        IndexDroppers.writeDropIndexes(body, collection, options);
        assertEquals("DROP INDEX test_collection_a_ordered_index;\n" +
                     "DROP INDEX test_collection_b_ordered_index;\n",
                body.toString());
    }

    @Test
    public void testOrderedIndexForOneField() throws Exception {
        IObject options = new DSObject("{ \"ordered\": \"a\" }");
        IndexDroppers.writeDropIndexes(body, collection, options);
        assertEquals("DROP INDEX test_collection_a_ordered_index;\n",
                body.toString());
    }

    @Test
    public void testDatetimeIndex() throws Exception {
        IObject options = new DSObject("{ \"datetime\": [ \"a\", \"b\" ] }");
        IndexDroppers.writeDropIndexes(body, collection, options);
        assertEquals("DROP INDEX test_collection_a_datetime_index;\n" +
                     "DROP INDEX test_collection_b_datetime_index;\n",
                body.toString());
    }

    @Test
    public void testDatetimeIndexForOneField() throws Exception {
        IObject options = new DSObject("{ \"datetime\": \"a\" }");
        IndexDroppers.writeDropIndexes(body, collection, options);
        assertEquals("DROP INDEX test_collection_a_datetime_index;\n",
                body.toString());
    }

    @Test
    public void testTagsIndex() throws Exception {
        IObject options = new DSObject("{ \"tags\": [ \"a\", \"b\" ] }");
        IndexDroppers.writeDropIndexes(body, collection, options);
        assertEquals("DROP INDEX test_collection_a_tags_index;\n" +
                     "DROP INDEX test_collection_b_tags_index;\n",
                body.toString());
    }

    @Test
    public void testTagsIndexForOneField() throws Exception {
        IObject options = new DSObject("{ \"tags\": \"a\" }");
        IndexDroppers.writeDropIndexes(body, collection, options);
        assertEquals("DROP INDEX test_collection_a_tags_index;\n",
                body.toString());
    }

    @Test
    public void testFulltextIndex() throws Exception {
        IObject options = new DSObject("{ " +
                "\"fulltext\": [ \"a\", \"b\" ]," +
                "\"language\": \"russian\"" +
                "}");
        IndexDroppers.writeDropIndexes(body, collection, options);
        assertEquals(   "DROP TRIGGER test_collection_fulltext_russian_update_trigger ON test_collection;\n" +
                        "DROP FUNCTION test_collection_fulltext_russian_update_trigger();\n" +
                        "DROP INDEX test_collection_fulltext_russian_index;\n",
                body.toString());
    }

    @Test
    public void testFulltextIndexForOneField() throws Exception {
        IObject options = new DSObject("{ " +
                "\"fulltext\": \"text\"," +
                "\"language\": \"russian\"" +
                "}");
        IndexDroppers.writeDropIndexes(body, collection, options);
        assertEquals(   "DROP TRIGGER test_collection_fulltext_russian_update_trigger ON test_collection;\n" +
                        "DROP FUNCTION test_collection_fulltext_russian_update_trigger();\n" +
                        "DROP INDEX test_collection_fulltext_russian_index;\n",
                body.toString());
    }

    @Test(expected = Exception.class)
    public void testWrongIndexFormat() throws Exception {
        IObject options = new DSObject("{ \"ordered\": 123 }");
        IndexDroppers.writeDropIndexes(body, collection, options);
    }

}
