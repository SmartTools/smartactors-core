package info.smart_tools.smartactors.core.db_tasks.psql.search;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IComplexQueryStatementBuilder;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.SQLOrderWriter;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.SQLPagingWriter;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.sql_commons.IDeclaredParam;
import info.smart_tools.smartactors.core.sql_commons.QueryConditionResolver;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.io.Writer;
import java.util.List;

/**
 *
 */
final class SearchQueryStatementBuilder implements IComplexQueryStatementBuilder {
    private String collection;
    private Object criteria;
    private List<IObject> orderBy;
    private List<IDeclaredParam> declaredParams;

    private final QueryConditionResolver conditionResolver;
    private final SQLOrderWriter orderWriter;
    private final SQLPagingWriter pagingWriter;


    private static final String FIRST_PART_TEMPLATE = "SELECT * FROM ";
    private static final String SECOND_PART_TEMPLATE = " WHERE";

    private static final int TEMPLATE_SIZE = FIRST_PART_TEMPLATE.length() +
            SECOND_PART_TEMPLATE.length();

    private SearchQueryStatementBuilder(final QueryConditionResolver conditionResolver,
                                        final SQLOrderWriter orderWriter,
                                        final SQLPagingWriter pagingWriter
    ) {
        this.conditionResolver = conditionResolver;
        this.orderWriter = orderWriter;
        this.pagingWriter = pagingWriter;
    }

    /**
     *
     * @param conditionResolver
     * @param orderWriter
     * @param pagingWriter
     * @return
     */
    public static SearchQueryStatementBuilder create(final QueryConditionResolver conditionResolver,
                                                     final SQLOrderWriter orderWriter,
                                                     final SQLPagingWriter pagingWriter
    ) {
        return new SearchQueryStatementBuilder(conditionResolver, orderWriter, pagingWriter);
    }

    /**
     *
     * @param collection
     * @return
     */
    SearchQueryStatementBuilder withCollection(@Nonnull final String collection) {
        this.collection = collection;
        return this;
    }

    /**
     *
     * @param criteria
     * @return
     */
    SearchQueryStatementBuilder withCriteria(final Object criteria) {
        this.criteria = criteria;
        return this;
    }

    /**
     *
     * @param orderBy
     * @return
     */
    SearchQueryStatementBuilder withOrderBy(final List<IObject> orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     *
     * @param declaredParams
     * @return
     */
    public SearchQueryStatementBuilder withDeclaredParams(@Nonnull final List<IDeclaredParam> declaredParams) {
        this.declaredParams = declaredParams;
        return this;
    }

    /**
     *
     * @return
     * @throws QueryBuildException
     */
    public QueryStatement build() throws QueryBuildException {
        try {
            QueryStatement queryStatement = new QueryStatement();
            Writer writer = queryStatement.getBodyWriter();
            StringBuilder queryBuilder = new StringBuilder(TEMPLATE_SIZE);
            queryBuilder
                    .append(FIRST_PART_TEMPLATE)
                    .append(collection)
                    .append(SECOND_PART_TEMPLATE);
            writer.write(queryBuilder.toString());
            conditionResolver
                    .resolve(null)
                    .write(queryStatement, conditionResolver, null, criteria, declaredParams);
            orderWriter.write(queryStatement, orderBy);
            pagingWriter.write(queryStatement);
            writer.write(";");

            return queryStatement;
        } catch (QueryBuildException e) {
            throw new QueryBuildException("Error writing query statement: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }
}
