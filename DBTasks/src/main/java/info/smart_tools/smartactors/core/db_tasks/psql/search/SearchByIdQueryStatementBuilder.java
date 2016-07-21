package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Builder for query statement {@link QueryStatement} of find by id query.
 */
final class SearchByIdQueryStatementBuilder implements IQueryStatementBuilder {
    private String collection;

    private final static String FIRST_PART_TEMPLATE = "SELECT * FROM ";
    private final static String SECOND_PART_TEMPLATE = " WHERE id=?;";

    private final static int TEMPLATE_SIZE = FIRST_PART_TEMPLATE.length() +
            SECOND_PART_TEMPLATE.length();

    /**
     * Default constructor. Creates a new instance of {@link SearchByIdQueryStatementBuilder}.
     */
    private SearchByIdQueryStatementBuilder() {}

    /**
     * Factory method for creates a new instance of {@link SearchByIdQueryStatementBuilder}.
     *
     * @return a new instance of {@link SearchByIdQueryStatementBuilder}.
     */
    static SearchByIdQueryStatementBuilder create() { return new SearchByIdQueryStatementBuilder(); }

    /**
     * Appends collection name to final query statement.
     *
     * @param collection - a collection name for which to create query statement.
     *
     * @return a link to yourself {@link SearchByIdQueryStatementBuilder}.
     */
    SearchByIdQueryStatementBuilder withCollection(@Nonnull final String collection) {
        this.collection = collection;
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
            StringBuilder queryBuilder = new StringBuilder(TEMPLATE_SIZE + collection.length());
            preparedQuery.getBodyWriter().write(queryBuilder
                    .append(FIRST_PART_TEMPLATE)
                    .append(collection)
                    .append(SECOND_PART_TEMPLATE)
                    .toString());

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
