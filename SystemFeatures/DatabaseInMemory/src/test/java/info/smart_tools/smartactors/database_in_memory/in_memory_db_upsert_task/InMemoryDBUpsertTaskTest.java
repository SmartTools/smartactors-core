package info.smart_tools.smartactors.database_in_memory.in_memory_db_upsert_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.interfaces.idatabase.exception.IDatabaseException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InMemoryDBUpsertTaskTest extends IOCInitializer {

    private InMemoryDatabase inMemoryDatabase;

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
    public void testPrepare() throws InvalidArgumentException, TaskPrepareException, TaskExecutionException, ChangeValueException {
        InMemoryDBUpsertTask upsertTask = new InMemoryDBUpsertTask();
        IObject query = new DSObject("{\"collectionName\": \"collection_name\"}");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        IFieldName documentFieldName = new FieldName("document");
        query.setValue(documentFieldName, document);
        upsertTask.prepare(query);
    }

    @Test
    public void testExecute() throws InvalidArgumentException, ChangeValueException, TaskPrepareException, TaskExecutionException, IDatabaseException {
        InMemoryDBUpsertTask upsertTask = new InMemoryDBUpsertTask();
        IObject query = new DSObject("{\"collectionName\": \"collection_name\"}");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        IFieldName documentFieldName = new FieldName("document");
        query.setValue(documentFieldName, document);
        upsertTask.prepare(query);
        upsertTask.execute();
        verify(inMemoryDatabase).upsert(document, "collection_name");
    }
}
