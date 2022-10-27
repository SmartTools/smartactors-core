package info.smart_tools.smartactors.database_in_memory.in_memory_db_select_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InMemoryDBSelectTaskTest extends IOCInitializer {

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
    public void testExecute() throws InvalidArgumentException, ChangeValueException, TaskPrepareException, TaskExecutionException, IDatabaseException, ActionExecutionException {
        InMemoryDBSelectTask selectTask = new InMemoryDBSelectTask();
        IObject query = new DSObject("{\"collectionName\": \"collection_name\"}");
        IObject criteria = new DSObject("{\"hello\": \"world\"}");
        IAction<IObject[]> callback = mock(IAction.class);

        IFieldName criteriaFieldName = new FieldName("criteria");
        IFieldName callbackFieldName = new FieldName("callback");
        query.setValue(criteriaFieldName, criteria);
        query.setValue(callbackFieldName, callback);

        selectTask.prepare(query);
        selectTask.execute();

        verify(inMemoryDatabase).select(criteria, "collection_name");
        verify(callback).execute(any(IObject[].class));
    }
}
