package info.smart_tools.smartactors.core.postgres_connection;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.istorage_connection.ICompiledQuery;
import info.smart_tools.smartactors.core.istorage_connection.IPreparedQuery;
import info.smart_tools.smartactors.core.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.core.istorage_connection.exception.StorageException;
import info.smart_tools.smartactors.core.postgres_connection.wrapper.ConnectionOptions;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Implementation of {@link IStorageConnection}
 */
public class PostgresConnection implements IStorageConnection {
    private static final String POSTGRESQL_JDBC_DRIVER_NAME = "org.postgresql.Driver";
    private Connection connection;
    private PreparedStatement validationQueryStatement;
    private static final Properties INIT_PROPS = new Properties();

    static {
        try (InputStream src = PostgresConnection.class.getResourceAsStream("db-init.properties")) {
            INIT_PROPS.load(src);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor by sql connection and options
     * @param options is options for connection
     * @throws StorageException Throw when object can't get connection for JDBC, or validation query down
     */
    public PostgresConnection(final ConnectionOptions options)
            throws StorageException {
        try {
            try {
                Class.forName(POSTGRESQL_JDBC_DRIVER_NAME);
            } catch (ClassNotFoundException e) {
                throw new StorageException("Could not load JDBC driver.", e);
            }

            try {
                Connection connection = DriverManager.getConnection(
                        options.getUrl(),
                        options.getUsername(),
                        options.getPassword());

                for (Object key : INIT_PROPS.keySet()) {
                    connection.createStatement().execute(INIT_PROPS.getProperty((String) key));
                }

                connection.setAutoCommit(false);
                this.connection = connection;
            } catch (SQLException | ReadValueException e) {
                throw new StorageException("Could not get JDBC connection.", e);
            }
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }

        try {
            this.validationQueryStatement = connection.prepareStatement("SELECT(1);");
        } catch (SQLException e) {
            throw new StorageException("Error creating validation query for PostgreSQL database connection: ", e);
        }
    }

    /**
     *
     * @param preparedQuery the instance of IPreparedQuery
     * @return CompiledQuery
     * @throws StorageException Throw whe query can't be compiled for one of reason
     */
    public ICompiledQuery compileQuery(final IPreparedQuery preparedQuery)
            throws StorageException {
        try {
            return new JDBCCompiledQuery(((QueryStatement) preparedQuery).compile(connection));
        } catch (SQLException e) {
            throw new StorageException(
                    String.format("Error compiling query statement \"%s\": ",
                            ((QueryStatement) preparedQuery).getBodyWriter().toString()), e);
        }
    }

    /**
     * Check if the connection is valid.
     *
     * @return {@code true} if the connection is valid
     * @throws StorageException Throw whe query can't be compiled
     */
    public boolean validate()
            throws StorageException {
        try {
            if (connection.isClosed()) {
                return false;
            }

            try {
                validationQueryStatement.executeQuery();
            } catch (SQLException e) {
                try {
                    // After invalid SELECT queries Postgresql needs a rollback of transaction.
                    // So check if it's that case.
                    rollback();
                    validationQueryStatement.executeQuery();
                } catch (SQLException ee) {
                    e.addSuppressed(ee);
                    throw e;
                }
            }

            return true;
        } catch (SQLException e) {
            throw new StorageException("Error validating connection: ", e);
        }
    }

    /**
     * Close the connection
     *
     * @throws StorageException Throw when connection can't be closed
     */
    public void close()
            throws StorageException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new StorageException("Error closing JDBC connection: ", e);
        }
    }

    /**
     * Commit the current transaction.
     *
     * @throws StorageException Throw when connection have internal errors
     */
    public void commit()
            throws StorageException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new StorageException("Error committing SQL transaction: ", e);
        }
    }

    /**
     * Rollback the current  transaction.
     *
     * @throws StorageException Throw when rollback is damaged or changes in database can't be applied with rollback operation
     */
    public void rollback()
            throws StorageException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new StorageException("Error rolling back SQL transaction: ", e);
        }
    }
}
