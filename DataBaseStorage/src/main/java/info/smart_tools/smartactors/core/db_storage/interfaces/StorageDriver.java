package info.smart_tools.smartactors.core.db_storage.interfaces;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.iobject.IObject;

public interface StorageDriver {

    /**
     * Opens a connection to database.
     *
     * @param options parameters of the connection such as database ULI/login/password or any other specific for the
     *                driver implementation
     * @return open and ready to be used DB connection
     */
    StorageConnection openConnection(IObject options) throws StorageException;

    default void performOperation(StorageExecutor executor, CompiledQuery query, IObject message)
        throws StorageException {
        executor.executeQuery(query, message);
    }
}
