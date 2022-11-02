package info.smart_tools.smartactors.version_management.versioned_router_decorator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.message_processing.map_router.MapRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link VersionedRouterDecorator}.
 */
public class VersionedRouterDecoratorTest {
    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_mapIsNull()
            throws Exception {
        assertNull(new VersionedRouterDecorator(null, null));
    }

    @Test
    public void Should_storeAndRevertReceivers()
            throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        Map<Object, Map<IModule, Object>> map = new ConcurrentHashMap<>();
        Object id = mock(Object.class);
        IMessageReceiver receiver0 = mock(IMessageReceiver.class);
        IMessageReceiver receiver1 = mock(IMessageReceiver.class);
        ModuleManager.addModule("module_id_1", "module_name_1", "");
        IModule module = ModuleManager.getModuleById("module_id_1");
        IModule core = ModuleManager.getCurrentModule();

        IRouter router = new VersionedRouterDecorator(map, new MapRouter(new ConcurrentHashMap<>()));

        router.register(id, receiver0);
        //verify(map).put(same(id), any());
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
        new VersionedRouterDecorator(mock(Map.class), mock(MapRouter.class)).route(mock(Object.class));
    }

    @Test
    public void Should_enumerate_returnListOfIdentifiersOfAllReceivers()
            throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        ArrayList<Object> keys = new ArrayList<>(Arrays.asList(new Object(), new Object()));
        Map<Object, Map<IModule, Object>> map = new ConcurrentHashMap<>();

        IRouter router = new VersionedRouterDecorator(map, new MapRouter(new ConcurrentHashMap<>()));
        router.register(keys.get(0), mock(IMessageReceiver.class));
        router.register(keys.get(1), mock(IMessageReceiver.class));

        List<Object> keys1 = router.enumerate();
        if (!keys1.containsAll(keys)) {
            fail();
        }
    }
}
