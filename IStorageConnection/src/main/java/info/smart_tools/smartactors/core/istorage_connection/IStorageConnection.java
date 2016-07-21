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
     * @throws StorageException Throw whe query can't be compiled for one of reason
     */
    ICompiledQuery compileQuery(IPreparedQuery preparedQuery) throws StorageException;

    /**
     * Check if the connection is valid.
     *
     * @return {@code true} if the connection is valid
     * @throws StorageException Throw whe query can't be compiled for one of reason
     */
    boolean validate() throws StorageException;

    /**
     * Close the connection
     *
     * @throws StorageException Throw when connection can't be closed
     */
    void close() throws StorageException;

    /**
     * Commit the current transaction.
     *
     * @throws StorageException Throw when connection have internal errors
     */
    void commit() throws StorageException;

    /**
     * Rollback the current  transaction.
     *
     * @throws StorageException Throw when rollback is damaged or changes in database can't be applied with rollback operation
     */
    void rollback() throws StorageException;
}
