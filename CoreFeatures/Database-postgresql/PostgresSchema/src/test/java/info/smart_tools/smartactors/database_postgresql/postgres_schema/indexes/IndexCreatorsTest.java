package info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Test for different index creation statements.
 */
public class IndexCreatorsTest {

    private CollectionName collection;
    private StringWriter body;

    @BeforeClass
    public static void initIOC() throws PluginException, ProcessExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        bootstrap.start();
    }

    @Before
    public void setUp() throws QueryBuildException {
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
    public void testOrderedIndexForOneFieldWithTypeCast() throws Exception {
        IObject options = new DSObject("{ \"ordered\": { \"fieldName\": \"a\", \"type\": \"decimal\" }}");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_a_ordered_index ON test_collection USING BTREE (((document#>>'{a}')::decimal));\n",
                body.toString());
    }

    @Test
    public void testOrderedIndexForSeveralFieldsWithTypeCast() throws Exception {
        IObject options = new DSObject("{ \"ordered\": [ {\"fieldName\": \"a\", \"type\": \"decimal\"}, \"b\" ]}");
        IndexCreators.writeCreateIndexes(body, collection, options);
        assertEquals("CREATE INDEX test_collection_a_ordered_index ON test_collection USING BTREE (((document#>>'{a}')::decimal));\n" +
                        "CREATE INDEX test_collection_b_ordered_index ON test_collection USING BTREE ((document#>'{b}'));\n",
                body.toString());
    }

    @Test(expected = QueryBuildException.class)
    public void testOrderedIndexForOneFieldWithTypeCastFailure() throws Exception {
        IObject options = new DSObject("{ \"ordered\": { }}");
        IndexCreators.writeCreateIndexes(body, collection, options);
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
