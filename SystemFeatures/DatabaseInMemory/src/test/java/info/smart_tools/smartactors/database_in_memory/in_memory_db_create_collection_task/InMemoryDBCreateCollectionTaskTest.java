package info.smart_tools.smartactors.database_in_memory.in_memory_db_create_collection_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InMemoryDBCreateCollectionTaskTest extends IOCInitializer {
    InMemoryDatabase inMemoryDatabase;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        inMemoryDatabase = mock(InMemoryDatabase.class);
        IOC.register(Keys.getKeyByName(InMemoryDatabase.class.getCanonicalName()), new SingletonStrategy(
                        inMemoryDatabase
                )
        );
    }

    @Test
    public void testPreparing() throws InvalidArgumentException, TaskPrepareException {
        InMemoryDBCreateCollectionTask createCollectionTask = new InMemoryDBCreateCollectionTask();
        IObject query = new DSObject("{\"collectionName\": \"collection_name\"}");
        createCollectionTask.prepare(query);
    }

    @Test
    public void testExecuting() throws InvalidArgumentException, TaskPrepareException, TaskExecutionException {
        InMemoryDBCreateCollectionTask createCollectionTask = new InMemoryDBCreateCollectionTask();
        IObject query = new DSObject("{\"collectionName\": \"collection_name\"}");
        createCollectionTask.prepare(query);
        createCollectionTask.execute();
        verify(inMemoryDatabase).createCollection("collection_name");
    }
}
