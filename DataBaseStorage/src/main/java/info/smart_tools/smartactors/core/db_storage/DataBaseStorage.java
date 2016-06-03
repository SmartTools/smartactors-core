package info.smart_tools.smartactors.core.db_storage;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageDriver;
import info.smart_tools.smartactors.core.db_storage.interfaces.wrapper.StorageParams;
import info.smart_tools.smartactors.core.db_storage.utils.ConnectionPool;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;

public class DataBaseStorage {
    private ConnectionPool connectionPool;
    private StorageDriver storageDriver;

    public DataBaseStorage(StorageParams params) {
        try {
            String driverName = params.getDriver() != null ? params.getDriver() : "async_postgres";

            //TODO: how to do extractWrapped?
            storageDriver = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), StorageDriver.class.toString()), params);
            connectionPool = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), ConnectionPool.class.toString()), params);
        } catch (ResolutionException | ReadValueException | ChangeValueException e) {
            //TODO: what kind of exception should be thrown?
        }
    }

    protected interface QueryExecution {
        void execute(StorageConnection connection) throws StorageException;
    }

    protected void executeQuery(QueryExecution execution)
            throws Exception {
        StorageConnection connection = connectionPool.getConnection();

        try {
            execution.execute(connection);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    protected void executeTransaction(QueryExecution execution)
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
