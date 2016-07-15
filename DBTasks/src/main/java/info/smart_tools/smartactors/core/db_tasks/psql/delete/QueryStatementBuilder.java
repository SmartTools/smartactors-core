package info.smart_tools.smartactors.core.db_tasks.psql.delete;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * Builder for query statement {@link QueryStatement} of delete document by id query.
 */
final class QueryStatementBuilder implements IQueryStatementBuilder {
    private String collection;

    private static final String FIRST_PART_TEMPLATE = "DELETE FROM ";
    private static final String SECOND_PART_TEMPLATE = " WHERE id = ?;";
    private static final int TEMPLATE_SIZE = FIRST_PART_TEMPLATE.length() + SECOND_PART_TEMPLATE.length();

    /**
     * Default constructor. Creates a new instance of {@link QueryStatementBuilder}.
     */
    private QueryStatementBuilder() {}

    /**
     * Factory method for creates a new instance of {@link QueryStatementBuilder}.
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
        return this;
    }

    /**
     * Builds query statement of find by id query.
     *
     * @return formed and filled query statement {@link QueryStatement}.
     *
     * @throws QueryBuildException when a some critical error in during building query statement.
     */
    public QueryStatement build() throws QueryBuildException {
        requiresNonnull(collection, "The collection should not be a null or empty, should try invoke 'withCollection'.");

        try {
            QueryStatement preparedQuery = new QueryStatement();
            Writer writer = preparedQuery.getBodyWriter();
            StringBuilder queryBuilder = new StringBuilder(TEMPLATE_SIZE + collection.length());

            queryBuilder
                    .append(FIRST_PART_TEMPLATE)
                    .append(collection)
                    .append(SECOND_PART_TEMPLATE);
            writer.write(queryBuilder.toString());

            return preparedQuery;
        } catch (IOException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }

    private void requiresNonnull(final String str, final String message) throws QueryBuildException {
        if (str == null || str.isEmpty()) {
            throw new QueryBuildException(message);
        }
    }
}
