package info.smart_tools.smartactors.plugin.null_connection_pool;

import info.smart_tools.smartactors.database.interfaces.istorage_connection.ICompiledQuery;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IPreparedQuery;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.exception.StorageException;

/**
 * Stub for database connection which does nothing.
 */
public class NullConnection implements IStorageConnection {

    @Override
    public ICompiledQuery compileQuery(final IPreparedQuery preparedQuery) throws StorageException {
        throw new StorageException("NullConnection cannot compile query");
    }

    @Override
    public boolean validate() throws StorageException {
        return false;
    }

    @Override
    public void close() throws StorageException {
    }

    @Override
    public void commit() throws StorageException {
    }

    @Override
    public void rollback() throws StorageException {
    }

}
