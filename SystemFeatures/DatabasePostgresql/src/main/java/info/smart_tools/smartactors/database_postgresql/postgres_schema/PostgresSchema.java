package info.smart_tools.smartactors.database_postgresql.postgres_schema;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes.IndexCreators;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes.IndexDroppers;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.FieldPath;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.PostgresFieldPath;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.io.IOException;
import java.io.Writer;

/**
 * A set of static methods to take statements to be executed in the Postgres database.
 * The statements are to perform operations over jsonb documents.
 */
public final class PostgresSchema {

    /**
     * Name of the DOCUMENT column.
     */
    public static final String DOCUMENT_COLUMN = "document";

    /**
     * Name of the column for fulltext search.
     */
    public static final String FULLTEXT_COLUMN = "fulltext";

    /**
     * Pattern for the document field with the document ID.
     */
    public static final String ID_FIELD_PATTERN = "%sID";

    /**
     * Dictionary for Full Text Search
     */
    public static final String DEFAULT_FTS_DICTIONARY = "english";

    /**
     * Default page size. How many documents to return when the paging is not defined.
     */
    public static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * Private constructor to avoid instantiation.
     */
    private PostgresSchema() {
    }

    /**
     * Fills the statement body with the CREATE TABLE sentence and CREATE INDEX sentences
     * to create the desired collection and it's indexes.
     * @param statement statement to fill the body
     * @param collection collection name to use to construct the sequence name
     * @param options document describing a set of options for the collection creation
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void create(final QueryStatement statement, final CollectionName collection, final IObject options)
            throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();
            CreateClauses.writeFunctions(body);
            body.write("CREATE TABLE ");
            body.write(collection.toString());
            body.write(" (");
            body.write(DOCUMENT_COLUMN);
            body.write(" jsonb NOT NULL");
            CreateClauses.writeFullTextColumn(body, options);
            body.write(");\n");
            CreateClauses.writePrimaryKey(body, collection);
            if (options != null) {
                IndexCreators.writeCreateIndexes(body, collection, options);
            }
        } catch (Exception e) {
            throw new QueryBuildException("Failed to build create body", e);
        }
    }

    /**
     * Fills the statement body with the ALTER TABLE ADD COLUMN sentence and
     * CREATE INDEX sentences to add the column and corresponding index for
     * full text search operation with desired language.
     * @param statement statement to fill the body
     * @param collection collection name to use to construct the sequence name
     * @param options document describing a set of options for the collection creation
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void addIndexes(final QueryStatement statement, final CollectionName collection, final IObject options)
            throws QueryBuildException {
        try {
            if (options == null) {
                throw new QueryBuildException("Options for db.collection.addindexes must not be null");
            }
            Writer body = statement.getBodyWriter();
            AlterClauses.writeAddFulltextColumn(body, collection, options);
            IndexCreators.writeCreateIndexes(body, collection, options);
        } catch (Exception e) {
            throw new QueryBuildException("Failed to build create body", e);
        }
    }

    /**
     * Fills the statement body with the ALTER TABLE DROP COLUMN sentence and
     * DROP INDEX sentences to drop the column and corresponding index for
     * full text search operation with desired language.
     * @param statement statement to fill the body
     * @param collection collection name to use to construct the sequence name
     * @param options document describing a set of options for the collection creation
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void dropIndexes(final QueryStatement statement, final CollectionName collection, final IObject options)
            throws QueryBuildException {
        try {
            if (options == null) {
                throw new QueryBuildException("Options for db.collection.dropindexes must not be null");
            }
            Writer body = statement.getBodyWriter();
            IndexDroppers.writeDropIndexes(body, collection, options);
            AlterClauses.writeDropFulltextColumn(body, collection, options);
        } catch (Exception e) {
            throw new QueryBuildException("Failed to build create body", e);
        }
    }

    /**
     * Fills the statement body with the ALTER TABLE ADD COLUMN sentence and
     * CREATE INDEX sentences to add the column and corresponding index for
     * full text search operation with desired language.
     * It executes indexes addition inside the transaction.
     * @param statement statement to fill the body
     * @param collection collection name to use to construct the sequence name
     * @param options document describing a set of options for the collection creation
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void addIndexesSafe(final QueryStatement statement, final CollectionName collection, final IObject options)
            throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();
            body.write("BEGIN;");
            addIndexes(statement, collection, options);
            body.write("COMMIT;");
        } catch (QueryBuildException e) {
            throw e;
        } catch (Exception e) {
            throw new QueryBuildException("Failed to build create body", e);
        }
    }

    /**
     * Returns the path to ID property of the document in the specified collection.
     * @param collection name of the collection
     * @return path to field like "collectionID"
     * @throws QueryBuildException if the path is invalid
     */
    public static FieldPath getIdFieldPath(final CollectionName collection) throws QueryBuildException {
        return PostgresFieldPath.fromString(String.format(ID_FIELD_PATTERN, collection.toString()));
    }

