package info.smart_tools.smartactors.database_postgresql.postgres_schema;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test for SQL statements.
 */
public class PostgresSchemaTest extends IOCInitializer {

    private CollectionName collection;
    private QueryStatement statement;
    private StringWriter body;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        collection = CollectionName.fromString("test_collection");
        body = new StringWriter();
        statement = mock(QueryStatement.class);
        when(statement.getBodyWriter()).thenReturn(body);
    }

    @Test
    public void testInsert() throws QueryBuildException {
        PostgresSchema.insert(statement, collection);
        assertEquals("INSERT INTO test_collection (document) " +
                "VALUES (?::jsonb)", body.toString());
    }

    @Test
    public void testUpdate() throws QueryBuildException {
        PostgresSchema.update(statement, collection);
        assertEquals("UPDATE test_collection " +
                "SET document = ?::jsonb " +
                "WHERE (document#>'{test_collectionID}') = to_json(?)::jsonb", body.toString());
    }

    @Test
    public void testGetById() throws QueryBuildException {
        PostgresSchema.getById(statement, collection);
        assertEquals("SELECT document FROM test_collection " +
                "WHERE (document#>'{test_collectionID}') = to_json(?)::jsonb", body.toString());
    }

    @Test
    public void testSearch() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } } }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection " +
                "WHERE ((((document#>'{a}')=to_json(?)::jsonb))) " +
                "LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(2)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithPaging() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } }," +
                " \"page\": { \"size\": 10, \"number\": 3 } }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection " +
                "WHERE ((((document#>'{a}')=to_json(?)::jsonb))) " +
                "LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(2)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithSorting() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } }," +
                " \"sort\": [ { \"a\": \"desc\" } ] }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection " +
                "WHERE ((((document#>'{a}')=to_json(?)::jsonb))) " +
                "ORDER BY(document#>'{a}')DESC " +
                "LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(2)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithPagingAndSorting() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } }," +
                " \"page\": { \"size\": 10, \"number\": 3 }, " +
                " \"sort\": [ { \"a\": \"desc\" } ] }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection " +
            "WHERE ((((document#>'{a}')=to_json(?)::jsonb))) " +
            "ORDER BY(document#>'{a}')DESC LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(2)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithPagingByLimitAndOffset() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } }," +
            " \"page\": { \"limit\": 10, \"offset\": 100 } }");
        PostgresSchema.searchByLimitAndOffset(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection " +
            "WHERE ((((document#>'{a}')=to_json(?)::jsonb))) " +
            "LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(2)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithPagingByLimitAndDefaultOffset() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } }," +
            " \"page\": { \"limit\": 10 } }");
        PostgresSchema.searchByLimitAndOffset(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection " +
                "WHERE ((((document#>'{a}')=to_json(?)::jsonb))) " +
                "LIMIT(?)OFFSET(?)",
                body.toString());
        verify(statement, times(2)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithEmptyFilter() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { } }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection WHERE (TRUE) LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(1)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithEmptyCriteria() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(1)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithNullCriteria() throws InvalidArgumentException, QueryBuildException {
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, null);
        assertEquals("SELECT document FROM test_collection LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(1)).pushParameterSetter(any());
    }

    @Test
    public void testCreate() throws QueryBuildException {
        PostgresSchema.create(statement, collection, null);
        assertEquals(
        "BEGIN; SELECT pg_advisory_xact_lock(2142616274639426746); " +
                "CREATE OR REPLACE FUNCTION parse_timestamp_immutable(source jsonb) RETURNS timestamptz AS $$ " +
                        "BEGIN RETURN source::text::timestamptz; END; " +
                        "$$ LANGUAGE 'plpgsql' IMMUTABLE; COMMIT;\n" +
                "BEGIN; SELECT pg_advisory_xact_lock(2142616274639426746); " +
                "CREATE OR REPLACE FUNCTION bigint_to_jsonb_immutable(source bigint) RETURNS jsonb AS $$ " +
                        "BEGIN RETURN to_json(source)::jsonb; END; " +
                        "$$ LANGUAGE 'plpgsql' IMMUTABLE; COMMIT;\n" +
                "CREATE TABLE test_collection (document jsonb NOT NULL);\n" +
                "CREATE UNIQUE INDEX test_collection_pkey ON test_collection USING BTREE ((document#>'{test_collectionID}'));\n",
                body.toString()
        );
    }

    @Test
    public void testCreateWithIndexes() throws QueryBuildException, InvalidArgumentException {
        IObject options = new DSObject("{ \"ordered\": \"a\"," +
                "\"fulltext\": \"b\"," +
                "\"language\": \"english\"" +
                "}");
        PostgresSchema.create(statement, collection, options);
        assertEquals(
                "BEGIN; SELECT pg_advisory_xact_lock(2142616274639426746); " +
                "CREATE OR REPLACE FUNCTION parse_timestamp_immutable(source jsonb) RETURNS timestamptz AS $$ " +
                        "BEGIN RETURN source::text::timestamptz; END; " +
                        "$$ LANGUAGE 'plpgsql' IMMUTABLE; COMMIT;\n" +
                "BEGIN; SELECT pg_advisory_xact_lock(2142616274639426746); " +
                "CREATE OR REPLACE FUNCTION bigint_to_jsonb_immutable(source bigint) RETURNS jsonb AS $$ " +
                        "BEGIN RETURN to_json(source)::jsonb; END; " +
                        "$$ LANGUAGE 'plpgsql' IMMUTABLE; COMMIT;\n" +
                "CREATE TABLE test_collection (document jsonb NOT NULL, fulltext_english tsvector);\n" +
                "CREATE UNIQUE INDEX test_collection_pkey ON test_collection USING BTREE ((document#>'{test_collectionID}'));\n" +
                "CREATE INDEX test_collection_a_ordered_index ON test_collection USING BTREE ((document#>'{a}'));\n" +
                "CREATE INDEX test_collection_fulltext_english_index ON test_collection USING GIN (fulltext_english);\n" +
                "CREATE FUNCTION test_collection_fulltext_english_update_trigger() RETURNS trigger AS $$\n" +
                "begin\n" +
                "new.fulltext_english := " +
                "to_tsvector('english', coalesce((new.document#>'{b}')::text,''));\n" +
                "return new;\n" +
                "end\n" +
                "$$ LANGUAGE plpgsql;\n" +
                "CREATE TRIGGER test_collection_fulltext_english_update_trigger BEFORE INSERT OR UPDATE " +
                "ON test_collection FOR EACH ROW EXECUTE PROCEDURE " +
                "test_collection_fulltext_english_update_trigger();\n",
                body.toString());
    }

    @Test
    public void testAddIndexes() throws QueryBuildException, InvalidArgumentException {
        IObject options = new DSObject("{ \"ordered\": \"a\"," +
                "\"fulltext\": \"b\"," +
                "\"datetime\": \"date\"," +
                "\"tags\": \"tags\"," +
                "\"language\": \"russian\"" +
                "}");
        PostgresSchema.addIndexes(statement, collection, options);
        assertEquals("ALTER TABLE test_collection ADD COLUMN fulltext_russian tsvector DEFAULT NULL;\n" +
                        "UPDATE test_collection SET fulltext_russian := to_tsvector('russian', coalesce((document#>'{b}')::text,''));\n" +
                        "CREATE INDEX test_collection_a_ordered_index ON test_collection USING BTREE ((document#>'{a}'));\n" +
                        "CREATE INDEX test_collection_date_datetime_index ON test_collection USING BTREE ((parse_timestamp_immutable(document#>'{date}')));\n" +
                        "CREATE INDEX test_collection_fulltext_russian_index ON test_collection USING GIN (fulltext_russian);\n" +
                        "CREATE FUNCTION test_collection_fulltext_russian_update_trigger() RETURNS trigger AS $$\n" +
                        "begin\n" +
                        "new.fulltext_russian := " +
                        "to_tsvector('russian', coalesce((new.document#>'{b}')::text,''));\n" +
                        "return new;\n" +
                        "end\n" +
                        "$$ LANGUAGE plpgsql;\n" +
                        "CREATE TRIGGER test_collection_fulltext_russian_update_trigger BEFORE INSERT OR UPDATE " +
                        "ON test_collection FOR EACH ROW EXECUTE PROCEDURE " +
                        "test_collection_fulltext_russian_update_trigger();\n" +
                        "CREATE INDEX test_collection_tags_tags_index ON test_collection USING GIN ((document#>'{tags}'));\n",
                body.toString());
    }

    @Test
    public void testDropIndexes() throws QueryBuildException, InvalidArgumentException {
        IObject options = new DSObject("{ \"ordered\": \"a\"," +
                "\"fulltext\": \"b\"," +
                "\"datetime\": \"date\"," +
                "\"tags\": \"tags\"," +
                "\"language\": \"russian\"" +
                "}");
        PostgresSchema.dropIndexes(statement, collection, options);
        assertEquals("DROP INDEX test_collection_a_ordered_index;\n" +
                        "DROP INDEX test_collection_date_datetime_index;\n" +
                        "DROP TRIGGER test_collection_fulltext_russian_update_trigger ON test_collection;\n" +
                        "DROP FUNCTION test_collection_fulltext_russian_update_trigger();\n" +
                        "DROP INDEX test_collection_fulltext_russian_index;\n" +
                        "DROP INDEX test_collection_tags_tags_index;\n" +
                        "ALTER TABLE test_collection DROP COLUMN fulltext_russian;\n",
                body.toString());
    }

    @Test
    public void testDelete() throws QueryBuildException {
        PostgresSchema.delete(statement, collection);
        assertEquals("DELETE FROM test_collection " +
                "WHERE (document#>'{test_collectionID}') = to_json(?)::jsonb", body.toString());
    }

    @Test
    public void testCount() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } } }");
        PostgresSchema.count(statement, collection, criteria);
        assertEquals("SELECT COUNT(*) FROM test_collection " +
                "WHERE ((((document#>'{a}')=to_json(?)::jsonb)))", body.toString());
        verify(statement, times(1)).pushParameterSetter(any());
    }

    @Test
    public void testCountWithPaging() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } }," +
                " \"page\": { \"size\": 10, \"number\": 3 } }");
        PostgresSchema.count(statement, collection, criteria);
        assertEquals("SELECT COUNT(*) FROM test_collection " +
                "WHERE ((((document#>'{a}')=to_json(?)::jsonb)))", body.toString());
        verify(statement, times(1)).pushParameterSetter(any());
    }

    @Test
    public void testCountWithSorting() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } }," +
                " \"sort\": [ { \"a\": \"desc\" } ] }");
        PostgresSchema.count(statement, collection, criteria);
        assertEquals("SELECT COUNT(*) FROM test_collection " +
                "WHERE ((((document#>'{a}')=to_json(?)::jsonb)))", body.toString());
        verify(statement, times(1)).pushParameterSetter(any());
    }

    @Test
    public void testCountWithPagingAndSorting() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"a\": { \"$eq\": \"b\" } }," +
                " \"page\": { \"size\": 10, \"number\": 3 }, " +
                " \"sort\": [ { \"a\": \"desc\" } ] }");
        PostgresSchema.count(statement, collection, criteria);
        assertEquals("SELECT COUNT(*) FROM test_collection " +
                        "WHERE ((((document#>'{a}')=to_json(?)::jsonb)))", body.toString());
        verify(statement, times(1)).pushParameterSetter(any());
    }

    @Test
    public void testCountWithEmptyFilter() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { } }");
        PostgresSchema.count(statement, collection, criteria);
        assertEquals("SELECT COUNT(*) FROM test_collection WHERE (TRUE)", body.toString());
        verify(statement, times(0)).pushParameterSetter(any());
    }

    @Test
    public void testCountWithEmptyCriteria() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ }");
        PostgresSchema.count(statement, collection, criteria);
        assertEquals("SELECT COUNT(*) FROM test_collection", body.toString());
        verify(statement, times(0)).pushParameterSetter(any());
    }

    @Test
    public void testCountWithNullCriteria() throws InvalidArgumentException, QueryBuildException {
        PostgresSchema.count(statement, collection, null);
        assertEquals("SELECT COUNT(*) FROM test_collection", body.toString());
        verify(statement, times(0)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithNot() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"$not\": { \"a\": { \"$eq\": \"b\" }, \"c\": { \"$eq\": \"d\" } } } }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection " +
                "WHERE ((NOT((((document#>'{a}')=to_json(?)::jsonb))AND(((document#>'{c}')=to_json(?)::jsonb))))) " +
                "LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(3)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithFulltext() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"$fulltext\": \"term1 term2\" } }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection WHERE " +
                "(fulltext_english@@(to_tsquery(?,?))) LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(2)).pushParameterSetter(any());
    }

    @Test
    public void testSearchWithFulltextWithLanguage() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"$fulltext\": { \"query\":\"term1 term2\", \"language\":\"russian\" } } }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
        assertEquals("SELECT document FROM test_collection WHERE " +
                "(fulltext_russian@@(to_tsquery(?,?))) LIMIT(?)OFFSET(?)", body.toString());
        verify(statement, times(2)).pushParameterSetter(any());
    }

    @Test(expected = QueryBuildException.class)
    public void testSearchWithFulltextComplex() throws InvalidArgumentException, QueryBuildException {
        IObject criteria = new DSObject("{ \"filter\": { \"$fulltext\": { \"query\":{ \"$or\": [ \"term1\", \"term2\" ] }, \"language\":\"russian\" } } }");
        PostgresSchema.searchByPageSizeAndNumber(statement, collection, criteria);
    }
}
