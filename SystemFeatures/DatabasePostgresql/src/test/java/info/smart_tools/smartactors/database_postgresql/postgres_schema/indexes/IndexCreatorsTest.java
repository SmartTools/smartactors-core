package info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Test for different index creation statements.
 */
public class IndexCreatorsTest extends IOCInitializer {

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
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_a_ordered_index ON test_collection USING BTREE ((document#>'{a}'));\n" +
                     "CREATE INDEX test_collection_b_ordered_index ON test_collection USING BTREE ((document#>'{b}'));\n",
                body.toString());
    }

    @Test
    public void testOrderedIndexForOneField() throws Exception {
        IObject options = new DSObject("{ \"ordered\": \"a\" }");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_a_ordered_index ON test_collection USING BTREE ((document#>'{a}'));\n",
                body.toString());
    }

    @Test
    public void testDatetimeIndex() throws Exception {
        IObject options = new DSObject("{ \"datetime\": [ \"a\", \"b\" ] }");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_a_datetime_index ON test_collection USING BTREE ((parse_timestamp_immutable(document#>'{a}')));\n" +
                     "CREATE INDEX test_collection_b_datetime_index ON test_collection USING BTREE ((parse_timestamp_immutable(document#>'{b}')));\n",
                body.toString());
    }

    @Test
    public void testDatetimeIndexForOneField() throws Exception {
        IObject options = new DSObject("{ \"datetime\": \"a\" }");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_a_datetime_index ON test_collection USING BTREE ((parse_timestamp_immutable(document#>'{a}')));\n",
                body.toString());
    }

    @Test
    public void testTagsIndex() throws Exception {
        IObject options = new DSObject("{ \"tags\": [ \"a\", \"b\" ] }");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_a_tags_index ON test_collection USING GIN ((document#>'{a}'));\n" +
                     "CREATE INDEX test_collection_b_tags_index ON test_collection USING GIN ((document#>'{b}'));\n",
                body.toString());
    }

    @Test
    public void testTagsIndexForOneField() throws Exception {
        IObject options = new DSObject("{ \"tags\": \"a\" }");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_a_tags_index ON test_collection USING GIN ((document#>'{a}'));\n",
                body.toString());
    }

    @Test
    public void testFulltextIndex() throws Exception {
        IObject options = new DSObject("{ " +
                "\"fulltext\": [ \"a\", \"b\" ]," +
                "\"language\": \"russian\"" +
                "}");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_fulltext_russian_index ON test_collection USING GIN (fulltext_russian);\n" +
                        "CREATE FUNCTION test_collection_fulltext_russian_update_trigger() RETURNS trigger AS $$\n" +
                        "begin\n" +
                        "new.fulltext_russian := " +
                        "to_tsvector('russian', coalesce((new.document#>'{a}')::text,'') || ' ' || coalesce((new.document#>'{b}')::text,''));\n" +
                        "return new;\n" +
                        "end\n" +
                        "$$ LANGUAGE plpgsql;\n" +
                        "CREATE TRIGGER test_collection_fulltext_russian_update_trigger BEFORE INSERT OR UPDATE " +
                        "ON test_collection FOR EACH ROW EXECUTE PROCEDURE " +
                        "test_collection_fulltext_russian_update_trigger();\n",
                body.toString());
    }

    @Test
    public void testFulltextIndexForOneField() throws Exception {
        IObject options = new DSObject("{ " +
                "\"fulltext\": \"text\"," +
                "\"language\": \"russian\"" +
                "}");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_fulltext_russian_index ON test_collection USING GIN (fulltext_russian);\n" +
                        "CREATE FUNCTION test_collection_fulltext_russian_update_trigger() RETURNS trigger AS $$\n" +
                        "begin\n" +
                        "new.fulltext_russian := " +
                        "to_tsvector('russian', coalesce((new.document#>'{text}')::text,''));\n" +
                        "return new;\n" +
                        "end\n" +
                        "$$ LANGUAGE plpgsql;\n" +
                        "CREATE TRIGGER test_collection_fulltext_russian_update_trigger BEFORE INSERT OR UPDATE " +
                        "ON test_collection FOR EACH ROW EXECUTE PROCEDURE " +
                        "test_collection_fulltext_russian_update_trigger();\n",
                body.toString());
    }

    @Test(expected = Exception.class)
    public void testWrongIndexFormat() throws Exception {
        IObject options = new DSObject("{ \"ordered\": 123 }");
        IndexCreators.writeCreateIndexes(body, collection, options);
    }

}
