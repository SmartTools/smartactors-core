package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

/**
 *
 */
@FunctionalInterface
public interface IQueryStatementFactory {
    /**
     *
     * @return
     * @throws QueryBuildException
     */
    QueryStatement create() throws QueryBuildException;
}
