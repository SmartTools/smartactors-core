package info.smart_tools.smartactors.core.db_storage;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ConnectionPool;

public class DataBaseStorage {

    public interface QueryExecution {
        void execute(StorageConnection connection) throws StorageException;
    }

    public static void executeQuery(ConnectionPool connectionPool, QueryExecution execution)
            throws Exception {
        StorageConnection connection = connectionPool.getConnection();

        try {
            execution.execute(connection);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    public static void executeTransaction(ConnectionPool connectionPool, QueryExecution execution)
            throws Exception {
        StorageConnection connection = connectionPool.getConnection();
        try {
            execution.execute(connection);
            connection.commit();
        } catch(Exception e) {
            try {
                connection.rollback();
            } catch (StorageException ee) {
                e.addSuppressed(ee);
            }

            throw e;
        } finally {
            connectionPool.returnConnection(connection);
        }
    }
}
