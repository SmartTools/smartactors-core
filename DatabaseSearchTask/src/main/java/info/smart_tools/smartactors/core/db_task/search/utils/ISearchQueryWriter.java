package info.smart_tools.smartactors.core.db_task.search.utils;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;
import info.smart_tools.smartactors.core.db_task.search.wrappers.ISearchQuery;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Writer for a some clause of a searching query.
 */
public interface ISearchQueryWriter {
    /**
     * Writes a some clause in the searching query.
     *
     * @param queryStatement - statement of the searching query.
     * @param queryMessage - message with parameters for the searching query.
     * @param setters - list of query parameters setter.
     *                Any setter sets some parameter into query.
     *
     * @throws QueryBuildException during query writing.
     */
    void write(
            @Nonnull final QueryStatement queryStatement,
            @Nonnull final ISearchQuery queryMessage,
            @Nonnull final List<SQLQueryParameterSetter> setters) throws QueryBuildException;
}
