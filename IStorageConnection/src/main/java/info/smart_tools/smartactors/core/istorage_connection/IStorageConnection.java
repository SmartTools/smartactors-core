package info.smart_tools.smartactors.core.istorage_connection;

import info.smart_tools.smartactors.core.istorage_connection.exception.StorageException;

/**
 * Interface for storage connection
 */
public interface IStorageConnection {
    /**
     * Compile a {@link IPreparedQuery} into the {@link ICompiledQuery}
     *
     * @param preparedQuery the query prepared for compiling
     * @return the query ready to be executed
     * @throws StorageException
     */
    ICompiledQuery compileQuery(IPreparedQuery preparedQuery) throws StorageException;

    /**
     * Check if the connection is valid.
     *
     * @return {@code true} if the connection is valid
     * @throws StorageException
     */
    boolean validate() throws StorageException;

    /**
     * Close the connection
     *
     * @throws StorageException
     */
    void close() throws StorageException;

    /**
     * Commit the current transaction.
     *
     * @throws StorageException
     */
    void commit() throws StorageException;

    /**
     * Rollback the current  transaction.
     *
     * @throws StorageException
     */
    void rollback() throws StorageException;
}
