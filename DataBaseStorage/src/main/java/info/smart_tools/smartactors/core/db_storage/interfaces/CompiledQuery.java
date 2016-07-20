package info.smart_tools.smartactors.core.db_storage.interfaces;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

import java.sql.SQLException;
import java.util.List;

/**
 * Query for future using
 */
public interface CompiledQuery {
    /**
     * @param parameterSetters Parameters for Query
     * @throws SQLException Throw when some of statement throw exception
     * @throws QueryBuildException Throw when query can't be compiled
     */
    void setParameters(List<SQLQueryParameterSetter> parameterSetters) throws SQLException, QueryBuildException;
}
