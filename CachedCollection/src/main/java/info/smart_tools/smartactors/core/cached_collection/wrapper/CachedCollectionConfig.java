package info.smart_tools.smartactors.core.cached_collection.wrapper;

import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ipool.IPool;

/**
 * Wrapper for configuration iobject for cached collection
 */
public interface CachedCollectionConfig {

    /**
     * @return connection pool
     * @throws ReadValueException Calling when try read value of variable
     */
    IPool getConnectionPool() throws ReadValueException;

    /**
     * @return collection name
     * @throws ReadValueException Calling when try read value of variable
     */
    CollectionName getCollectionName() throws ReadValueException;

    //TODO:: Should be in another wrapper?

    /**
     * @param connectionPool Some realisation of IPool
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setConnectionPool(IPool connectionPool) throws ChangeValueException;

    /**
     * @param collectionName Name of collection
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setCollectionName(CollectionName collectionName) throws ChangeValueException;
}
