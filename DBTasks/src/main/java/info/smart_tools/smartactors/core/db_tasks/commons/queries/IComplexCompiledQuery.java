package info.smart_tools.smartactors.core.db_tasks.commons.queries;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.sql_commons.DeclaredParam;
import info.smart_tools.smartactors.core.sql_commons.IDeclaredParam;

import java.util.List;

/**
 * Impl. of compiled query for complex query with variable parameters.
 */
public interface IComplexCompiledQuery extends ICompiledQuery {
    /**
     * @return the declared parameters for query.
     * @see DeclaredParam
     */
    List<IDeclaredParam> getDeclaredParams();
}
