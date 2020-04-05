package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor;

import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.wrapper.CreateCollectionWrapper;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import org.junit.Test;
import org.mockito.internal.verification.VerificationModeFactory;

import static org.mockito.Mockito.*;

public class CreateCollectionActorTest extends IOCInitializer {
    private CreateCollectionActor actor = new CreateCollectionActor();

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        IOC.register(Keys.getKeyByName("connectionOptions"),
                new ApplyFunctionToArgumentsStrategy(args -> mock(ConnectionOptions.class))
        );
        IOC.register(Keys.getKeyByName("PostgresConnectionPool"),
                new ApplyFunctionToArgumentsStrategy(args -> mock(IPool.class))
        );
    }

    @Test
    public void Should_CreateCollection() throws Exception {
        CreateCollectionWrapper wrapper = mock(CreateCollectionWrapper.class);
        when(wrapper.getCollectionName()).thenReturn("test");
        when(wrapper.getConnectionOptionsRegistrationName()).thenReturn("connectionOptions");
        when(wrapper.getOptions()).thenReturn(null);

        ITask task = mock(ITask.class);
        IOC.register(Keys.getKeyByName("db.collection.create-if-not-exists"),
                new ApplyFunctionToArgumentsStrategy(args -> task)
        );
        actor.createTable(wrapper);

        verify(task, VerificationModeFactory.times(1)).execute();
    }
}
