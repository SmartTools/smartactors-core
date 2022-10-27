package info.smart_tools.smartactors.database_in_memory.in_memory_get_by_id_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.interfaces.idatabase.exception.IDatabaseException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.database_in_memory.in_memory_db_get_by_id_task.InMemoryGetByIdTask;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class InMemoryGetByIdTaskTest extends IOCInitializer {

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
    public void testPrepare() throws InvalidArgumentException, ChangeValueException, TaskPrepareException {
        InMemoryGetByIdTask getByIdTask = new InMemoryGetByIdTask();
        IObject query = new DSObject("{\"collectionName\": \"collection_name\", \"id\": 3}");
        IAction<IObject> iObjectIAction = mock(IAction.class);
        query.setValue(new FieldName("callback"), iObjectIAction);
        getByIdTask.prepare(query);
    }

    @Test
    public void testExecution() throws InvalidArgumentException, ChangeValueException, TaskPrepareException, IDatabaseException, ActionExecutionException, TaskExecutionException {
        InMemoryGetByIdTask getByIdTask = new InMemoryGetByIdTask();
        IObject query = new DSObject("{\"collectionName\": \"collection_name\", \"id\": 3}");
        IAction<IObject> iObjectIAction = mock(IAction.class);
        query.setValue(new FieldName("callback"), iObjectIAction);
        getByIdTask.prepare(query);
        IObject document = new DSObject("{}");
        when(inMemoryDatabase.getById(3, "collection_name")).thenReturn(document);
        getByIdTask.execute();
        verify(iObjectIAction).execute(document);
    }
}
