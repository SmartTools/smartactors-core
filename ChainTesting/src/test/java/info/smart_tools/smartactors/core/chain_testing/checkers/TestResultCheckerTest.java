package info.smart_tools.smartactors.core.chain_testing.checkers;

import info.smart_tools.smartactors.core.chain_testing.exceptions.InvalidTestDescriptionException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.plugin.dsobject.PluginDSObject;
import info.smart_tools.smartactors.plugin.ifieldname.IFieldNamePlugin;
import info.smart_tools.smartactors.plugin.ioc_keys.PluginIOCKeys;
import info.smart_tools.smartactors.plugin.scope_provider.PluginScopeProvider;
import info.smart_tools.smartactors.plugin.scoped_ioc.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link TestResultChecker}.
 */
public class TestResultCheckerTest extends PluginsLoadingTestBase {

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Test(expected = InvalidTestDescriptionException.class)
    public void Should_throwWhenDescriptionContainsBothSections()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'assert':[], 'intercept':{}}".replace('\'','"'));
        TestResultChecker.createChecker(desc);
    }

    @Test(expected = InvalidTestDescriptionException.class)
    public void Should_throwWhenDescriptionContainsNoneOfSections()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{}");
        TestResultChecker.createChecker(desc);
    }

    @Test(expected = TestStartupException.class)
    public void Should_throwWhenCannotResolveDependency()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'assert':[]}".replace('\'','"'));
        IOC.remove(Keys.getOrAdd(IFieldName.class.getCanonicalName()));
        assertTrue(TestResultChecker.createChecker(desc) instanceof AssertionChecker);
    }

    @Test
    public void Should_createAssertionChecker()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'assert':[]}".replace('\'','"'));
        assertTrue(TestResultChecker.createChecker(desc) instanceof AssertionChecker);
    }

    @Test
    public void Should_createExceptionInterceptingChecker()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'intercept':{'class':'java.lang.NullPointerException'}}".replace('\'','"'));
        IOC.register(Keys.getOrAdd("receiver_id_from_iobject"), new SingletonStrategy(mock(Object.class)));
        IOC.register(Keys.getOrAdd(IRouter.class.getCanonicalName()), new SingletonStrategy(mock(IRouter.class)));
        assertTrue(TestResultChecker.createChecker(desc) instanceof ExceptionInterceptor);
    }
}