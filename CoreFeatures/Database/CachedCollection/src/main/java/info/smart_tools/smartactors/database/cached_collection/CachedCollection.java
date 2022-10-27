package info.smart_tools.smartactors.database.cached_collection;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.pool_guard.IPoolGuard;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.base.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.database.cached_collection.exception.ClearCachedMapException;
import info.smart_tools.smartactors.database.cached_collection.exception.DeleteCacheItemException;
import info.smart_tools.smartactors.database.cached_collection.exception.GetCacheItemException;
import info.smart_tools.smartactors.database.cached_collection.exception.UpsertCacheItemException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of cached collection {@link ICachedCollection}
 * Resolves IDatabaseTasks for each type of operations, constructs queries for task's prepare() method,
 * sets connection from pool to task and executes it.
 */
public class CachedCollection implements ICachedCollection {

    private IField collectionNameField;
    private IField keyNameField;
    private IField specificKeyNameField;
    private IField idField;
    private IField isActiveField;

    private IPool connectionPool;
    private String collectionName;
    private String keyName;
    private ConcurrentMap<String, List<IObject>> map;

    /**
     * Constructor which initializes database tasks for db operations and connection pool for them.
     * @param config configuration object with collection settings and pool. Contains
     * {
     *               "сollectionName": "name of database collection",
     *               "connectionPool": "pool with connection objects needed for database tasks",
     *               "keyName": "name of field which stores key value into document"
     * }
     * @throws InvalidArgumentException Except when actor can't be created with @config
     */
    public CachedCollection(final IObject config) throws InvalidArgumentException {
        try {
            this.map = new ConcurrentHashMap<>();
            this.collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
            IField connectionPoolField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "connectionPool");
            this.keyNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "keyName");
            this.isActiveField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "isActive");
            this.collectionName = collectionNameField.in(config);
            this.connectionPool = connectionPoolField.in(config);
            this.keyName = keyNameField.in(config);
            this.specificKeyNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), keyName);
            this.idField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), String.format("%sID", collectionName));
        } catch (ResolutionException | ReadValueException e) {
            throw new InvalidArgumentException("Can't create cached collection.", e);
        }
    }

    /**
     * Looks for value by key inside cache, if cache doesn't return any result, collection
     * resolves search task and constructs query for it. Query contains:
     * {
     *     "collectionName": "current collection name",
     *     "keyName": "name of field which stores key value into document",
     *     "keyValue": "cache key value for search"
     * }
     * Found object from DB would be saved into cache
     * @param key for cache. Cache should store needed value by this string.
     * @return list with found objects
     * @throws GetCacheItemException if any errors occurred
     */
    @Override
    public List<IObject> getItems(final String key) throws GetCacheItemException {

        try {
            final List<IObject> items = map.getOrDefault(key, new ArrayList<>());
            if (items.isEmpty()) {
                try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                    IDatabaseTask getItemTask = IOC.resolve(
                            Keys.getKeyByName("db.cached_collection.get_item"),
                            poolGuard.getObject(),
                            collectionName,
                            keyName,
                            key,
                            (IAction<IObject[]>) foundDocs -> {
                                try {
                                    items.addAll(Arrays.asList(foundDocs));
                                } catch (Exception e) {
                                    throw new ActionExecutionException(e);
                                }
                            }
                    );

                    getItemTask.execute();
                    map.put(key, items);
                } catch (PoolGuardException e) {
                    throw new GetCacheItemException("Can't get connection from pool.", e);
                }
            }

            return items;
        } catch (TaskExecutionException e) {
            throw new GetCacheItemException("Error during execution read task.", e);
        } catch (ResolutionException e) {
            throw new GetCacheItemException("Can't resolve cached object.", e);
        }
    }

    /**
     * Deletes object from cache and set active flag to false for object into DB.
     * Query object for task contains:
     * {
     *     "collectionName": "current collection name",
     *     "document": {iobject from message parameter}
     * }
     * @param message document for delete
     * @throws DeleteCacheItemException
     */
    @Override
    public void delete(final IObject message) throws DeleteCacheItemException {
        try {
            try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                IDatabaseTask deleteTask = IOC.resolve(
                    Keys.getKeyByName("db.cached_collection.delete"),
                    poolGuard.getObject(),
                    collectionName,
                    message
                );
                deleteTask.execute();
            } catch (PoolGuardException e) {
                throw new DeleteCacheItemException("Can't get connection from pool.", e);
            } catch (TaskExecutionException e) {
                throw new DeleteCacheItemException("Error during execution delete task.", e);
            }

            String key = specificKeyNameField.in(message);
            List<IObject> items = map.get(key);
            if (items != null) {
                for (IObject obj : items) {
                    if (idField.in(obj).equals(idField.in(message))) {
                        items.remove(obj);
                        break;
                    }
                }
            }
        }  catch (ResolutionException e) {
            throw new DeleteCacheItemException("Can't resolve cached object.", e);
        } catch (InvalidArgumentException | ReadValueException e) {
            throw new DeleteCacheItemException("Can't delete cached object.", e);
        }
    }

    /**
     * Adds or updates object into cache and into DB.
     * Query object for task contains:
     * {
     *     "collectionName": "current collection name",
     *     "document": {iobject from message parameter}
     * }
     * @param message document for upsert
     * @throws UpsertCacheItemException
     */
    @Override
    public void upsert(final IObject message) throws UpsertCacheItemException {

        try {
            try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                Boolean isActive = isActiveField.in(message);
                isActiveField.out(message, true);
                IDatabaseTask upsertTask = IOC.resolve(
                    Keys.getKeyByName("db.cached_collection.upsert"),
                    poolGuard.getObject(),
                    collectionName,
                    message
                );
                try {
                    upsertTask.execute();
                } catch (TaskExecutionException e) {
                    isActiveField.out(message, isActive);
                    throw new UpsertCacheItemException("Error during execution upsert task.", e);
                }
            } catch (PoolGuardException e) {
                throw new UpsertCacheItemException("Can't get connection from pool.", e);
            } catch (InvalidArgumentException e) {
                throw new UpsertCacheItemException("Error during operate with isActive field.", e);
            }
            String key = specificKeyNameField.in(message);
            List<IObject> items = map.get(key);
            if (items != null && !items.isEmpty()) {
                Boolean isInsert = true;
                for (IObject obj : items) {
                    if (idField.in(obj).equals(idField.in(message))) {
                        items.remove(obj);
                        items.add(message);
                        isInsert = false;
                        break;
                    }
                }

                if (isInsert) {
                    items.add(message);
                }
            } else {
                map.put(key, new ArrayList<>(Arrays.asList(message)));
            }
        } catch (InvalidArgumentException | ReadValueException | ChangeValueException e) {
            throw new UpsertCacheItemException("Can't add or update cached object.", e);
        } catch (ResolutionException e) {
            throw new UpsertCacheItemException("Can't resolve cached object.", e);
        }
    }

    /**
     * Deletes all objects from cache map.
     * @throws DeleteCacheItemException
     */
    @Override
    public void clearCache() throws ClearCachedMapException {
        try {
            map.clear();
        }  catch (Exception e) {
            throw new ClearCachedMapException("Can't clear cached map.", e);
        }
    }
}
