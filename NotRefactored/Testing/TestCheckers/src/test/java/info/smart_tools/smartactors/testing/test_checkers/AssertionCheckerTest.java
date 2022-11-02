package info.smart_tools.smartactors.testing.test_checkers;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject_extension.configuration_object.ConfigurationObject;
import info.smart_tools.smartactors.iobject_extension_plugins.configuration_object_plugin.InitializeConfigurationObjectStrategies;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.interfaces.iassertion.IAssertion;
import info.smart_tools.smartactors.testing.interfaces.iassertion.exception.AssertionFailureException;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test for {@link AssertionChecker}.
 */
public class AssertionCheckerTest extends PluginsLoadingTestBase {
    private IAssertion assertion1Mock;
    private IAssertion assertion2Mock;
    private IMessageProcessor messageProcessorMock;
    private IObject environmentMock;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
        load(InitializeConfigurationObjectStrategies.class);
    }

    @Override
    protected void registerMocks()
            throws Exception {
        assertion1Mock = Mockito.mock(IAssertion.class);
        assertion2Mock = Mockito.mock(IAssertion.class);
        messageProcessorMock = Mockito.mock(IMessageProcessor.class);
        environmentMock = Mockito.mock(IObject.class);

        Mockito.when(messageProcessorMock.getEnvironment()).thenReturn(environmentMock);

        IOC.register(Keys.getKeyByName("assertion of type atype1"), new SingletonStrategy(assertion1Mock));
        IOC.register(Keys.getKeyByName("assertion of type atype2"), new SingletonStrategy(assertion2Mock));
    }

    @Test(expected = InitializationException.class)
    public void Should_constructorThrowWhenCannotResolveAssertionDependency()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'type': 'nonexist', 'name': 'Nope'}".replace('\'', '"'));

        new AssertionChecker(Collections.singletonList(a1desc));
    }

    @Test(expected = InitializationException.class)
    public void Should_constructorThrowWhenCannotResolveFieldNameDependency()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'type': 'atype1', 'name': 'Nope'}".replace('\'', '"'));

        IOC.unregister(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"));

        new AssertionChecker(Collections.singletonList(a1desc));
    }

    @Test(expected = InitializationException.class)
    public void Should_constructorThrowWhenCannotResolveIObjectDependency()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'type': 'atype1', 'name': 'Nope'}".replace('\'', '"'));

        IStrategy strategy = new ApplyFunctionToArgumentsStrategy(
                (a) -> {
                    try {
                        String name = (String) a[0];
                        if (name.equals("type")) {
                            throw new Exception();
                        }
                        return new FieldName(name);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                strategy
        );

        new AssertionChecker(Collections.singletonList(a1desc));
    }

    @Test
    public void Should_checkAssertions()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'type': 'atype1', 'name': 'Ass1'}".replace('\'', '"'));

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        checker.check(messageProcessorMock, null);
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenCannotReadValueFromEnvironment()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'type': 'atype1', 'name': 'Ass1'}".replace('\'', '"'));

        Mockito.when(environmentMock.getValue(Matchers.any())).thenThrow(ReadValueException.class);

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        checker.check(messageProcessorMock, null);
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenAssertionFails()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'type': 'atype1', 'name': 'Ass1'}".replace('\'', '"'));

        Mockito.doThrow(AssertionFailureException.class).when(assertion1Mock).check(Matchers.same(a1desc), Matchers.any());

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        checker.check(messageProcessorMock, null);
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenExceptionOccurs()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'type': 'atype1', 'name': 'Ass1'}".replace('\'', '"'));

        Mockito.doThrow(AssertionFailureException.class).when(assertion1Mock).check(Matchers.same(a1desc), Matchers.any());

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        checker.check(messageProcessorMock, new Exception());
    }

    @Test
    public void Should_createWrapperDescription()
            throws Exception {
        IStrategyRegistration strategy = IOC.resolve(Keys.getKeyByName("expandable_strategy#resolve key for configuration object"));
        strategy.register("in_", new ApplyFunctionToArgumentsStrategy(
                (a) -> {
                    try {
                        Object obj = a[1];
                        if (obj instanceof String) {
                            IObject innerObject = new ConfigurationObject();
                            innerObject.setValue(new FieldName("name"), "wds_getter_strategy");
                            innerObject.setValue(new FieldName("args"), new ArrayList<String>() {{ add((String) obj); }});
                            return new ArrayList<IObject>() {{ add(innerObject); }};
                        }
                        return obj;
                    } catch (Throwable e) {
                        throw new RuntimeException(
                                "Error in configuration 'wrapper' rule.", e
                        );
                    }
                })
        );

        IObject a1desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'type': 'atype1', 'name': 'Ass1', 'value': 'message/x'}".replace('\'', '"'));

        Mockito.doThrow(AssertionFailureException.class).when(assertion1Mock).check(Matchers.same(a1desc), Matchers.any());

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        IObject desc = checker.getSuccessfulReceiverArguments();
        IObject wrapper = (IObject) desc.getValue(new FieldName("wrapper"));
        assertNotNull(wrapper);
        List<IObject> transformationRules = (List<IObject>) wrapper.getValue(new FieldName("in_Ass1"));
        assertNotNull(transformationRules);
        assertEquals(transformationRules.size(), 1);
    }
}
