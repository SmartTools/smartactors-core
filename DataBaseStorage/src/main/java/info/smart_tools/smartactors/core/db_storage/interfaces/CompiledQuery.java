package info.smart_tools.smartactors.core.db_storage.interfaces;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

import java.sql.SQLException;
import java.util.List;

public interface CompiledQuery {
    void setParameters(List<SQLQueryParameterSetter> parameterSetters) throws SQLException, QueryBuildException;
}
