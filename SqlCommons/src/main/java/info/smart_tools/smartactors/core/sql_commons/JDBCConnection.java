package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IPreparedQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link IStorageConnection} wrapping the {@link Connection}.
 */
public class JDBCConnection implements IStorageConnection {
    protected Connection connection;
    protected JDBCConnectionOptions options;

    private String id;

    public JDBCConnection(final Connection connection, final JDBCConnectionOptions options) {
        this.connection = connection;
        this.options = options;
        this.id = String.valueOf(UUID.randomUUID());
    }

    @Override
    public ICompiledQuery compileQuery(final IPreparedQuery preparedQuery) throws StorageException {
        try {
            return new JDBCCompiledQuery(((QueryStatement) preparedQuery).compile(connection));
        } catch (SQLException e) {
            throw new StorageException(
                    String.format("Error compiling query statement \"%s\": ",
                            ((QueryStatement)preparedQuery).bodyWriter.toString()),
                    e);
        } catch (NullPointerException e) {
            throw new StorageException("Incoming prepared query is null!", e);
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

    @Override
    public String getId() {
        return id;
    }
}
