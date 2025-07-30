package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task.CreateIfNotExistsCollectionMessage;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task.PostgresCreateIfNotExistsTask;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin with IOC-strategies for database tasks
 */
public class CreateCollectionPlugin extends BootstrapPlugin {

     /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public CreateCollectionPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("CreatePostgresCollectionIfNotExistsPlugin")
    @After({})
    @Before("")
    public void registerCreateIfNotExistsTask() throws ResolutionException, RegistrationException, InvalidArgumentException {
        IField collectionNameField = IOC.resolve(
                Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
        IField optionsField = IOC.resolve(
                Keys.getKeyByName(IField.class.getCanonicalName()), "options");

        IOC.register(
                Keys.getKeyByName(CreateIfNotExistsCollectionMessage.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject message = (IObject) args[0];
                            return new CreateIfNotExistsCollectionMessage() {
                                @Override
                                public CollectionName getCollectionName() throws ReadValueException {
                                    try {
                                        return (CollectionName) collectionNameField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IObject getOptions() throws ReadValueException {
                                    try {
                                        return (IObject) optionsField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                            };
                        }
                )
        );
        IOC.register(
                Keys.getKeyByName("db.collection.create-if-not-exists"),
                //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                IStorageConnection connection = (IStorageConnection) args[0];
                                CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                IObject options = null;
                                if (args.length > 2) {
                                    options = (IObject) args[2];
                                }
                                IDatabaseTask task = new PostgresCreateIfNotExistsTask(connection);

                                IObject query = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));

                                collectionNameField.out(query, collectionName);
                                optionsField.out(query, options);

                                task.prepare(query);
                                return task;
                            } catch (Exception e) {
                                throw new RuntimeException("Can't resolve create if not exists db task: " + e.getMessage(), e);
                            }
                        }
                )
        );
    }
}
