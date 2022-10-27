package info.smart_tools.smartactors.debugger.session_impl;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerBreakpointsStorage;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSequence;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link DebuggerBreakpointsStorageImpl}.
 */
public class DebuggerBreakpointsStorageImplTest extends IOCInitializer {
    private IChainStorage chainStorageMock;
    private IReceiverChain chainMock;
    private IObject stepArgsMock1, stepArgsMock2;
    private IDebuggerSequence debuggerSequenceMock;
    private IMessageProcessingSequence realSequenceMock;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        chainStorageMock = mock(IChainStorage.class);

        IOC.register(Keys.getKeyByName(IChainStorage.class.getCanonicalName()), new SingletonStrategy(chainStorageMock));

        debuggerSequenceMock = mock(IDebuggerSequence.class);
        realSequenceMock = mock(IMessageProcessingSequence.class);

        when(debuggerSequenceMock.getRealSequence()).thenReturn(realSequenceMock);

        chainMock = mock(IReceiverChain.class);
        stepArgsMock1 = mock(IObject.class);
        stepArgsMock2 = mock(IObject.class);

        IOC.register(Keys.getKeyByName("chain_id_from_map_name"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) args[0];
            }
        });
    }

    @Test
    public void Should_createBreakpoints()
            throws Exception {
        when(chainStorageMock.resolve("thatChain")).thenReturn(chainMock);
        when(chainMock.getArguments(42)).thenReturn(stepArgsMock1);
        when(chainMock.getArguments(21)).thenReturn(stepArgsMock2);

        IDebuggerBreakpointsStorage storage = new DebuggerBreakpointsStorageImpl();

        String id = storage.addBreakpoint(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'chain':'thatChain'," +
                        "'step':42," +
                        "'enabled':true" +
                        "}").replace('\'','"')));

        when(realSequenceMock.getCurrentReceiverArguments()).thenReturn(stepArgsMock1);
        assertTrue(storage.shouldBreakAt(debuggerSequenceMock));

        when(realSequenceMock.getCurrentReceiverArguments()).thenReturn(stepArgsMock2);
        assertFalse(storage.shouldBreakAt(debuggerSequenceMock));

        assertEquals(1, storage.listBreakpoints().size());

        String id2 = storage.addBreakpoint(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'chain':'thatChain'," +
                        "'step':21," +
                        "'enabled':false" +
                        "}").replace('\'','"')));

        assertEquals(2, storage.listBreakpoints().size());

        when(realSequenceMock.getCurrentReceiverArguments()).thenReturn(stepArgsMock2);
        assertFalse(storage.shouldBreakAt(debuggerSequenceMock));

        storage.modifyBreakpoint(id2, IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'" + id2 + "'," +
                        "'enabled':true" +
                        "}").replace('\'','"')));

        when(realSequenceMock.getCurrentReceiverArguments()).thenReturn(stepArgsMock2);
        assertTrue(storage.shouldBreakAt(debuggerSequenceMock));
    }
}
