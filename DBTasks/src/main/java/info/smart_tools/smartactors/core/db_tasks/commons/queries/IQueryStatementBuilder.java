package info.smart_tools.smartactors.core.db_tasks.commons.queries;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

/**
 * Builder for a simple query statement.
 */
public interface IQueryStatementBuilder {
    /**
     * Build and gives a prepared instance of {@link QueryStatement}.
     *
     * @return the prepared instance of {@link QueryStatement}.
     * @exception QueryBuildException when errors in during building query statement.
     */
    QueryStatement build() throws QueryBuildException;
}