    /**
     * Fills the statement body with the collection name for the INSERT statement.
     * @param statement statement to fill the body
     * @param collection collection name to use as the table name
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void insert(final QueryStatement statement, final CollectionName collection) throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();
            body.write("INSERT INTO ");
            body.write(collection.toString());
            body.write(" (");
            body.write(DOCUMENT_COLUMN);
            body.write(") VALUES (?::jsonb)");
        } catch (IOException e) {
            throw new QueryBuildException("Failed to build insert body", e);
        }
    }

    /**
     * Fills the statement body with the collection name for the UPDATE statement
     * @param statement statement to fill the body
     * @param collection collection name to use as the table name
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void update(final QueryStatement statement, final CollectionName collection) throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();
            body.write("UPDATE ");
            body.write(collection.toString());
            body.write(" SET ");
            body.write(DOCUMENT_COLUMN);
            body.write(" = ?::jsonb WHERE (");
            body.write(getIdFieldPath(collection).toSQL());
            body.write(") = to_json(?)::jsonb");
        } catch (IOException e) {
            throw new QueryBuildException("Failed to build update body", e);
        }
    }

    /**
     * Fills the statement body with the collection name for the SELECT statement to find the document by ID.
     * @param statement statement to fill the body
     * @param collection collection name to use as the table name
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void getById(final QueryStatement statement, final CollectionName collection) throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();
            body.write("SELECT ");
            body.write(DOCUMENT_COLUMN);
            body.write(" FROM ");
            body.write(collection.toString());
            body.write(" WHERE (");
            body.write(getIdFieldPath(collection).toSQL());
            body.write(") = to_json(?)::jsonb");
        } catch (IOException e) {
            throw new QueryBuildException("Failed to build getById body", e);
        }
    }

    /**
     * Fills the statement body and it's list of parameter setters with the collection name and WHERE clause
     * for the SELECT statement to search the document by it's fields.
     * <p>
     *     The example of search criteria.
     * {@code
     *   {
     *       "filter": {
     *           "$or": [
     *               "a": { "$eq": "b" },
     *               "b": { "$gt": 42 }
     *           ]
     *       },
     *       "page": {
     *           "size": 50,
     *           "number": 2
     *       },
     *       "sort": [
     *           { "a": "asc" },
     *           { "b": "desc" }
     *       ]
     *   }
     * }
     * </p>
     * @param statement statement to fill the body and add parameter setters
     * @param collection collection name to use as the table name
     * @param criteria complex JSON describing the search criteria
     * @throws QueryBuildException when something goes wrong
     */
    public static void search(final QueryStatement statement, final CollectionName collection, final IObject criteria)
            throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();

            body.write("SELECT ");
            body.write(DOCUMENT_COLUMN);
            body.write(" FROM ");
            body.write(collection.toString());

            if (criteria == null) {
                SearchClauses.writeDefaultPaging(statement);
                return;
            }
            SearchClauses.writeSearchWhere(statement, criteria);
            SearchClauses.writeSearchOrder(statement, criteria);
            SearchClauses.writeSearchPaging(statement, criteria);
        } catch (Exception e) {
            throw new QueryBuildException("Failed to build search query", e);
        }
    }

    /**
     * Fills the statement body with the collection name for the DELETE statement to delete the document by ID.
     * @param statement statement to fill the body
     * @param collection collection name to use as the table name
     * @throws QueryBuildException if the statement body cannot be built
     */
    public static void delete(final QueryStatement statement, final CollectionName collection) throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();
            body.write("DELETE FROM ");
            body.write(collection.toString());
            body.write(" WHERE (");
            body.write(getIdFieldPath(collection).toSQL());
            body.write(") = to_json(?)::jsonb");
        } catch (IOException e) {
            throw new QueryBuildException("Failed to build delete body", e);
        }
    }

    /**
     * Fills the statement body and it's list of parameter setters with the collection name and WHERE clause
     * for the SELECT COUNT(*) statement to count the documents in the collection.
     * <p>
     *     The example of search criteria.
     * {@code
     *   {
     *       "filter": {
     *           "$or": [
     *               "a": { "$eq": "b" },
     *               "b": { "$gt": 42 }
     *           ]
     *       }
     *   }
     * }
     * </p>
     * @param statement statement to fill the body and add parameter setters
     * @param collection collection name to use as the table name
     * @param criteria complex JSON describing the search criteria
     * @throws QueryBuildException when something goes wrong
     */
    public static void count(final QueryStatement statement, final CollectionName collection, final IObject criteria)
            throws QueryBuildException {
        try {
            Writer body = statement.getBodyWriter();

            body.write("SELECT COUNT(*) FROM ");
            body.write(collection.toString());

            if (criteria == null) {
                return;
            }
            SearchClauses.writeSearchWhere(statement, criteria);
        } catch (Exception e) {
            throw new QueryBuildException("Failed to build count query", e);
        }
    }

}
