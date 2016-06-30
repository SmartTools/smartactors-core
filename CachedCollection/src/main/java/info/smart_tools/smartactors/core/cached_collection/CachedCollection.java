package info.smart_tools.smartactors.core.cached_collection;

import info.smart_tools.smartactors.core.cached_collection.exception.DeleteCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.GetCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.UpsertCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.task.DeleteFromCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.task.GetObjectFromCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.task.UpsertIntoCachedCollectionTask;
import info.smart_tools.smartactors.core.cached_collection.wrapper.CachedCollectionConfig;
import info.smart_tools.smartactors.core.cached_collection.wrapper.GetObjectFromCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.delete.DeleteFromCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.delete.DeleteItem;
import info.smart_tools.smartactors.core.cached_collection.wrapper.upsert.UpsertIntoCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.upsert.UpsertItem;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
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
import java.util.stream.Collectors;

/**
 * Implementation of cached collection
 * {@link ICachedCollection}
 */
public class CachedCollection implements ICachedCollection {

    private IPool connectionPool;
    private CollectionName collectionName;
    private ConcurrentMap<String, List<IObject>> map;

    /**
     * Constructor which initializes database tasks for db operations and connection pool for them.
     * @param config wrapper for configuration object with tasks and pool.
     * @throws InvalidArgumentException Except when actor can't be created with @config
     */
    public CachedCollection(final CachedCollectionConfig config) throws InvalidArgumentException {
        try {
            this.collectionName = config.getCollectionName();
            this.connectionPool = config.getConnectionPool();
            this.map = new ConcurrentHashMap<>();
        } catch (ReadValueException e) {
            throw new InvalidArgumentException("Can't create cached collection.", e);
        }
    }

