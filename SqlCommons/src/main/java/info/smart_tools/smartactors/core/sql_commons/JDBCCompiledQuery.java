package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.SQLQueryParameterSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@link CompiledQuery} wrapping the {@link PreparedStatement}.
 */
public class JDBCCompiledQuery implements CompiledQuery {
    private final PreparedStatement preparedStatement;

    public JDBCCompiledQuery(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setParameters(List<SQLQueryParameterSetter> parameterSetters) throws SQLException, QueryBuildException {

        int index = 1;

        for (SQLQueryParameterSetter setter : parameterSetters) {
            index = setter.setParameters(this.preparedStatement, index);
        }
    }
}
