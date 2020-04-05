package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostgresCreateTableIfNotExistsTest extends IOCInitializer {
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
    public void testCreateIfNotExists() throws QueryBuildException {
        CreateTableIfNotExistsSchema.createIfNotExists(statement, collection, null);
        assertEquals(
                    "BEGIN; SELECT pg_advisory_xact_lock(2142616274639426746); " +
                "CREATE OR REPLACE FUNCTION parse_timestamp_immutable(source jsonb) RETURNS timestamptz AS $$ " +
                        "BEGIN RETURN source::text::timestamptz; END; " +
                        "$$ LANGUAGE 'plpgsql' IMMUTABLE; COMMIT;\n" +
                        "BEGIN; SELECT pg_advisory_xact_lock(2142616274639426746); " +
                        "CREATE OR REPLACE FUNCTION bigint_to_jsonb_immutable(source bigint) RETURNS jsonb AS $$ " +
                        "BEGIN RETURN to_json(source)::jsonb; END; " +
                        "$$ LANGUAGE 'plpgsql' IMMUTABLE; COMMIT;\n" +
                        "CREATE TABLE IF NOT EXISTS test_collection (document jsonb NOT NULL);\n" +
                        "do\n" +
                        "$$\n" +
                        "BEGIN\n" +
                        "IF to_regclass('test_collection_pkey') IS NULL THEN\n" +
                        "    CREATE UNIQUE INDEX test_collection_pkey ON test_collection USING BTREE ((document#>'{test_collectionID}'));\n" +
                        "end if;\n" +
                        "end \n" +
                        "$$;",
                body.toString());
    }
}
