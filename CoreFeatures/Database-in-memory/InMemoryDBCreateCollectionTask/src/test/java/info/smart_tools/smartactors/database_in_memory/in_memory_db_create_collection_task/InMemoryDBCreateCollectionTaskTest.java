package info.smart_tools.smartactors.database_in_memory.in_memory_db_create_collection_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InMemoryDBCreateCollectionTaskTest {
    InMemoryDatabase inMemoryDatabase;

    @Before
    public void setUp() throws ScopeProviderException, ResolutionException, RegistrationException, InvalidArgumentException {
        inMemoryDatabase = mock(InMemoryDatabase.class);
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );

        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);
        IOC.register(
                IOC.getKeyForKeyByNameStrategy(),
                new ResolveByNameIocStrategy()
        );
        IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                )
        );

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
