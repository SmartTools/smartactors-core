package info.smart_tools.smartactors.core.db_storage.interfaces;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.exceptions.QueryExecutionException;

import java.sql.ResultSet;

public interface ICompiledQuery {
    boolean execute() throws QueryExecutionException;
    ResultSet executeQuery() throws QueryExecutionException;
    int executeUpdate() throws QueryExecutionException;

    void setParameters(ISQLQueryParameterSetter parameterSetter) throws QueryBuildException;
}