    @Override
    public List<IObject> getItems(final String key) throws GetCacheItemException {

        try {
            List<IObject> items = map.get(key);
            if (items == null || items.isEmpty()) {
                try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                    IDatabaseTask getItemTask = IOC.resolve(Keys.getOrAdd(GetObjectFromCachedCollectionTask.class.toString()));
                    if (getItemTask == null) {
                        //NOTE:: we should have strategy for creating nested tasks for task-facade with smth like map
                        //with name of task-facade as a key
                        IDatabaseTask nestedTask = IOC.resolve(
                            Keys.getOrAdd(IDatabaseTask.class.toString()), GetObjectFromCachedCollectionTask.class.toString()
                        );
                        if (nestedTask == null) {
                            throw new GetCacheItemException("Can't create nested task for getItem task.");
                        }
                        getItemTask = new GetObjectFromCachedCollectionTask(nestedTask);
                        IOC.register(Keys.getOrAdd(GetObjectFromCachedCollectionTask.class.toString()), new SingletonStrategy(getItemTask));
                    }
                    GetObjectFromCachedCollectionQuery getItemQuery = IOC.resolve(
                        Keys.getOrAdd(GetObjectFromCachedCollectionQuery.class.toString())
                    );
                    getItemQuery.setCollectionName(collectionName);
                    getItemQuery.setKey(key);
                    getItemTask.setConnection(IOC.resolve(Keys.getOrAdd(StorageConnection.class.toString()), poolGuard.getObject()));
                    getItemTask.prepare(getItemQuery.wrapped());
                    getItemTask.execute();
                    items = getItemQuery.getSearchResult().collect(Collectors.toList());
                    map.put(key, items);
                } catch (PoolGuardException e) {
                    throw new GetCacheItemException("Can't get connection from pool.", e);
                } catch (InvalidArgumentException | RegistrationException e) {
                    throw new GetCacheItemException("Can't register strategy for getItem task.", e);
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
        } catch (ReadValueException | ChangeValueException e) {
            throw new GetCacheItemException("Can't read cached object.", e);
        }
    }

    @Override
    public void delete(final IObject message) throws DeleteCacheItemException {
        try {
            DeleteItem deleteItem;
            try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                IDatabaseTask deleteTask = IOC.resolve(Keys.getOrAdd(DeleteFromCachedCollectionTask.class.toString()));
                if (deleteTask == null) {
                    IDatabaseTask nestedTask = IOC.resolve(
                        Keys.getOrAdd(IDatabaseTask.class.toString()), DeleteFromCachedCollectionTask.class.toString()
                    );
                    if (nestedTask == null) {
                        throw new DeleteCacheItemException("Can't create nested task for delete task.");
                    }
                    deleteTask = new DeleteFromCachedCollectionTask(nestedTask);
                    IOC.register(Keys.getOrAdd(DeleteFromCachedCollectionTask.class.toString()), new SingletonStrategy(deleteTask));
                }
                deleteItem = IOC.resolve(Keys.getOrAdd(DeleteItem.class.toString()), message);
                DeleteFromCachedCollectionQuery deleteQuery = IOC.resolve(Keys.getOrAdd(DeleteFromCachedCollectionQuery.class.toString()));
                deleteQuery.setCollectionName(collectionName);
                deleteQuery.setDeleteItem(deleteItem);
                StorageConnection connection = IOC.resolve(Keys.getOrAdd(StorageConnection.class.toString()), poolGuard.getObject());
                deleteTask.setConnection(connection);
                deleteTask.prepare(deleteQuery.wrapped());
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
            }

            String key = deleteItem.getKey();
            List<IObject> items = map.get(key);
            if (items != null) {
                DeleteItem item;
                for (IObject obj : items) {
                    item = IOC.resolve(Keys.getOrAdd(DeleteItem.class.toString()), obj);
                    if (item.getId().equals(deleteItem.getId())) {
                        items.remove(obj);
                        break;
                    }
                }
            }
        }  catch (ResolutionException e) {
            throw new DeleteCacheItemException("Can't resolve cached object.", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new DeleteCacheItemException("Can't delete cached object.", e);
        }
    }

    @Override
    public void upsert(final IObject message) throws UpsertCacheItemException {

        try {
            UpsertItem upsertItem;
            try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                IDatabaseTask upsertTask = IOC.resolve(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.toString()));
                if (upsertTask == null) {
                    IDatabaseTask nestedTask = IOC.resolve(
                        Keys.getOrAdd(IDatabaseTask.class.toString()), UpsertIntoCachedCollectionTask.class.toString()
                    );
                    if (nestedTask == null) {
                        throw new UpsertCacheItemException("Can't create nested task for upsert task.");
                    }
                    upsertTask = new UpsertIntoCachedCollectionTask(nestedTask);
                    IOC.register(Keys.getOrAdd(UpsertIntoCachedCollectionTask.class.toString()), new SingletonStrategy(upsertTask));
                }
                upsertItem = IOC.resolve(Keys.getOrAdd(UpsertItem.class.toString()), message);
                Boolean isActive = upsertItem.isActive();
                upsertItem.setIsActive(true);
                UpsertIntoCachedCollectionQuery upsertQuery = IOC.resolve(Keys.getOrAdd(UpsertIntoCachedCollectionQuery.class.toString()));
                upsertQuery.setCollectionName(collectionName);
                upsertQuery.setUpsertItem(upsertItem);

                upsertTask.setConnection(IOC.resolve(Keys.getOrAdd(StorageConnection.class.toString()), poolGuard.getObject()));
                upsertTask.prepare(upsertQuery.wrapped());
                try {
                    upsertTask.execute();
                } catch (TaskExecutionException e) {
                    upsertItem.setIsActive(isActive);
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
            }
            String key = upsertItem.getKey();
            List<IObject> items = map.get(key);
            if (items != null && !items.isEmpty()) {
                UpsertItem item;
                for (IObject obj : items) {
                    item = IOC.resolve(Keys.getOrAdd(UpsertItem.class.toString()), obj);
                    if (item.getId().equals(upsertItem.getId())) {
                        items.remove(obj);
                        items.add(upsertItem.wrapped());
                        break;
                    }
                }
            } else {
                map.put(key, Collections.singletonList(upsertItem.wrapped()));
            }
        } catch (ReadValueException | ChangeValueException e) {
            throw new UpsertCacheItemException("Can't add or update cached object.", e);
        } catch (ResolutionException e) {
            throw new UpsertCacheItemException("Can't resolve cached object.", e);
        }
    }
}
