package info.smart_tools.smartactors.message_processing_plugins.standard_object_creators_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions.ObjectCreationException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link RawObjectCreator}.
 */
@PrepareForTest({IOC.class, Keys.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class RawObjectCreatorTest {
    private IKey fieldNameKey;
    private IKey routeFromObjectNameKey;
    private IKey dependencyKey;
    private IRouter routerMock;
    private IObject descriptionMock;
    private IFieldName dependencyFieldName;
    private IFieldName nameFieldName;

    @Before
    public void setUp()
            throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        dependencyFieldName = mock(IFieldName.class);
        nameFieldName = mock(IFieldName.class);

        fieldNameKey = mock(IKey.class);
        routeFromObjectNameKey = mock(IKey.class);
        dependencyKey = mock(IKey.class);

        when(IOC.resolve(fieldNameKey, "dependency")).thenReturn(dependencyFieldName);
        when(IOC.resolve(fieldNameKey, "name")).thenReturn(nameFieldName);

        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(fieldNameKey);
        when(Keys.getKeyByName("route_from_object_name")).thenReturn(routeFromObjectNameKey);

        routerMock = mock(IRouter.class);
        descriptionMock = mock(IObject.class);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenRouterIsNull()
            throws Exception {
        IRoutedObjectCreator creator = new RawObjectCreator();
        creator.createObject(null, descriptionMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenDescriptionIsNull()
            throws Exception {
        IRoutedObjectCreator creator = new RawObjectCreator();
        creator.createObject(routerMock, null);
    }

    @Test
    public void Should_resolveDependencyAndRegisterInRouter()
            throws Exception {
        IRoutedObjectCreator creator = new RawObjectCreator();
        IMessageReceiver receiverMock = mock(IMessageReceiver.class);

        when(descriptionMock.getValue(same(dependencyFieldName))).thenReturn("dependency_1");
        when(descriptionMock.getValue(same(nameFieldName))).thenReturn("name_1");
        when(Keys.getKeyByName("dependency_1")).thenReturn(dependencyKey);
        when(IOC.resolve(same(dependencyKey), same(descriptionMock))).thenReturn(receiverMock);
        when(IOC.resolve(same(routeFromObjectNameKey), eq("name_1"))).thenReturn("route_1");

        creator.createObject(routerMock, descriptionMock);

        verify(routerMock).register(eq("route_1"), same(receiverMock));
    }

    @Test(expected = ObjectCreationException.class)
    public void Should_throw_When_dependencyIsNotReceiver()
            throws Exception {
        IRoutedObjectCreator creator = new RawObjectCreator();
        Object notReceiverMock = mock(Object.class);

        when(descriptionMock.getValue(same(dependencyFieldName))).thenReturn("dependency_1");
        when(descriptionMock.getValue(same(nameFieldName))).thenReturn("name_1");
        when(Keys.getKeyByName("dependency_1")).thenReturn(dependencyKey);
        when(IOC.resolve(same(dependencyKey), same(descriptionMock))).thenReturn(notReceiverMock);
        when(IOC.resolve(same(routeFromObjectNameKey), eq("name_1"))).thenReturn("route_1");

        creator.createObject(routerMock, descriptionMock);
    }

    @Test(expected = ObjectCreationException.class)
    public void Should_wrapExceptions()
            throws Exception {
        IRoutedObjectCreator creator = new RawObjectCreator();
        Object notReceiverMock = mock(Object.class);

        when(descriptionMock.getValue(same(dependencyFieldName))).thenReturn("dependency_1");
        when(descriptionMock.getValue(same(nameFieldName))).thenReturn("name_1");
        when(Keys.getKeyByName("dependency_1")).thenReturn(dependencyKey);
        when(IOC.resolve(any(), any())).thenThrow(ResolutionException.class);

        creator.createObject(routerMock, descriptionMock);
    }
}
