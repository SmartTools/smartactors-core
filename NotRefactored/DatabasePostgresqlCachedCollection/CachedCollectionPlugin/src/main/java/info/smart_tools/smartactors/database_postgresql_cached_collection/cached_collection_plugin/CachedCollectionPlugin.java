package info.smart_tools.smartactors.database_postgresql_cached_collection.cached_collection_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.database.cached_collection.CachedCollection;
import info.smart_tools.smartactors.database.cached_collection.ICachedCollection;
import info.smart_tools.smartactors.database.cached_collection.task.DeleteFromCachedCollectionTask;
import info.smart_tools.smartactors.database.cached_collection.task.GetItemFromCachedCollectionTask;
import info.smart_tools.smartactors.database.cached_collection.task.UpsertIntoCachedCollectionTask;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_by_composite_name_ioc_with_lambda_strategy.ResolveByCompositeNameIOCStrategy;

public class CachedCollectionPlugin extends BootstrapPlugin {

     /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public CachedCollectionPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("PostgresCachedCollectionPlugin")
    @After({})
    @Before("")
    public void registerPostgresCachedCollection() throws RegistrationException {
        registerCachedCollection();
        registerTasks();
    }

    private void registerCachedCollection() throws RegistrationException {
        try {
            IKey cachedCollectionKey = Keys.getKeyByName(ICachedCollection.class.getCanonicalName());
            IField connectionPoolField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "connectionPool");
            IField collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
            IField keyNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "keyName");
            IOC.register(cachedCollectionKey, new ResolveByCompositeNameIOCStrategy(
                    (args) -> {
                        try {
                            ConnectionOptions connectionOptions = (ConnectionOptions) args[0];
                            IPool connectionPool = IOC.resolve(Keys.getKeyByName("PostgresConnectionPool"), connectionOptions);
                            IObject config = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));
                            connectionPoolField.out(config, connectionPool);
                            collectionNameField.out(config, args[1]);
                            keyNameField.out(config, args[2]);

                            return new CachedCollection(config);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }));
        } catch (RegistrationException | InvalidArgumentException | ResolutionException e) {
            throw new RegistrationException("CreateCachedCollection plugin can't load", e);
        }
    }

    private void registerTasks() throws RegistrationException {
        try {
            IField collectionNameField = IOC.resolve(
                    Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
            IField documentField = IOC.resolve(
                    Keys.getKeyByName(IField.class.getCanonicalName()), "document");
            IField callbackField = IOC.resolve(
                    Keys.getKeyByName(IField.class.getCanonicalName()), "callback");
            IField keyNameField = IOC.resolve(
                    Keys.getKeyByName(IField.class.getCanonicalName()), "keyName");
            IField keyField = IOC.resolve(
                    Keys.getKeyByName(IField.class.getCanonicalName()), "key");


            IOC.register(
                    Keys.getKeyByName("db.cached_collection.upsert"),
                    //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                    new ApplyFunctionToArgumentsStrategy(
                            (args) -> {
                                try {
                                    IStorageConnection connection = (IStorageConnection) args[0];
                                    CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                    IObject document = (IObject) args[2];
                                    IDatabaseTask task = new UpsertIntoCachedCollectionTask(connection);

                                    IObject query = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));

                                    collectionNameField.out(query, collectionName);
                                    documentField.out(query, document);

                                    task.prepare(query);
                                    return task;
                                } catch (Exception e) {
                                    throw new RuntimeException("Can't resolve upsert db task.", e);
                                }
                            }
                    )
            );

            IOC.register(
                    Keys.getKeyByName("db.cached_collection.delete"),
                    //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                    new ApplyFunctionToArgumentsStrategy(
                            (args) -> {
                                try {
                                    IStorageConnection connection = (IStorageConnection) args[0];
                                    CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                    IObject document = (IObject) args[2];
                                    IDatabaseTask task = new DeleteFromCachedCollectionTask(connection);

                                    IObject query = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));

                                    collectionNameField.out(query, collectionName);
                                    documentField.out(query, document);

                                    task.prepare(query);
                                    return task;
                                } catch (Exception e) {
                                    throw new RuntimeException("Can't resolve upsert db task.", e);
                                }
                            }
                    )
            );

            IOC.register(
                    Keys.getKeyByName("db.cached_collection.get_item"),
                    //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                    new ApplyFunctionToArgumentsStrategy(
                            (args) -> {
                                try {
                                    IStorageConnection connection = (IStorageConnection) args[0];
                                    CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                    IDatabaseTask task = new GetItemFromCachedCollectionTask(connection);

                                    IObject query = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));
                                    collectionNameField.out(query, collectionName);
                                    keyNameField.out(query, args[2]);
                                    keyField.out(query, args[3]);
                                    callbackField.out(query, args[4]);

                                    task.prepare(query);
                                    return task;
                                } catch (Exception e) {
                                    throw new RuntimeException("Can't resolve upsert db task.", e);
                                }
                            }
                    )
            );
        } catch (RegistrationException | InvalidArgumentException | ResolutionException e) {
            throw new RegistrationException(e);
        }
    }
}
