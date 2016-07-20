package info.smart_tools.smartactors.core.postgres_connection.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * IObjectWrapper for connection parameters
 */
public interface ConnectionOptions {
    /**
     * @return url of database
     * @throws ReadValueException if any errors occurred
     */
    String getUrl() throws ReadValueException;

    /**
     * @return username of db's user
     * @throws ReadValueException if any errors occurred
     */
    String getUsername() throws ReadValueException;

    /**
     * @return return password for user
     * @throws ReadValueException if any errors occurred
     */
    String getPassword() throws ReadValueException;

    /**
     * @return maximum of connections for this pool
     * @throws ReadValueException if any errors occurred
     */
    Integer getMaxConnections() throws ReadValueException;
}
