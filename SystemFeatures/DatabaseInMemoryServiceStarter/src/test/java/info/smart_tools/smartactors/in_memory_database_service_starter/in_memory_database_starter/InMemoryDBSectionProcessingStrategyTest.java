package info.smart_tools.smartactors.in_memory_database_service_starter.in_memory_database_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.database.interfaces.idatabase.exception.IDatabaseException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;


public class InMemoryDBSectionProcessingStrategyTest extends IOCInitializer {
    private InMemoryDatabase inMemoryDatabase;
    private IObject mockObject;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }


    @Override
    protected void registerMocks() throws Exception {
        inMemoryDatabase = mock(InMemoryDatabase.class);
        mockObject = mock(IObject.class);
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

