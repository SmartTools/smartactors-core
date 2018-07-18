package info.smart_tools.smartactors.endpoint_components_generic.composite_exceptional_action;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link CompositeActionCreationStrategy}.
 */
public class CompositeActionCreationStrategyTest extends TrivialPluginsLoadingTestBase {
    private IBiAction[] actions = new IBiAction[8];
    private IResolveDependencyStrategy actionStrategy;
    private Object contextStub = new Object();

    @Override
    protected void registerMocks() throws Exception {
        actionStrategy = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("exceptional endpoint action"), actionStrategy);

        for (int i = 0; i < actions.length; i++) {
            actions[i] = mock(IBiAction.class);
            when(actionStrategy.resolve(eq(String.valueOf(i)), notNull())).thenReturn(actions[i]);
        }
    }

    @Test public void Should_parseConfigAndCreateStrategy() throws Exception {
        String conf = ("{" +
                "   'exceptionClassActions': [" +
                "       {" +
                "           'class': 'java.lang.IllegalArgumentException', 'action': '2'" +
                "       }," +
                "       {" +
                "           'class': 'java.lang.IllegalStateException', 'action': '3'" +
                "       }," +
                "       {" +
                "           'class': 'java.lang.Error', 'action': '4'" +
                "       }" +
                "   ]," +
                "   'defaultAction': {'action': '1'}" +
                "}").replace('\'','"');
        IObject confObj = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), conf);

        IBiAction action = new CompositeActionCreationStrategy().resolve(null, confObj);

        Throwable e = new IllegalArgumentException("nope");

        action.execute(contextStub, e);
        verify(actions[2]).execute(same(contextStub), same(e));
        verifyNoMoreInteractions(Arrays.copyOf(actions, actions.length, Object[].class));
        reset(actions[2]);

        e = new AssertionError();

        action.execute(contextStub, e);
        verify(actions[4]).execute(same(contextStub), same(e));
        verifyNoMoreInteractions(Arrays.copyOf(actions, actions.length, Object[].class));

        e = new Exception();

        action.execute(contextStub, e);
        verify(actions[1]).execute(same(contextStub), same(e));
        verifyNoMoreInteractions(Arrays.copyOf(actions, actions.length, Object[].class));
    }
}
