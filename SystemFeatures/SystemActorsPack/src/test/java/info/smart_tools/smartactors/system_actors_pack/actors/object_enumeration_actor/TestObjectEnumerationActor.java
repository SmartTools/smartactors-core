package info.smart_tools.smartactors.system_actors_pack.actors.object_enumeration_actor;

import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Test for {@link ObjectEnumerationActor}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class TestObjectEnumerationActor {

    private IChainStorage chainStorageMock;
    private IRouter routerMock;

    private IKey chainStorageKey = mock(IKey.class);
    private IKey routerKey = mock(IKey.class);

    private EnumerationResult result;
    private List listMock;

    @Before
    public void setUp()
        throws Exception {
        chainStorageMock = mock(IChainStorage.class);
        routerMock = mock(IRouter.class);

        mockStatic(IOC.class, Keys.class);
        when(Keys.getKeyByName(eq(IChainStorage.class.getCanonicalName()))).thenReturn(chainStorageKey);
        when(Keys.getKeyByName(eq(IRouter.class.getCanonicalName()))).thenReturn(routerKey);

        when(IOC.resolve(same(chainStorageKey))).thenReturn(chainStorageMock);
        when(IOC.resolve(same(routerKey))).thenReturn(routerMock);

        result = mock(EnumerationResult.class);
        listMock = mock(List.class);
    }

    @Test
    public void Should_enumerateChains_enumerateChains()
        throws Exception {
        when(chainStorageMock.enumerate()).thenReturn(listMock);

        new ObjectEnumerationActor().enumerateChains(result);

        verify(result).setItems(same(listMock));
    }

    @Test
    public void Should_enumerateReceivers_enumerateReceivers()
        throws Exception {
        when(routerMock.enumerate()).thenReturn(listMock);

        new ObjectEnumerationActor().enumerateReceivers(result);

        verify(result).setItems(same(listMock));
    }
}
