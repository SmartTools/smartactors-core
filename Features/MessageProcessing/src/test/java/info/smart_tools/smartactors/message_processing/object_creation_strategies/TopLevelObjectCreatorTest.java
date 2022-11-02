package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link TopLevelObjectCreator}.
 */
public class TopLevelObjectCreatorTest extends IOCInitializer {
    private IObject configMock;
    private IObject contextMock;
    private IReceiverObjectListener listenerMock;
    private IStrategy objectResolutionStrategy;
    private Object object = new Object();

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy");
    }
    @Override
    protected void registerMocks() throws Exception {
        configMock = mock(IObject.class);
        contextMock = mock(IObject.class);
        listenerMock = mock(IReceiverObjectListener.class);
        objectResolutionStrategy = mock(IStrategy.class);

        IOC.register(Keys.getKeyByName("the object dependency"), objectResolutionStrategy);

        when(objectResolutionStrategy.resolve(same(configMock))).thenReturn(object);

        when(configMock.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency")))
                .thenReturn("the object dependency");
    }

    @Test
    public void Should_resolveObjectNotifyListenerAndStoreItInContext()
            throws Exception {
        IReceiverObjectCreator creator = new TopLevelObjectCreator();

        creator.create(listenerMock, configMock, contextMock);

        verify(objectResolutionStrategy, times(1)).resolve(any());
        verify(listenerMock).acceptItem(isNull(), same(object));
        verify(listenerMock).endItems();
        verify(contextMock).setValue(eq(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "topLevelObject")), same(object));
    }

    @Test
    public void Should_returnListOfOneIdentifier()
            throws Exception {
        IReceiverObjectCreator creator = new TopLevelObjectCreator();

        assertEquals(Collections.singletonList(null), creator.enumIdentifiers(configMock, contextMock));
    }

    @Test(expected = ReceiverObjectCreatorException.class)
    public void Should_wrapExceptionWhenErrorOccursResolvingObject()
            throws Exception {
        IReceiverObjectCreator creator = new TopLevelObjectCreator();

        when(objectResolutionStrategy.resolve(any())).thenThrow(StrategyException.class);

        creator.create(listenerMock, configMock, contextMock);
    }
}
