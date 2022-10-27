package info.smart_tools.smartactors.message_processing.map_router;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MapRouter}.
 */
public class MapRouterTest {
    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_mapIsNull()
            throws Exception {
        assertNull(new MapRouter(null));
    }

    @Test
    public void Should_storeAndRevertReceivers()
            throws Exception {
        Map<Object, IMessageReceiver> map = mock(Map.class);
        Object id = mock(Object.class);
        IMessageReceiver receiver0 = mock(IMessageReceiver.class);
        IMessageReceiver receiver1 = mock(IMessageReceiver.class);

        MapRouter router = new MapRouter(map);

        router.register(id, receiver0);
        verify(map).put(same(id), same(receiver0));

        when(map.put(same(id), same(receiver1))).thenReturn(receiver0);
        router.register(id, receiver1);
        verify(map).put(same(id), same(receiver1));

        when(map.get(same(id))).thenReturn(receiver1);

        assertSame(receiver1, router.route(id));

        router.unregister(id);
        verify(map).remove(id);

        when(map.remove(same(id))).thenReturn(receiver1);
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
        Map<Object, IMessageReceiver> mapMock = mock(Map.class);
        when(mapMock.keySet()).thenReturn(keys);

        IRouter router = new MapRouter(mapMock);

        assertEquals(new ArrayList<Object>(keys), router.enumerate());
    }
}