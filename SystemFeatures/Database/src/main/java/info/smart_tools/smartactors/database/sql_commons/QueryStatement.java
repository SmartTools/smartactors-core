package info.smart_tools.smartactors.database.sql_commons;

import info.smart_tools.smartactors.database.database_storage.interfaces.PreparedQuery;
import info.smart_tools.smartactors.database.database_storage.interfaces.SQLQueryParameterSetter;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *  Stores a text of SQL statement and list of {@link SQLQueryParameterSetter}'s which should be used on
 *  {@link PreparedStatement} created using this text.
 */
public class QueryStatement implements PreparedQuery {
    StringWriter bodyWriter;

    public QueryStatement() {
        this.bodyWriter = new StringWriter();
    }

    /**
     *  @return Writer where to write statement text.
     */
    public Writer getBodyWriter() {
        return bodyWriter;
    }

    /**
     *  Creates {@link PreparedStatement} ad applies all {@link SQLQueryParameterSetter}'s on it.
     *
     *  @param connection database connection to use for statement creation.
     *  @return created statement.
     *  @throws SQLException if any errors occurred
     */
    public PreparedStatement compile(final Connection connection) throws SQLException {
        return connection.prepareStatement(this.bodyWriter.toString());
    }
}
