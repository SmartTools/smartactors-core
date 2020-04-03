package info.smart_tools.smartactors.testing.test_assertions;

import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.interfaces.iassertion.IAssertion;

/**
 * Base class for tests for {@link IAssertion} implementations.
 */
public class AssertionTestBase extends PluginsLoadingTestBase {
    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    protected void apply(Class<? extends IAssertion> clz, IObject desc, Object value)
            throws Exception {
        (clz.newInstance()).check(desc, value);
    }

    protected void apply(Class<? extends IAssertion> clz, String desc, Object value)
            throws Exception {
        apply(clz, IOC.<IObject>resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), desc), value);
    }
}
