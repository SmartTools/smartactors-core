package info.smart_tools.smartactors.testing_plugins.test_checkers_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_extension.configuration_object.ConfigurationObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.testing.interfaces.iassertion.IAssertion;
import info.smart_tools.smartactors.testing.interfaces.iresult_checker.IResultChecker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TestCheckersPluginTest {

    @Before
    public void init()
            throws Exception {
        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), new StrategyContainer());
        ScopeProvider.setCurrentScope(scope);

        IOC.register(
                IOC.getKeyForKeyByNameStrategy(),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new FieldName((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                new ApplyFunctionToArgumentsStrategy(
                        (a) -> {
                            try {
                                return new DSObject();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
        IOC.register(
                IOC.resolve(
                        IOC.getKeyForKeyByNameStrategy(), "configuration object"
                ),
                new ApplyFunctionToArgumentsStrategy(
                        (a) -> {

                            if (a.length == 0) {
                                return new ConfigurationObject();
                            } else if (a.length == 1 && a[0] instanceof String) {
                                try {
                                    return new ConfigurationObject((String) a[0]);
                                } catch (InvalidArgumentException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                throw new RuntimeException("Could not create new instance of Configuration Object.");
                            }
                        }
                )
        );
        IAssertion assertionMock = mock(IAssertion.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "assertion of type myType"), new SingletonStrategy(assertionMock)
        );
        Object receiverIdStrategyMock = mock(Object.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "receiver_id_from_iobject"), new SingletonStrategy(receiverIdStrategyMock)
        );
        IRouter routerMock = mock(IRouter.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IRouter.class.getCanonicalName()), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) routerMock;
            }
        });
    }

    @Test
    public void checkPluginCreation()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new TestCheckersPlugin(bootstrap);
        assertNotNull(plugin);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnCreation()
            throws Exception {
        new TestCheckersPlugin(null);
        fail();
    }

    @Test
    public void checkLoadExecution()
            throws Exception {
        Checker checker = new Checker();
        checker.item = new BootstrapItem("test");
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        List<IBootstrapItem<String>> itemList = new ArrayList<>();
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                checker.item = (IBootstrapItem<String>) args[0];
                itemList.add(checker.item);
                return null;
            }
        })
                .when(bootstrap)
                .add(any(IBootstrapItem.class));
        IPlugin plugin = new TestCheckersPlugin(bootstrap);
        plugin.load();
        assertEquals(itemList.size(), 1);
        IBootstrapItem<String> item = itemList.get(0);
        item.executeProcess();
        List<IObject> assertions = new ArrayList<>();
        IObject assertion = mock(IObject.class);
        assertions.add(assertion);
        when(assertion.getValue(new FieldName("name"))).thenReturn("myName");
        when(assertion.getValue(new FieldName("type"))).thenReturn("myType");
        when(assertion.getValue(new FieldName("value"))).thenReturn("myValue");
        IObject interception = mock(IObject.class);
        when(interception.getValue(new FieldName("class"))).thenReturn(Integer.class.getCanonicalName());
        IResultChecker assertChecker = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IResultChecker.class.getCanonicalName() + "#assert"), assertions);
        IResultChecker interceptChecker = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IResultChecker.class.getCanonicalName() + "#intercept"), interception);
        assertNotNull(assertChecker);
        assertNotNull(interceptChecker);
    }

    @Test (expected = PluginException.class)
    public void checkPluginExceptionOnPluginLoad()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new TestCheckersPlugin(bootstrap);
        doThrow(Exception.class).when(bootstrap).add(any(IBootstrapItem.class));
        plugin.load();
        fail();
    }
}
