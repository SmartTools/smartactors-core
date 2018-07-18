package info.smart_tools.smartactors.endpoint_components_generic.scope_setter_message_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ScopeSetterMessageHandlerTest extends TrivialPluginsLoadingTestBase {
    @Test public void Should_setScopeForNextHandlers() throws Exception {
        IScope scope = ScopeProvider.getScope(ScopeProvider.createScope(null));
        IMessageHandlerCallback callback = mock(IMessageHandlerCallback.class);
        IMessageContext messageContext = mock(IMessageContext.class);

        doAnswer(invocationOnMock -> {
            assertSame(messageContext, invocationOnMock.getArgumentAt(0, IMessageContext.class));
            assertSame(ScopeProvider.getCurrentScope(), scope);
            return null;
        }).when(callback).handle(any());

        new ScopeSetterMessageHandler(scope).handle(callback, messageContext);

        verify(callback).handle(same(messageContext));
    }
}
