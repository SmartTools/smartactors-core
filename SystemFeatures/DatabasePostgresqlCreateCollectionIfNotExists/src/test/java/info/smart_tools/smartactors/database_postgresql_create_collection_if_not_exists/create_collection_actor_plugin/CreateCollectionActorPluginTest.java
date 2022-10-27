package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.CreateCollectionActor;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.exception.CreateCollectionActorException;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_plugin.CreateCollectionPlugin;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.field.field.Field;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CreateCollectionActorPluginTest extends IOCInitializer {

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
        new TestConnectionOptionsPlugin(bootstrap).load();
        new TestPostgresConnectionPoolPlugin(bootstrap).load();
        new CreateCollectionPlugin(bootstrap).load();
        new CreateCollectionActorPlugin(bootstrap).load();
        bootstrap.start();
    }

    @Test
    public void testCreateTaskInitializedWithoutOptions() throws ResolutionException, CreateCollectionActorException {
        Object actor = IOC.resolve(Keys.getKeyByName("CreateCollectionIfNotExistsActor"));
        assertTrue(actor instanceof CreateCollectionActor);
    }
}
