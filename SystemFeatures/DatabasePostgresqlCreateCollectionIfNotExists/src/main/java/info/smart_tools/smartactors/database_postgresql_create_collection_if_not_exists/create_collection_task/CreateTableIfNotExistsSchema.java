package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.CreateClauses;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes.IndexCreators;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.io.Writer;

public final class CreateTableIfNotExistsSchema {
    /**
     * Name of the DOCUMENT column.
     */
    public static final String DOCUMENT_COLUMN = "document";

    private CreateTableIfNotExistsSchema() {
    }


    /**
     * Fills the statement body with the CREATE TABLE IF NOT EXISTS sentence and CREATE INDEX sentences
     * to create the desired collection and it's indexes.
     *
     * @param statement  statement to fill the body
     * @param collection collection name to use to construct the sequence name
     * @param options    document describing a set of options for the collection creation
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void createIfNotExists(final QueryStatement statement, final CollectionName collection, final IObject options)
            throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();
            CreateClauses.writeFunctions(body);
            body.write("CREATE TABLE IF NOT EXISTS ");
            body.write(collection.toString());
            body.write(" (");
            body.write(DOCUMENT_COLUMN);
            body.write(" jsonb NOT NULL");
            CreateClauses.writeFullTextColumn(body, options);
            body.write(");\n");
            CreateIndexIfNotExistsSchema.writePrimaryKeyIfNotExists(body, collection);
            if (options != null) {
                IndexCreators.writeCreateIndexes(body, collection, options);
            }
        } catch (Exception e) {
            throw new QueryBuildException("Failed to build create if not exists body", e);
        }
    }
}
