package info.smart_tools.smartactors.base.up_counter;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IPoorAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.IllegalUpCounterState;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class UpCounterTest {
    @Test
    public void Should_throwOnUnderflow()
            throws Exception {
        IUpCounter counter = new UpCounter();

        counter.up();
        counter.up();
        counter.down();
        counter.down();

        try {
            counter.down();
            fail();
        } catch (IllegalUpCounterState ignore) {
            // OK
        }
    }

    @Test
    public void Should_throwWhenSystemIsDown()
            throws Exception {
        IUpCounter counter = new UpCounter();

        counter.shutdown(new Object());

        try {
            counter.up();
            fail();
        } catch (IllegalUpCounterState ignore) {}

        try {
            counter.down();
            fail();
        } catch (IllegalUpCounterState ignore) {}

        try {
            counter.forceShutdown();
            fail();
        } catch (IllegalUpCounterState ignore) {}
    }

    @Test
    public void Should_shutdown()
            throws Exception {
        IUpCounter counter = new UpCounter();

        counter.up();

        counter.shutdown(new Object());

        counter.up();
        counter.down();
        counter.down();

        try {
            counter.up();
            fail();
        } catch (IllegalUpCounterState ignore) {}
    }

    @Test
    public void Should_forciblyShutdown()
            throws Exception {
        IUpCounter counter = new UpCounter();

        counter.up();

        counter.forceShutdown();

        try {
            counter.down();
            fail();
        } catch (IllegalUpCounterState ignore) {}
    }

    @Test
    public void Should_executeCallbacks()
            throws Exception {
        IUpCounter counter = new UpCounter();

        Object mode = new Object();

        IAction<Object> srCb1 = mock(IAction.class), srCb2 = mock(IAction.class), srCb3 = mock(IAction.class);
        IPoorAction scCb1 = mock(IPoorAction.class), scCb2 = mock(IPoorAction.class), scCb3 = mock(IPoorAction.class);

        doThrow(ActionExecuteException.class).when(srCb1).execute(any());
        doThrow(ActionExecuteException.class).when(srCb3).execute(any());
        doThrow(ActionExecuteException.class).when(scCb1).execute();
        doThrow(ActionExecuteException.class).when(scCb3).execute();

        counter.onShutdownRequest(srCb1);
        counter.onShutdownRequest(srCb2);
        counter.onShutdownRequest(srCb3);
        counter.onShutdownComplete(scCb1);
        counter.onShutdownComplete(scCb2);
        counter.onShutdownComplete(scCb3);

        counter.up();

        try {
            counter.shutdown(mode);
            fail();
        } catch (UpCounterCallbackExecutionException e) {
            // cause + 1
            assertEquals(1, e.getSuppressed().length);
        }

        verify(srCb1, times(1)).execute(same(mode));
        verify(srCb2, times(1)).execute(same(mode));
        verify(srCb3, times(1)).execute(same(mode));
        verifyNoMoreInteractions(scCb1, scCb2, scCb3);

        reset(srCb1, srCb2, srCb3);

        try {
            counter.down();
            fail();
        } catch (UpCounterCallbackExecutionException e) {
            // cause + 1
            assertEquals(1, e.getSuppressed().length);
        }

        verify(scCb1, times(1)).execute();
        verify(scCb2, times(1)).execute();
        verify(scCb3, times(1)).execute();
        verifyNoMoreInteractions(srCb1, srCb2, srCb3);
    }

    @Test
    public void Should_workWithParentCounter()
            throws Exception {
        Object mode = new Object();

        IAction<Object> srCb1 = mock(IAction.class), srCb2 = mock(IAction.class), srCb3 = mock(IAction.class);
        IPoorAction scCb1 = mock(IPoorAction.class), scCb2 = mock(IPoorAction.class), scCb3 = mock(IPoorAction.class);

        IUpCounter parent = new UpCounter();
        IUpCounter counter = new UpCounter(parent);

        counter.up();

        doThrow(ActionExecuteException.class).when(srCb1).execute(any());
        doThrow(ActionExecuteException.class).when(srCb3).execute(any());
        doThrow(ActionExecuteException.class).when(scCb1).execute();
        doThrow(ActionExecuteException.class).when(scCb3).execute();

        counter.onShutdownRequest(srCb1);
        counter.onShutdownRequest(srCb2);
        counter.onShutdownRequest(srCb3);
        counter.onShutdownComplete(scCb1);
        counter.onShutdownComplete(scCb2);
        counter.onShutdownComplete(scCb3);

        try {
            parent.shutdown(mode);
            fail();
        } catch (UpCounterCallbackExecutionException ignore) { }

        verify(srCb1, times(1)).execute(same(mode));
        verify(srCb2, times(1)).execute(same(mode));
        verify(srCb3, times(1)).execute(same(mode));
        verifyNoMoreInteractions(scCb1, scCb2, scCb3);

        reset(srCb1, srCb2, srCb3);

        try {
            counter.down();
            fail();
        } catch (UpCounterCallbackExecutionException ignore) { }

        verify(scCb1, times(1)).execute();
        verify(scCb2, times(1)).execute();
        verify(scCb3, times(1)).execute();
        verifyNoMoreInteractions(srCb1, srCb2, srCb3);

        try {
            parent.up();
            fail();
        } catch (IllegalUpCounterState ignore) { }
    }

    @Test
    public void Should_callCallbacksOnParentForceShutdown()
            throws Exception {
        IPoorAction scCb = mock(IPoorAction.class);

        IUpCounter parent = new UpCounter();
        IUpCounter counter = new UpCounter(parent);

        doThrow(ActionExecuteException.class).when(scCb).execute();

        counter.onShutdownComplete(scCb);

        try {
            parent.forceShutdown();
            fail();
        } catch (UpCounterCallbackExecutionException ignore) {}

        verify(scCb).execute();
    }

    @Test
    public void Should_shutdownImmediatelyWhenParentDoes()
            throws Exception {
        IPoorAction scCb = mock(IPoorAction.class);

        IUpCounter parent = new UpCounter();
        IUpCounter counter = new UpCounter(parent);

        counter.onShutdownComplete(scCb);

        parent.shutdown(new Object());

        verify(scCb).execute();

        try {
            counter.up();
            fail();
        } catch (IllegalUpCounterState ignore) { }
    }
}
