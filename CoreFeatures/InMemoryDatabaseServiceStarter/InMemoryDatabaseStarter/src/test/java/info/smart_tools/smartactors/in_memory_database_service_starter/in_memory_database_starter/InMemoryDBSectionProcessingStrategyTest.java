package info.smart_tools.smartactors.in_memory_database_service_starter.in_memory_database_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.database.interfaces.idatabase.exception.IDatabaseException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;


public class InMemoryDBSectionProcessingStrategyTest {
    private InMemoryDatabase inMemoryDatabase;
    private IObject mockObject;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        inMemoryDatabase = mock(InMemoryDatabase.class);
        mockObject = mock(IObject.class);
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
        IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                            }
                            return null;
                        }
                )
        );

        IOC.register(Keys.getKeyByName("IObjectByString"), new SingletonStrategy(mockObject));
        IOC.register(Keys.getKeyByName(InMemoryDatabase.class.getCanonicalName()), new SingletonStrategy(inMemoryDatabase));

    }

    @Test
    public void testLoadingConfig() throws InvalidArgumentException, ResolutionException, ConfigurationProcessingException, ChangeValueException, IDatabaseException {
        List<String> iObjects = new LinkedList<>();
        iObjects.add("{\"foo\": \"bar\"}");
        iObjects.add("{\"foo1\": \"bar1\"}");
        IObject inMemoryDatabaseConfig = new DSObject("{\"name\":\"my_collection_name\"}");
        List<IObject> inMemoryDb = new ArrayList<>();
        DSObject config = new DSObject();
        inMemoryDatabaseConfig.setValue(new FieldName("documents"), iObjects);
        inMemoryDb.add(inMemoryDatabaseConfig);
        InMemoryDBSectionProcessingStrategy sectionProcessingStrategy = new InMemoryDBSectionProcessingStrategy();
        config.setValue(new FieldName("inMemoryDb"), inMemoryDb);
        sectionProcessingStrategy.onLoadConfig(config);
        verify(inMemoryDatabase).createCollection("my_collection_name");
        verify(inMemoryDatabase, times(2)).insert(mockObject, "my_collection_name");
        sectionProcessingStrategy.onRevertConfig(config);
        sectionProcessingStrategy.getSectionName();
    }
}

