package info.smart_tools.smartactors.core.postgres_connection.wrapper;

/**
 * IObjectWrapper for connection parameters
 */
public interface ConnectionOptions {
    /**
     * @return url of database
     */
    String getUrl();

    /**
     * @return username of db's user
     */
    String getUsername();

    /**
     * @return return password for user
     */
    String getPassword();

    /**
     * @return maximum of connections for this pool
     */
    Integer getMaxConnections();
}
