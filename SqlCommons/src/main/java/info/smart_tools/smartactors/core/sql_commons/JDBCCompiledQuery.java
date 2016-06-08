package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;

import java.sql.PreparedStatement;

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
}
