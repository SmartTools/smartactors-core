package info.smart_tools.smartactors.endpoint_components_generic.expect_sent_response_final_action;

import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.notNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ExpectSentResponseFinalActionTest extends TrivialPluginsLoadingTestBase {
    private IObject env;
    private IObject context;
    private Object cctx;
    private IBiAction<Object, Throwable> excAction;

    @Override
    protected void registerMocks() throws Exception {
        env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        env.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"),
                context
        );

        context.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionContext"),
                cctx
        );

        excAction = mock(IBiAction.class);
    }

    @Test
    public void Should_notCallExceptionalActionWhenFlagIsSet() throws Exception {
        context.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responseSent"),
                Boolean.TRUE
        );

        new ExpectSentResponseFinalAction<>(excAction).execute(env);

        verifyZeroInteractions(excAction);
    }

    @Test
    public void Should_callExceptionalActionWhenFlagIsFalse() throws Exception {
        context.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responseSent"),
                Boolean.FALSE
        );

        new ExpectSentResponseFinalAction<>(excAction).execute(env);

        verify(excAction).execute(same(cctx), (Throwable) notNull());
    }

    @Test
    public void Should_callExceptionalActionWhenFlagIsNotSet() throws Exception {
        new ExpectSentResponseFinalAction<>(excAction).execute(env);

        verify(excAction).execute(same(cctx), (Throwable) notNull());
    }
}
