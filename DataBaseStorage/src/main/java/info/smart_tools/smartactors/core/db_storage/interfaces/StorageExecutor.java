package info.smart_tools.smartactors.core.db_storage.interfaces;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryExecutionException;
import info.smart_tools.smartactors.core.iobject.IObject;

public interface StorageExecutor {
    void executeQuery(CompiledQuery compiledQuery, IObject message)
            throws QueryExecutionException;
}
