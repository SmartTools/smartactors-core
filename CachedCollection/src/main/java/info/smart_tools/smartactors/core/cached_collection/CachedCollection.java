package info.smart_tools.smartactors.core.cached_collection;

import info.smart_tools.smartactors.core.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.core.cached_collection.exception.DeleteCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.GetCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.UpsertCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.task.DeleteFromCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.task.GetObjectFromCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.task.UpsertIntoCachedCollectionTask;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.pool_guard.IPoolGuard;
import info.smart_tools.smartactors.core.pool_guard.PoolGuard;
import info.smart_tools.smartactors.core.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;

import java.util.Collections;
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
    private IField keyValueField;
    private IField specificKeyNameField;
    private IField documentField;
    private IField idField;
    private IField isActiveField;
    private IField searchResultField;

    private IPool connectionPool;
    private CollectionName collectionName;
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
            this.collectionNameField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
            IField connectionPoolField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "connectionPool");
            this.keyNameField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "keyName");
            this.keyValueField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "keyValue");
            this.documentField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "document");
            this.idField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "id");
            this.isActiveField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "isActive");
            this.searchResultField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "searchResult");
            this.collectionName = collectionNameField.in(config);
            this.connectionPool = connectionPoolField.in(config);
            this.keyName = keyNameField.in(config);
            this.specificKeyNameField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), keyName);
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
            List<IObject> items = map.get(key);
            if (items == null || items.isEmpty()) {
                try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                    IDatabaseTask getItemTask = IOC.resolve(Keys.getOrAdd(GetObjectFromCachedCollectionTask.class.getCanonicalName()));
                    if (getItemTask == null) {
                        //NOTE:: we should have strategy for creating nested tasks for task-facade with smth like map
                        //with name of task-facade as a key
                        IDatabaseTask nestedTask = IOC.resolve(
                            Keys.getOrAdd(IDatabaseTask.class.getCanonicalName()), GetObjectFromCachedCollectionTask.class.getCanonicalName()
                        );
                        if (nestedTask == null) {
                            throw new GetCacheItemException("Can't create nested task for getItem task.");
                        }
                        getItemTask = new GetObjectFromCachedCollectionTask(nestedTask);
                        IOC.register(Keys.getOrAdd(GetObjectFromCachedCollectionTask.class.getCanonicalName()), new SingletonStrategy(getItemTask));
                    }
                    IObject getItemQuery = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
                    collectionNameField.out(getItemQuery, collectionName);
                    keyNameField.out(getItemQuery, keyName);
                    keyValueField.out(getItemQuery, key);
                    getItemTask.setConnection(IOC.resolve(Keys.getOrAdd(StorageConnection.class.getCanonicalName()), poolGuard.getObject()));
                    getItemTask.prepare(getItemQuery);
                    getItemTask.execute();
                    items = searchResultField.in(getItemQuery);
                    map.put(key, items);
                } catch (PoolGuardException e) {
                    throw new GetCacheItemException("Can't get connection from pool.", e);
                } catch (InvalidArgumentException | RegistrationException e) {
                    throw new GetCacheItemException("Can't register strategy for getItem task.", e);
                } catch (CreateCachedCollectionTaskException e) {
                    throw new GetCacheItemException("Can't create getItem task.", e);
                }
            }

            return items;
        } catch (TaskSetConnectionException e) {
            throw new GetCacheItemException("Can't set connection to read task.", e);
        } catch (TaskPrepareException e) {
            throw new GetCacheItemException("Error during preparing read task.", e);
        } catch (TaskExecutionException e) {
            throw new GetCacheItemException("Error during execution read task.", e);
        } catch (ResolutionException e) {
            throw new GetCacheItemException("Can't resolve cached object.", e);
        } catch (ChangeValueException | ReadValueException e) {
            throw new GetCacheItemException("Can't read cached object.", e);
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
                IDatabaseTask deleteTask = IOC.resolve(Keys.getOrAdd(DeleteFromCachedCollectionTask.class.getCanonicalName()));
                if (deleteTask == null) {
                    IDatabaseTask nestedTask = IOC.resolve(
                        Keys.getOrAdd(IDatabaseTask.class.getCanonicalName()), DeleteFromCachedCollectionTask.class.getCanonicalName()
                    );
                    if (nestedTask == null) {
                        throw new DeleteCacheItemException("Can't create nested task for delete task.");
                    }
                    deleteTask = new DeleteFromCachedCollectionTask(nestedTask);
                    IOC.register(Keys.getOrAdd(DeleteFromCachedCollectionTask.class.getCanonicalName()), new SingletonStrategy(deleteTask));
                }
                IObject deleteQuery = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
                collectionNameField.out(deleteQuery, collectionName);
                documentField.out(deleteQuery, message);
                StorageConnection connection = IOC.resolve(Keys.getOrAdd(StorageConnection.class.getCanonicalName()), poolGuard.getObject());
                deleteTask.setConnection(connection);
                deleteTask.prepare(deleteQuery);
                deleteTask.execute();
            } catch (PoolGuardException e) {
                throw new DeleteCacheItemException("Can't get connection from pool.", e);
            } catch (TaskSetConnectionException e) {
                throw new DeleteCacheItemException("Can't set connection to delete task.", e);
            } catch (TaskPrepareException e) {
                throw new DeleteCacheItemException("Error during preparing delete task.", e);
            } catch (TaskExecutionException e) {
                throw new DeleteCacheItemException("Error during execution delete task.", e);
            } catch (InvalidArgumentException | RegistrationException e) {
                throw new DeleteCacheItemException("Can't register strategy for delete task.", e);
            } catch (CreateCachedCollectionTaskException e) {
                throw new DeleteCacheItemException("Can't create delete task.", e);
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
        } catch (InvalidArgumentException | ReadValueException | ChangeValueException e) {
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
                IDatabaseTask upsertTask = IOC.resolve(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.getCanonicalName()));
                if (upsertTask == null) {
                    IDatabaseTask nestedTask = IOC.resolve(
                        Keys.getOrAdd(IDatabaseTask.class.getCanonicalName()), UpsertIntoCachedCollectionTask.class.getCanonicalName()
                    );
                    if (nestedTask == null) {
                        throw new UpsertCacheItemException("Can't create nested task for upsert task.");
                    }
                    upsertTask = new UpsertIntoCachedCollectionTask(nestedTask);
                    IOC.register(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.getCanonicalName()), new SingletonStrategy(upsertTask));
                }
                IObject upsertQuery = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
                Boolean isActive = isActiveField.in(message);
                isActiveField.out(message, true);
                collectionNameField.out(upsertQuery, collectionName);
                documentField.out(upsertQuery, message);

                upsertTask.setConnection(IOC.resolve(Keys.getOrAdd(StorageConnection.class.getCanonicalName()), poolGuard.getObject()));
                upsertTask.prepare(upsertQuery);
                try {
                    upsertTask.execute();
                } catch (TaskExecutionException e) {
                    isActiveField.out(message, isActive);
                    throw new UpsertCacheItemException("Error during execution upsert task.", e);
                }
            } catch (PoolGuardException e) {
                throw new UpsertCacheItemException("Can't get connection from pool.", e);
            } catch (TaskSetConnectionException e) {
                throw new UpsertCacheItemException("Can't set connection to upsert task.", e);
            } catch (TaskPrepareException e) {
                throw new UpsertCacheItemException("Error during preparing upsert task.", e);
            } catch (InvalidArgumentException | RegistrationException e) {
                throw new UpsertCacheItemException("Can't register strategy for upsert task.", e);
            }  catch (CreateCachedCollectionTaskException e) {
                throw new UpsertCacheItemException("Can't create upsert task.", e);
            }
            String key = specificKeyNameField.in(message);
            List<IObject> items = map.get(key);
            if (items != null && !items.isEmpty()) {
                for (IObject obj : items) {
                    if (idField.in(obj).equals(idField.in(message))) {
                        items.remove(obj);
                        items.add(message);
                        break;
                    }
                }
            } else {
                map.put(key, Collections.singletonList(message));
            }
        } catch (InvalidArgumentException | ReadValueException | ChangeValueException e) {
            throw new UpsertCacheItemException("Can't add or update cached object.", e);
        } catch (ResolutionException e) {
            throw new UpsertCacheItemException("Can't resolve cached object.", e);
        }
    }
}
