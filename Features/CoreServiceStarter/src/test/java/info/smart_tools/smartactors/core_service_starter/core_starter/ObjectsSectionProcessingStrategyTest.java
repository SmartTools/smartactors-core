package info.smart_tools.smartactors.core_service_starter.core_starter;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.map_router.MapRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test for {@link ObjectsSectionProcessingStrategy}
 */
public class ObjectsSectionProcessingStrategyTest extends IOCInitializer {
    private IReceiverObjectListener listenerMock;
    private IReceiverObjectCreator[] creatorMocks;
    private IObject[] objectConfigMocks;
    private IObject configMock;
    private IStrategy fullCreatorResolutionStrategy;

    @Before
    public void init() throws Exception {

        IOC.register(
                Keys.getKeyByName(IRouter.class.getCanonicalName()),
                new SingletonStrategy(new MapRouter(new ConcurrentHashMap<>())));
    }

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        configMock = mock(IObject.class);
        listenerMock = mock(IReceiverObjectListener.class);
        fullCreatorResolutionStrategy = mock(IStrategy.class);

        creatorMocks = new IReceiverObjectCreator[3];
        objectConfigMocks = new IObject[creatorMocks.length];

        for (int i = 0; i < creatorMocks.length; i++) {
            creatorMocks[i] = mock(IReceiverObjectCreator.class);
            objectConfigMocks[i] = mock(IObject.class);
            when(fullCreatorResolutionStrategy.resolve(same(objectConfigMocks[i]))).thenReturn(creatorMocks[i]);
        }

        when(configMock.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects")))
                .thenReturn(Arrays.asList(objectConfigMocks));

        IOC.register(Keys.getKeyByName("global router registration receiver object listener"), new SingletonStrategy(listenerMock));
        IOC.register(Keys.getKeyByName("full receiver object creator"), fullCreatorResolutionStrategy);
    }

    @Test
    public void Should_createObjects()
            throws Exception {
        IFieldName sectionName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects");

        new ObjectsSectionProcessingStrategy(sectionName, "schema").onLoadConfig(configMock);

        for (int i = 0; i < creatorMocks.length; i++) {
            verify(creatorMocks[i]).create(same(listenerMock), same(objectConfigMocks[i]), any());
        }
    }

    @Test
    public void Should_storeSectionName()
            throws Exception {
        IFieldName sectionName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects");

        assertEquals(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects"),
                new ObjectsSectionProcessingStrategy(sectionName, "schema").getSectionName()
        );
    }

    @Test(expected = ConfigurationProcessingException.class)
    public void Should_wrapCreationExceptions()
            throws Exception {
        IFieldName sectionName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects");

        doThrow(ReceiverObjectCreatorException.class).when(creatorMocks[0]).create(any(), any(), any());

        new ObjectsSectionProcessingStrategy(sectionName, "schema").onLoadConfig(configMock);
    }

    @Test
    public void Should_disposeObjects()
            throws Exception {
        IFieldName sectionName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects");

        ObjectsSectionProcessingStrategy strategy = new ObjectsSectionProcessingStrategy(sectionName, "schema");
        strategy.onLoadConfig(configMock);

        for (int i = 0; i < creatorMocks.length; i++) {
            when(objectConfigMocks[i].getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name")))
                    .thenReturn("someObject");
        }
        strategy.onRevertConfig(configMock);
    }

    @Test
    public void Should_throwExceptions()
            throws Exception {
        IFieldName sectionName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects");

        ObjectsSectionProcessingStrategy strategy = new ObjectsSectionProcessingStrategy(sectionName, "schema");
        strategy.onLoadConfig(configMock);

        for (int i = 1; i < creatorMocks.length; i++) {
            when(objectConfigMocks[i].getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name")))
                    .thenReturn("someObject");
        }
        doThrow(ReadValueException.class).when(objectConfigMocks[0]).getValue(any());

        try {
            strategy.onRevertConfig(configMock);
            fail();
        } catch(ConfigurationProcessingException e) {
        }

        doThrow(ReadValueException.class).when(configMock).getValue(any());

        try {
            strategy.onRevertConfig(configMock);
            fail();
        } catch(ConfigurationProcessingException e) {
        }
    }

}
