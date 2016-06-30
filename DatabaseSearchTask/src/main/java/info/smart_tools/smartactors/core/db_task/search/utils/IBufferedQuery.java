package info.smart_tools.smartactors.core.db_task.search.utils;

import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;

import java.util.List;

/**
 * A buffered search query.
 */
public interface IBufferedQuery {
    /** Compiled query with empty parameters. */
    CompiledQuery getCompiledQuery();
    void setCompiledQuery(CompiledQuery query);

    /** Parameters setters for compiled query. */
    List<SQLQueryParameterSetter> getParametersSetters();
    void setParametersSetters(List<SQLQueryParameterSetter> setters);
}
