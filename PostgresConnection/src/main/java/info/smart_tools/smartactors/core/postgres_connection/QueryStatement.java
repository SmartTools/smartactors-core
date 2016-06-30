package info.smart_tools.smartactors.core.postgres_connection;

import info.smart_tools.smartactors.core.istorage_connection.IPreparedQuery;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 *  Stores a text of SQL statement and list of {@link SQLQueryParameterSetter}'s which should be used on
 *  {@link PreparedStatement} created using this text.
 */
public class QueryStatement implements IPreparedQuery {
    StringWriter bodyWriter;
    List<SQLQueryParameterSetter> parameterSetters;

    public QueryStatement() {
        this.bodyWriter = new StringWriter();
        this.parameterSetters = new LinkedList<>();
    }

    /**
     *  @return Writer where to write statement text.
     */
    public Writer getBodyWriter() {
        return bodyWriter;
    }

    /**
     *  Add {@link SQLQueryParameterSetter} to list of setters to be used.
     *  @param setter setter to add.
     */
    public void pushParameterSetter(final SQLQueryParameterSetter setter) {
        parameterSetters.add(setter);
    }

    /**
     *  Creates {@link PreparedStatement} ad applies all {@link SQLQueryParameterSetter}'s on it.
     *
     *  @param connection database connection to use for statement creation.
     *  @return created statement.
     *  @throws SQLException
     */
    public PreparedStatement compile(final Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(this.bodyWriter.toString());

        int index = 1;

        for (SQLQueryParameterSetter setter : this.parameterSetters) {
            index = setter.setParameters(stmt, index);
        }

        return stmt;
    }
}
