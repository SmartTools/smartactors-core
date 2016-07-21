package info.smart_tools.smartactors.core.db_tasks.psql.create_collection;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.PSQLFieldPath;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder for query statement {@link QueryStatement} of create collection query.
 */
final class QueryStatementBuilder implements IQueryStatementBuilder {
    private String collection;
    private String createCollectionQuery;
    private Map<String, String> indexes;
    private IndexBuilder indexBuilder;

    private static final String FIRST_PART_TEMPLATE = "CREATE TABLE ";
    private static final String SECOND_PART_TEMPLATE = " (id BIGSERIAL PRIMARY KEY, document JSONB NOT NULL);\n";
    private static final int TEMPLATE_SIZE = FIRST_PART_TEMPLATE.length() + SECOND_PART_TEMPLATE.length();

    /**
     * Default constructor for {@link QueryStatementBuilder}.
     *              Creates a new instance of {@link QueryStatementBuilder}.
     */
    private QueryStatementBuilder() {
        this.indexBuilder = IndexBuilder.create();
    }

    /**
     * Factory method for creation a new instance of {@link QueryStatementBuilder}.
     *
     * @return a new instance of {@link QueryStatementBuilder}.
     */
    static QueryStatementBuilder create() {
        return new QueryStatementBuilder();
    }

    /**
     * Appends collection name to final query statement.
     *
     * @param collectionName - a collection name for which to create query statement.
     *
     * @return a link to yourself {@link QueryStatementBuilder}.
     */
    QueryStatementBuilder withCollection(@Nonnull final String collectionName) {
        collection = collectionName;

        StringBuilder createCollectionQueryBuilder =
                new StringBuilder(TEMPLATE_SIZE + collection.length());
        createCollectionQuery = createCollectionQueryBuilder
                .append(FIRST_PART_TEMPLATE)
                .append(collection)
                .append(SECOND_PART_TEMPLATE)
                .toString();

        return this;
    }

    /**
     * Appends list of indexes to some collection.
     *
     * @param columnIndexes - list of indexes to some collection.
     *
     * @return a link to yourself {@link QueryStatementBuilder}.
     */
    QueryStatementBuilder withIndexes(@Nonnull final Map<String, String> columnIndexes) {
        indexes = new HashMap<>(columnIndexes);
        if (!indexes.containsKey("id")) {
            indexes.put("id", "id");
        }

        return this;
    }

    /**
     * Builds query statement of create collection query.
     *
     * @return formed and filled query statement {@link QueryStatement}.
     *
     * @throws QueryBuildException when a some critical error in during building query statement.
     */
    @Override
    public QueryStatement build() throws QueryBuildException {
        requiresNonnull(collection, "The collection should not be a null or empty, should try invoke 'withCollection'.");
        requiresNonnull(indexes, "The list of indexes should not be a null, should try invoke 'withIndexes'.");

        try {
            QueryStatement preparedQuery = new QueryStatement();
            Writer writer = preparedQuery.getBodyWriter();

            writer.write(createCollectionQuery);
            indexBuilder.withCollection(collection);
            for (Map.Entry<String, String> entry : indexes.entrySet()) {
                FieldPath field = PSQLFieldPath.fromString(entry.getKey());
                String indexType = entry.getValue();
                String index = indexBuilder
                        .withIndex(indexType)
                        .withField(field.getSQLRepresentation())
                        .build();

                writer.write(index);
            }

            return preparedQuery;
        } catch (IOException | IllegalArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }

    private void requiresNonnull(final String str, final String message) throws QueryBuildException {
        if (str == null || str.isEmpty()) {
            throw new QueryBuildException(message);
        }
    }

    private void requiresNonnull(final Map map, final String message) throws QueryBuildException {
        if (map == null) {
            throw new QueryBuildException(message);
        }
    }
}
