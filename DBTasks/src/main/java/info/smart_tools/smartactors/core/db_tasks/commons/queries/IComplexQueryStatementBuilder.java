package info.smart_tools.smartactors.core.db_tasks.commons.queries;

import info.smart_tools.smartactors.core.sql_commons.IDeclaredParam;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Builder for a complex query statement.
 * Adds in list of declared parameters {@link IComplexQueryStatementBuilder#withDeclaredParams(List)}
 *              a query parameters from criteria in the order of their processing
 *              for further insertion of parameter values in prepared compiled query.
 */
public interface IComplexQueryStatementBuilder extends IQueryStatementBuilder {
    /**
     * Adds in builder a list that must to save
     *          the required parameters in the order of their processing.
     *
     * @param declaredParams - declared in a criteria(field in query message) query parameters.
     * @return a link of yourself {@link IComplexQueryStatementBuilder}.
     */
    IComplexQueryStatementBuilder withDeclaredParams(@Nonnull final List<IDeclaredParam> declaredParams);
}
