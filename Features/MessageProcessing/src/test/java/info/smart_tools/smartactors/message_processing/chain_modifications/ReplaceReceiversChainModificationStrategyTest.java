package info.smart_tools.smartactors.message_processing.chain_modifications;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ReplaceReceiversChainModificationStrategy}.
 */
public class ReplaceReceiversChainModificationStrategyTest extends IOCInitializer {
    private IStrategy replaceStrategy1;
    private IStrategy replaceStrategy2;
    private IReceiverChain originalChainMock;

    private IMessageReceiver[] receivers = new IMessageReceiver[] {
            mock(IMessageReceiver.class), mock(IMessageReceiver.class),
            mock(IMessageReceiver.class), mock(IMessageReceiver.class),
            mock(IMessageReceiver.class), mock(IMessageReceiver.class)};

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        replaceStrategy1 = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("replacement 1 strategy"), replaceStrategy1);
        replaceStrategy2 = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("replacement 2 strategy"), replaceStrategy2);
        originalChainMock = mock(IReceiverChain.class);
    }

    @Test
    public void Should_forwardCallsToOriginalChain()
            throws Exception {
        when(originalChainMock.get(0)).thenReturn(receivers[0]);

        IReceiverChain decorated = new ReplaceReceiversChainModificationStrategy().resolve(originalChainMock,
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{'replacements':[]}".replace('\'','"')));

        assertSame(receivers[0], decorated.get(0));

        reset(originalChainMock);

        decorated.getArguments(1);
        verify(originalChainMock).getArguments(1);
        reset(originalChainMock);

        decorated.getId();
        verify(originalChainMock).getId();
        reset(originalChainMock);

        decorated.getExceptionalChainNamesAndEnvironments(null);
        verify(originalChainMock).getExceptionalChainNamesAndEnvironments(null);
        reset(originalChainMock);

        decorated.getExceptionalChainNames();
        verify(originalChainMock).getExceptionalChainNames();
        reset(originalChainMock);

        decorated.getChainDescription();
        verify(originalChainMock).getChainDescription();
    }

    @Test
    public void Should_replaceReceivers()
            throws Exception {
        IObject modDesc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{'replacements':[" +
                        "{" +
                        "'step':0," +
                        "'dependency':'replacement 1 strategy'," +
                        "'args':'args1'" +
                        "}," +
                        "{" +
                        "'step':3," +
                        "'dependency':'replacement 2 strategy'," +
                        "'args':'42'" +
                        "}]}").replace('\'','"'));

        when(originalChainMock.get(0)).thenReturn(receivers[0]);
        when(originalChainMock.get(1)).thenReturn(receivers[1]);
        when(originalChainMock.get(2)).thenReturn(receivers[2]);

        when(replaceStrategy1.resolve(same(receivers[0]), eq("args1")))
                .thenReturn(receivers[3])
                .thenThrow(StrategyException.class);

        when(replaceStrategy2.resolve(same(null), eq("42")))
                .thenReturn(receivers[4])
                .thenThrow(StrategyException.class);

        IReceiverChain decorated = new ReplaceReceiversChainModificationStrategy().resolve(originalChainMock, modDesc);

        assertSame(receivers[3], decorated.get(0));
        assertSame(receivers[1], decorated.get(1));
        assertSame(receivers[2], decorated.get(2));
        assertSame(receivers[4], decorated.get(3));
        assertNull(decorated.get(4));
    }

    @Test(expected = StrategyException.class)
    public void Should_throwWhenReplacementStepIndexIsTooLarge()
            throws Exception {
        IObject modDesc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{'replacements':[" +
                        "{" +
                        "'step':2," +
                        "'dependency':'replacement 1 strategy'," +
                        "'args':'args1'" +
                        "}]}").replace('\'','"'));

        when(originalChainMock.get(0)).thenReturn(receivers[0]);

        assertNotNull(new ReplaceReceiversChainModificationStrategy().resolve(originalChainMock, modDesc));
    }
}
