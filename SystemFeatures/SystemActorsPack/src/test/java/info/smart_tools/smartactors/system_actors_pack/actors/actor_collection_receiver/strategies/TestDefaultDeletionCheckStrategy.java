package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.strategies;

import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces.IChildDeletionCheckStrategy;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDefaultDeletionCheckStrategy extends PluginsLoadingTestBase {

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Test
    public void Should_checkDeletionFlag()
        throws Exception {
        IChildDeletionCheckStrategy strategy = new DefaultDeletionCheckStrategy();

        IObject ctx = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));
        IObject env = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
            "{'context':{'deleteChild':true}}".replace('\'','"'));

        assertTrue(strategy.checkDelete(ctx, env));

        env = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
            "{'context':{'deleteChild':false}}".replace('\'','"'));

        assertFalse(strategy.checkDelete(ctx, env));

        env = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
            "{'context':{}}".replace('\'','"'));

        assertFalse(strategy.checkDelete(ctx, env));
    }
}
