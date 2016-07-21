package info.smart_tools.smartactors.core.db_tasks.psql.insert;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.io.IOException;

class QueryStatementBuilder implements IQueryStatementBuilder {
    private String collection;
    private StringBuilder queryBuilder;

    private static final String[] TEMPLATE_PARTS = { "INSERT INTO ",
            "(document) VALUES(?::jsonb) RETURNING id;" };

    private static final int TEMPLATE_SIZE = TEMPLATE_PARTS[0].length() + TEMPLATE_PARTS[1].length();

    protected QueryStatementBuilder() {}

    public static QueryStatementBuilder create() {
        return new QueryStatementBuilder();
    }

    QueryStatementBuilder withCollection(@Nonnull final String collectionName) {
        collection = collectionName;
        queryBuilder = new StringBuilder(TEMPLATE_SIZE + collection.length());
        queryBuilder
                .append(TEMPLATE_PARTS[0])
                .append(collection)
                .append(TEMPLATE_PARTS[1]);

        return this;
    }

    public QueryStatement build() throws QueryBuildException {
        try {
            requiresNonnull(collection, "The collection should not be a null or empty, " +
                    "should try invoke 'withCollection'.");
            QueryStatement preparedQuery = new QueryStatement();
            preparedQuery.getBodyWriter().write(queryBuilder.toString());

            return preparedQuery;
        } catch (IOException e) {
            throw new QueryBuildException("A query statement building error: " + e.getMessage(), e);
        }
    }

    private void requiresNonnull(final String str, final String message) throws QueryBuildException {
        if (str == null || str.isEmpty()) {
            throw new QueryBuildException(message);
        }
    }
}
