package info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * IObjectWrapper for connection parameters
 */
public interface ConnectionOptions {
    /**
     * @return url to connect with database
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

    /**
     * @param url url to connect with database
     * @throws ChangeValueException if any errors occurred
     */
    void setUrl(String url) throws ChangeValueException;

    /**
     * @param username of database user
     * @throws ChangeValueException if any errors occurred
     */
    void setUsername(String username) throws ChangeValueException;

    /**
     * @param password od database user
     * @throws ChangeValueException if any errors occurred
     */
    void setPassword(String password) throws ChangeValueException;

    /**
     * @param maxConnections maximum of connections for this pool
     * @throws ChangeValueException if any errors occurred
     */
    void setMaxConnections(Integer maxConnections) throws ChangeValueException;
}
