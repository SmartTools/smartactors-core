package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.PostgresSchema;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.FieldPath;

import java.io.IOException;
import java.io.Writer;

public final class CreateIndexIfNotExistsSchema {
    private CreateIndexIfNotExistsSchema() {
    }

    /**
     * Writes the primary key definition if not exists.
     *
     * @param body       body of SQL query to write
     * @param collection name of the collection/table
     * @throws IOException         if write to body is not possible
     * @throws QueryBuildException if there is a syntax error
     */
    static void writePrimaryKeyIfNotExists(final Writer body, final CollectionName collection) throws IOException, QueryBuildException {
        String collectionName = collection.toString();
        FieldPath idPath = PostgresSchema.getIdFieldPath(collection);
        body.write(String.format(
                "do\n" +
                        "$$\n" +
                        "BEGIN\n" +
                        "IF to_regclass('%1$s_pkey') IS NULL THEN\n" +
                        "    CREATE UNIQUE INDEX %1$s_pkey ON %1$s USING BTREE ((%2$s));\n" +
                        "end if;\n" +
                        "end \n" +
                        "$$;",
                collectionName, idPath.toSQL())
        );
    }
}
