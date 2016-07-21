package info.smart_tools.smartactors.core.map_router;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void Should_storeReceivers()
            throws Exception {
        Map map = mock(Map.class);
        Object id = mock(Object.class);
        IMessageReceiver receiver = mock(IMessageReceiver.class);

        MapRouter router = new MapRouter(map);

        router.register(id, receiver);
        verify(map).put(same(id), same(receiver));

        when(map.get(same(id))).thenReturn(receiver);

        assertSame(receiver, router.route(id));
    }

    @Test(expected = RouteNotFoundException.class)
    public void Should_throwWhenNoRouteFound()
            throws Exception {
        new MapRouter(mock(Map.class)).route(mock(Object.class));
    }
}