package info.smart_tools.smartactors.core.chain_testing;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link MainTestChain}.
 */
public class MainTestChainTest {
    private IAction<Throwable> completionCallbackMock;
    private IObject successArgumentsMock;

    @Before
    public void setUp()
            throws Exception {
        completionCallbackMock = mock(IAction.class);
        successArgumentsMock = mock(IObject.class);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenCallbackIsNull()
            throws Exception {
        assertNotNull(new MainTestChain(null, null));
    }

    @Test
    public void Should_haveName()
            throws Exception {
        IReceiverChain chain = new MainTestChain(completionCallbackMock, successArgumentsMock);
        assertNotNull(chain.getName());
    }

    @Test
    public void Should_returnSuccessReceiverArgumentsForFirstReceiverInChain()
            throws Exception {
        IReceiverChain chain = new MainTestChain(completionCallbackMock, successArgumentsMock);
        assertSame(successArgumentsMock, chain.getArguments(0));
        assertNull(chain.getArguments(1));
    }

    @Test
    public void Should_returnSuccessReceiverForFirstReceiverInChain()
            throws Exception {
        IReceiverChain chain = new MainTestChain(completionCallbackMock, successArgumentsMock);
        assertNotNull(chain.get(0));
        assertNull(chain.get(1));
    }

    @Test
    public void Should_callCallbackWhenChainCompletedWithException()
            throws Exception {
        Throwable exceptionMock = mock(Throwable.class);

        IReceiverChain chain = new MainTestChain(completionCallbackMock, successArgumentsMock);

        chain.getExceptionalChain(exceptionMock);
        chain.getExceptionalChain(exceptionMock);

        verify(completionCallbackMock, times(1)).execute(same(exceptionMock));
    }

    @Test
    public void Should_callCallbackWhenChainCompletedSuccessful()
            throws Exception {
        IReceiverChain chain = new MainTestChain(completionCallbackMock, successArgumentsMock);

        IMessageProcessor mpMock = mock(IMessageProcessor.class);

        chain.get(0).receive(mpMock);
        chain.get(0).receive(mpMock);

        verify(completionCallbackMock, times(1)).execute(null);
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_successReceiverWrapExceptionThrownByCallback()
            throws Exception {
        IReceiverChain chain = new MainTestChain(completionCallbackMock, successArgumentsMock);
        IMessageProcessor mpMock = mock(IMessageProcessor.class);

        doThrow(ActionExecuteException.class).when(completionCallbackMock).execute(null);

        chain.get(0).receive(mpMock);
    }
}
