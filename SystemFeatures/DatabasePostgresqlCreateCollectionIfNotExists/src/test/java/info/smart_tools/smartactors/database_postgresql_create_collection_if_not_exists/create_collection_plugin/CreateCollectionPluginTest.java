package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task.CreateIfNotExistsCollectionMessage;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_task.PostgresCreateIfNotExistsTask;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.field.field.Field;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CreateCollectionPluginTest extends IOCInitializer {

    private IObject message;
    private IStorageConnection connection;
    private CollectionName collection;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        IKey fieldKey = Keys.getKeyByName(IField.class.getCanonicalName());
        IOC.register(
                fieldKey,
                new ResolveByNameIocStrategy(
                        (args) -> {
                            String fieldName = String.valueOf(args[0]);
                            try {
                                return new Field(
                                        IOC.resolve(
                                                Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                                fieldName
                                        )
                                );
                            } catch (InvalidArgumentException | ResolutionException e) {
                                throw new RuntimeException("Can't resolve IField: ", e);
                            }
                        }
                )
        );
        Bootstrap bootstrap = new Bootstrap();
        new CreateCollectionPlugin(bootstrap).load();
        bootstrap.start();

        message = mock(IObject.class);
        connection = mock(IStorageConnection.class);
        collection = CollectionName.fromString("test");
    }

    @Test
    public void testCreateTaskInitialized() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getKeyByName(CreateIfNotExistsCollectionMessage.class.getCanonicalName()), message)
                instanceof CreateIfNotExistsCollectionMessage);
        IObject options = mock(IObject.class);
        assertTrue(IOC.resolve(Keys.getKeyByName("db.collection.create-if-not-exists"), connection, collection, options)
                instanceof PostgresCreateIfNotExistsTask);
    }

    @Test
    public void testCreateTaskInitializedWithoutOptions() throws ResolutionException {
        assertTrue(IOC.resolve(Keys.getKeyByName("db.collection.create-if-not-exists"), connection, collection)
                instanceof PostgresCreateIfNotExistsTask);
    }
}
