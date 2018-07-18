package info.smart_tools.smartactors.endpoint_components_generic.add_final_actions_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AddFinalActionsHandlerTest extends TrivialPluginsLoadingTestBase {
    private IObject env;
    private IObject context;
    private IDefaultMessageContext<Object, IObject, Object> ctx;
    private IMessageHandlerCallback<IDefaultMessageContext<Object, IObject, Object>> callback;
    private IAction<IObject> action1, action2, action3;
    private List<IAction<IObject>> actions;

    @Override
    protected void registerMocks() throws Exception {
        env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        env.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"),
                context
        );

        ctx = mock(IDefaultMessageContext.class);
        when(ctx.getDstMessage()).thenReturn(env);

        callback = mock(IMessageHandlerCallback.class);

        action1 = mock(IAction.class);
        action2 = mock(IAction.class);
        action3 = mock(IAction.class);

        actions = Arrays.asList(action1, action2);
    }

    @Test
    public void Should_addListAndActions() throws Exception {
        doAnswer(i -> {
            List l = (List) context.getValue(
                    IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "finalActions")
            );
            assertNotSame(actions, l);
            assertTrue(l.contains(action1));
            assertTrue(l.contains(action2));
            return null;
        }).when(callback).handle(any());

        new AddFinalActionsHandler<>(actions).handle(callback, ctx);

        verify(callback).handle(same(ctx));
    }

    @Test
    public void Should_keepListAndAddActions() throws Exception {
        List l0 = new ArrayList();
        l0.add(action3);

        context.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "finalActions"),
                l0
        );

        doAnswer(i -> {
            List l = (List) context.getValue(
                    IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "finalActions")
            );
            assertSame(l0, l);
            assertTrue(l.contains(action1));
            assertTrue(l.contains(action2));
            assertTrue(l.contains(action3));
            return null;
        }).when(callback).handle(any());

        new AddFinalActionsHandler<>(actions).handle(callback, ctx);

        verify(callback).handle(same(ctx));
    }
}