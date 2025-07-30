package info.smart_tools.smartactors.base.up_counter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.IllegalUpCounterState;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import org.junit.Test;

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
        IActionNoArgs scCb1 = mock(IActionNoArgs.class), scCb2 = mock(IActionNoArgs.class), scCb3 = mock(IActionNoArgs.class),
                scCb4 = mock(IActionNoArgs.class);

        doThrow(ActionExecutionException.class).when(srCb1).execute(any());
        doThrow(ActionExecutionException.class).when(srCb3).execute(any());
        doThrow(ActionExecutionException.class).when(scCb1).execute();
        doThrow(ActionExecutionException.class).when(scCb3).execute();
        doThrow(InvalidArgumentException.class).when(scCb4).execute();

        counter.onShutdownRequest(this.toString()+"1",srCb1);
        counter.onShutdownRequest(this.toString()+"2",srCb2);
        counter.onShutdownRequest(this.toString()+"3",srCb3);
        counter.onShutdownComplete(this.toString()+"1",scCb1);
        counter.onShutdownComplete(this.toString()+"2",scCb2);
        counter.onShutdownComplete(this.toString()+"3",scCb3);
        counter.onShutdownComplete(this.toString()+"4",scCb4);

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

        reset(srCb1, srCb2, srCb3);

        try {
            counter.down();
            fail();
        } catch (UpCounterCallbackExecutionException e) {
            // cause + 1
            assertEquals(2, e.getSuppressed().length);
        }

        verify(scCb1, times(1)).execute();
        verify(scCb2, times(1)).execute();
        verify(scCb3, times(1)).execute();

        counter.removeFromShutdownRequest(this.toString()+"3");
        IActionNoArgs act1 = counter.onShutdownComplete(this.toString()+"4",scCb4);
        try {
            act1.execute();
        } catch(Exception ignore) {}
        IActionNoArgs act2 = counter.onShutdownComplete(this.toString()+"2",scCb2);
        act2.execute();

        IActionNoArgs act3 = counter.removeFromShutdownComplete(this.toString()+"4");
        try {
            act3.execute();
        } catch(Exception ignore) {}
        IActionNoArgs act4 = counter.removeFromShutdownComplete(this.toString()+"2");
        act4.execute();
        verify(scCb4, times(3)).execute();
        verify(scCb2, times(3)).execute();
        verifyNoMoreInteractions(scCb1, scCb2, scCb3, scCb4);
    }

    @Test
    public void Should_workWithParentCounter()
            throws Exception {
        Object mode = new Object();

        IAction<Object> srCb1 = mock(IAction.class), srCb2 = mock(IAction.class), srCb3 = mock(IAction.class);
        IActionNoArgs scCb1 = mock(IActionNoArgs.class), scCb2 = mock(IActionNoArgs.class), scCb3 = mock(IActionNoArgs.class);
        IUpCounter badParent = mock(UpCounter.class);

        IUpCounter parent = new UpCounter();
        IUpCounter counter = new UpCounter(parent);

        counter.up();

        doThrow(ActionExecutionException.class).when(srCb1).execute(any());
        doThrow(ActionExecutionException.class).when(srCb3).execute(any());
        doThrow(ActionExecutionException.class).when(scCb1).execute();
        doThrow(ActionExecutionException.class).when(scCb3).execute();

        doThrow(ActionExecutionException.class).when(badParent).onShutdownRequest(any(),any());
        try {
            IUpCounter badCounter1 = new UpCounter(badParent);
        } catch (Exception ignore) {}
        doThrow(ActionExecutionException.class).when(badParent).down();
        try {
            IUpCounter badCounter2 = new UpCounter(badParent);
        } catch (Exception ignore) {}

        counter.onShutdownRequest(this.toString()+"1",srCb1);
        counter.onShutdownRequest(this.toString()+"2",srCb2);
        counter.onShutdownRequest(this.toString()+"3",srCb3);
        counter.onShutdownComplete(this.toString()+"1",scCb1);
        counter.onShutdownComplete(this.toString()+"2",scCb2);
        counter.onShutdownComplete(this.toString()+"3",scCb3);

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
        IActionNoArgs scCb = mock(IActionNoArgs.class);

        IUpCounter parent = new UpCounter();
        IUpCounter counter = new UpCounter(parent);

        doThrow(ActionExecutionException.class).when(scCb).execute();

        counter.onShutdownComplete(this.toString(),scCb);

        try {
            parent.forceShutdown();
            fail();
        } catch (UpCounterCallbackExecutionException ignore) {}

        verify(scCb).execute();
    }

    @Test
    public void Should_shutdownImmediatelyWhenParentDoes()
            throws Exception {
        IActionNoArgs scCb = mock(IActionNoArgs.class);

        IUpCounter parent = new UpCounter();
        IUpCounter counter = new UpCounter(parent);

        counter.onShutdownComplete(this.toString(),scCb);

        parent.shutdown(new Object());

        verify(scCb).execute();

        try {
            counter.up();
            fail();
        } catch (IllegalUpCounterState ignore) { }
    }
}
