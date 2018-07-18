package info.smart_tools.smartactors.endpoint_components_generic.null_client_callback;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class NullClientCallbackTest {
    private IObject env, res;
    private Throwable exc;

    @Before public void setUp() {
        env = res = mock(IObject.class);
        exc = mock(Throwable.class);
    }

    @Test public void Should_doNothing() throws Exception {
        new NullClientCallback().onStart(env);
        new NullClientCallback().onSuccess(env, res);
        new NullClientCallback().onError(env, exc);
        verifyZeroInteractions(env, res, exc);
    }
}
