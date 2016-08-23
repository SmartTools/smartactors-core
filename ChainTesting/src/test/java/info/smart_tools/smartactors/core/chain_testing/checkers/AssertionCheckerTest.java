package info.smart_tools.smartactors.core.chain_testing.checkers;

import info.smart_tools.smartactors.core.chain_testing.Assertion;
import info.smart_tools.smartactors.core.chain_testing.exceptions.AssertionFailureException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.plugin.dsobject.PluginDSObject;
import info.smart_tools.smartactors.plugin.ifieldname.IFieldNamePlugin;
import info.smart_tools.smartactors.plugin.ioc_keys.PluginIOCKeys;
import info.smart_tools.smartactors.plugin.scope_provider.PluginScopeProvider;
import info.smart_tools.smartactors.plugin.scoped_ioc.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link AssertionChecker}.
 */
public class AssertionCheckerTest extends PluginsLoadingTestBase {
    private Assertion assertion1Mock;
    private Assertion assertion2Mock;
    private IMessageProcessor messageProcessorMock;
    private IObject environmentMock;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks()
            throws Exception {
        assertion1Mock = mock(Assertion.class);
        assertion2Mock = mock(Assertion.class);
        messageProcessorMock = mock(IMessageProcessor.class);
        environmentMock = mock(IObject.class);

        when(messageProcessorMock.getEnvironment()).thenReturn(environmentMock);

        IOC.register(Keys.getOrAdd("assertion of type atype1"), new SingletonStrategy(assertion1Mock));
        IOC.register(Keys.getOrAdd("assertion of type atype2"), new SingletonStrategy(assertion2Mock));
    }

    @Test(expected = TestStartupException.class)
    public void Should_constructorThrowWhenCannotResolveAssertionDependency()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'type': 'nonexist', 'name': 'Nope'}".replace('\'', '"'));

        new AssertionChecker(Collections.singletonList(a1desc));
    }

    @Test(expected = TestStartupException.class)
    public void Should_constructorThrowWhenCannotResolveFieldNameDependency()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'type': 'atype1', 'name': 'Nope'}".replace('\'', '"'));

        IOC.remove(Keys.getOrAdd(IFieldName.class.getCanonicalName()));

        new AssertionChecker(Collections.singletonList(a1desc));
    }

    @Test(expected = TestStartupException.class)
    public void Should_constructorThrowWhenCannotResolveIObjectDependency()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'type': 'atype1', 'name': 'Nope'}".replace('\'', '"'));

        IOC.remove(Keys.getOrAdd(IObject.class.getCanonicalName()));

        new AssertionChecker(Collections.singletonList(a1desc));
    }

    @Test
    public void Should_checkAssertions()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'type': 'atype1', 'name': 'Ass1'}".replace('\'', '"'));

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        checker.check(messageProcessorMock, null);
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenCannotReadValueFromEnvironment()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'type': 'atype1', 'name': 'Ass1'}".replace('\'', '"'));

        when(environmentMock.getValue(any())).thenThrow(ReadValueException.class);

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        checker.check(messageProcessorMock, null);
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenAssertionFails()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'type': 'atype1', 'name': 'Ass1'}".replace('\'', '"'));

        doThrow(AssertionFailureException.class).when(assertion1Mock).check(same(a1desc), any());

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        checker.check(messageProcessorMock, null);
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenExceptionOccurs()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'type': 'atype1', 'name': 'Ass1'}".replace('\'', '"'));

        doThrow(AssertionFailureException.class).when(assertion1Mock).check(same(a1desc), any());

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        checker.check(messageProcessorMock, new Exception());
    }

    @Test
    public void Should_createWrapperDescription()
            throws Exception {
        IObject a1desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'type': 'atype1', 'name': 'Ass1', 'value': 'message/x'}".replace('\'', '"'));

        doThrow(AssertionFailureException.class).when(assertion1Mock).check(same(a1desc), any());

        AssertionChecker checker = new AssertionChecker(Collections.singletonList(a1desc));

        assertEquals("{'wrapper':{'in_Ass1':'message/x'}}".replace('\'', '"'), checker.getSuccessfulReceiverArguments().serialize());
    }
}
