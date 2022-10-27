package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for {@link SetAddressFromObjectNameReceiverCreator}.
 */
public class SetAddressFromObjectNameReceiverCreatorTest extends IOCInitializer {
    private IObject configMock;
    private IObject contextMock;
    private IObject filterConfigMock;
    private IReceiverObjectCreator underlyingCreatorMock;
    private IReceiverObjectListener listenerMock;
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
        filterConfigMock = mock(IObject.class);
        underlyingCreatorMock = mock(IReceiverObjectCreator.class);
        listenerMock = mock(IReceiverObjectListener.class);

        when(configMock.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name"))).thenReturn("the_object_name");

        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem("old_object_name", object);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
            return null;
        }).when(underlyingCreatorMock).create(any(), any(), any());
    }

    @Test
    public void Should_replaceObjectName()
            throws Exception {
        IReceiverObjectCreator creator = new SetAddressFromObjectNameReceiverCreator(underlyingCreatorMock, filterConfigMock, configMock);

        creator.create(listenerMock, configMock, contextMock);

        verify(listenerMock).acceptItem(eq("the_object_name"), same(object));
        verify(listenerMock).endItems();
    }

    @Test
    public void Should_enumerateTheName()
            throws Exception {
        IReceiverObjectCreator creator = new SetAddressFromObjectNameReceiverCreator(underlyingCreatorMock, filterConfigMock, configMock);

        assertEquals(Collections.singletonList("the_object_name"), creator.enumIdentifiers(configMock, contextMock));
    }
}
