package info.smart_tools.smartactors.version_management.versioned_map_router;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.message_processing.map_router.MapRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link VersionedMapRouter}.
 */
public class VersionedMapRouterTest {

    @Before
    public synchronized void init() {
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_mapIsNull()
            throws Exception {
        assertNull(new VersionedMapRouter(null));
    }

    @Test
    public void Should_storeAndRevertReceivers()
            throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        Map<Object, Map<IModule, IMessageReceiver>> map = new ConcurrentHashMap<>();
        Object id = mock(Object.class);
        IMessageReceiver receiver0 = mock(IMessageReceiver.class);
        IMessageReceiver receiver1 = mock(IMessageReceiver.class);
        ModuleManager.addModule("module", "module", "");
        IModule module = ModuleManager.getModuleById("module");
        IModule core = ModuleManager.getCurrentModule();

        IRouter router = new VersionedMapRouter(map);

        router.register(id, receiver0);
        assertSame(receiver0, router.route(id));

        ModuleManager.setCurrentModule(module);
        router.register(id, receiver1);
        assertSame(receiver1, router.route(id));

        ModuleManager.setCurrentModule(core);
        assertSame(receiver0, router.route(id));

        router.unregister(id);
        try {
            router.route(id);
            fail();
        } catch(RouteNotFoundException e) {}

        ModuleManager.setCurrentModule(module);
        router.unregister(id);
        verify(receiver1).dispose();
    }

    @Test(expected = RouteNotFoundException.class)
    public void Should_throwWhenNoRouteFound()
            throws Exception {
        new MapRouter(mock(Map.class)).route(mock(Object.class));
    }

    @Test
    public void Should_enumerate_returnListOfIdentifiersOfAllReceivers()
            throws Exception {
        Set<Object> keys = new HashSet<>(Arrays.asList(new Object(), new Object()));
        Map<Object, Map<IModule, IMessageReceiver>> mapMock = mock(Map.class);
        when(mapMock.keySet()).thenReturn(keys);

        IRouter router = new VersionedMapRouter(mapMock);

        assertEquals(new ArrayList<Object>(keys), router.enumerate());
    }
}
