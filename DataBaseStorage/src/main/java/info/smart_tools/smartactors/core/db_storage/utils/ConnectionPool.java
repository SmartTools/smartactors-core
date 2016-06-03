package info.smart_tools.smartactors.core.db_storage.utils;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageDriver;
import info.smart_tools.smartactors.core.iobject.IObject;

import java.util.HashSet;
import java.util.Set;

public class ConnectionPool {
    private final Set<StorageConnection> freeConnections = new HashSet<>();

    private final StorageDriver storageDriver;
    private final IObject connectionOptions;

    private final int maxFreeConnections;

    public ConnectionPool(StorageDriver storageDriver, IObject connectionOptions)
            throws Exception {
        this.storageDriver = storageDriver;
        this.connectionOptions = connectionOptions;
        //TODO: get from config
        this.maxFreeConnections = 1;
    }

    public StorageConnection getConnection()
            throws StorageException {
        StorageConnection result = null;
        synchronized (freeConnections) {
            for (StorageConnection connection : freeConnections) {
                freeConnections.remove(connection);

                try {
                    if (connection.validate()) {
                        result = connection;
                        break;
                    }
                } catch (StorageException ignore) {
                }
            }
        }

        if (result == null) {
            result =  storageDriver.openConnection(connectionOptions);
        }

        return result;
    }

    public void returnConnection(StorageConnection connection)
            throws StorageException {

        synchronized (freeConnections) {
            if (freeConnections.size() >= maxFreeConnections) {
                connection.close();
            } else {
                freeConnections.add(connection);
            }
        }
    }
}
