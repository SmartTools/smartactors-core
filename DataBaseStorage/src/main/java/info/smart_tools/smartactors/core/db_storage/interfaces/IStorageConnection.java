package info.smart_tools.smartactors.core.db_storage.interfaces;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IPreparedQuery;

public interface IStorageConnection {
    /**
     * Compile a {@link IPreparedQuery} created by the {@link QueryBuilder} into the {@link ICompiledQuery} that is ready
     * to be executed by a {@link QueryExecutor}.
     *
     * @param preparedQuery the query prepared by {@link QueryBuilder}
     * @return the query ready to be executed by {@link QueryExecutor}
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

    /**
     * Returns connection identification
     * @return id
     */
    String getId();

}
