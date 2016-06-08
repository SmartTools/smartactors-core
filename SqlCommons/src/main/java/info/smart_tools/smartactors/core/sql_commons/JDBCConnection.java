package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.PreparedQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementation of {@link StorageConnection} wrapping the {@link Connection}.
 */
public class JDBCConnection implements StorageConnection {
    protected Connection connection;
    protected JDBCConnectionOptions options;

    public JDBCConnection(Connection connection, JDBCConnectionOptions options) {
        this.connection = connection;
        this.options = options;
    }

    @Override
    public CompiledQuery compileQuery(PreparedQuery preparedQuery) throws StorageException {
        try {
            return new JDBCCompiledQuery(((QueryStatement) preparedQuery).compile(connection));
        } catch (SQLException e) {
            throw new StorageException(
                    String.format("Error compiling query statement \"%s\": ",
                            ((QueryStatement)preparedQuery).bodyWriter.toString()),
                    e);
        }
    }

    @Override
    public boolean validate() throws StorageException {
        try {
            return !connection.isClosed() && this.connection.isValid(Optional.ofNullable(options.getValidationTimeout()).orElse(0));
        } catch (SQLException e) {
            throw new StorageException("Error validating connection: ", e);
        }
    }

    @Override
    public void close()
            throws StorageException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new StorageException("Error closing JDBC connection: ", e);
        }
    }

    @Override
    public void commit()
            throws StorageException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new StorageException("Error committing SQL transaction: ", e);
        }
    }

    @Override
    public void rollback()
            throws StorageException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new StorageException("Error rolling back SQL transaction: ", e);
        }
    }
}
