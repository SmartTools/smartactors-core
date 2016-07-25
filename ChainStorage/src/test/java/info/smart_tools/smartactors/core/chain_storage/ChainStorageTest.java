package info.smart_tools.smartactors.core.chain_storage;

import info.smart_tools.smartactors.core.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.core.ichain_storage.exceptions.ChainCreationException;
import info.smart_tools.smartactors.core.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Test for {@link ChainStorage}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(IOC.class)
public class ChainStorageTest {
    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_chainsMapIsNull()
            throws Exception {
        assertNull(new ChainStorage(null, mock(IRouter.class)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_routerIsNull()
            throws Exception {
        assertNull(new ChainStorage(mock(Map.class), null));
    }

    @Test(expected = ChainNotFoundException.class)
    public void Should_throw_When_chainIsNotFound()
            throws Exception {
        Map mapMock = mock(Map.class);
        IRouter routerMock = mock(IRouter.class);

        IChainStorage storage = new ChainStorage(mapMock, routerMock);

        storage.resolve(mock(Object.class));
    }

    @Test
    public void Should_returnChain_When_chainIsFound()
            throws Exception {
        Map mapMock = mock(Map.class);
        IRouter routerMock = mock(IRouter.class);
        IReceiverChain chainMock = mock(IReceiverChain.class);
        Object idMock = mock(Object.class);

        when(mapMock.get(same(idMock))).thenReturn(chainMock);

        IChainStorage storage = new ChainStorage(mapMock, routerMock);

        assertSame(chainMock, storage.resolve(idMock));
    }

    @Test
    public void Should_resolveAndStoreChains()
            throws Exception {
        mockStatic(IOC.class);

        IKey keyStorageKey = mock(IKey.class);
        IKey receiverChainKey = mock(IKey.class);

        IRouter routerMock = mock(IRouter.class);
        Map mapMock = mock(Map.class);
        IReceiverChain receiverChainMock = mock(IReceiverChain.class);
        Object chainId = mock(Object.class);
        IObject chainDesc = mock(IObject.class);

        PowerMockito.when(IOC.getKeyForKeyStorage()).thenReturn(keyStorageKey);
        PowerMockito.when(IOC.resolve(same(keyStorageKey), eq(IReceiverChain.class.toString())))
                .thenReturn(receiverChainKey);

        ChainStorage chainStorage = new ChainStorage(mapMock, routerMock);

        PowerMockito
                .when(IOC.resolve(same(receiverChainKey), same(chainId), same(chainDesc), same(chainStorage), same(routerMock)))
                .thenReturn(receiverChainMock);

        chainStorage.register(chainId, chainDesc);

        Mockito.verify(mapMock).put(chainId, receiverChainMock);
    }

    @Test
    public void Should_wrapExceptionsThrownByIOC()
            throws Exception {
        mockStatic(IOC.class);

        ResolutionException resolutionException = mock(ResolutionException.class);
        IRouter routerMock = mock(IRouter.class);
        Map mapMock = mock(Map.class);
        Object chainId = mock(Object.class);
        IObject chainDesc = mock(IObject.class);

        ChainStorage chainStorage = new ChainStorage(mapMock, routerMock);

        PowerMockito.when(IOC.resolve(any(),any())).thenThrow(resolutionException);

        try {
            chainStorage.register(chainId, chainDesc);
            fail();
        } catch (ChainCreationException e) {
            assertSame(resolutionException, e.getCause());
        }
    }
}
