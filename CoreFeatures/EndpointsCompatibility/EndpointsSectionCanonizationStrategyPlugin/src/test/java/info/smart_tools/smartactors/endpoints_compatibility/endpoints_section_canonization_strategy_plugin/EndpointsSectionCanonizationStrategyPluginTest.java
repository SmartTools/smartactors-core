package info.smart_tools.smartactors.endpoints_compatibility.endpoints_section_canonization_strategy_plugin;

import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject_extension_plugins.configuration_object_plugin.InitializeConfigurationObjectStrategies;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EndpointsSectionCanonizationStrategyPluginTest extends TrivialPluginsLoadingTestBase {
    private final String validMixedStyleConf = ("{'endpoints':[{" +
            "'type': 'http'," +
            "'name': 'anHttpEndpoint'," +
            "'port': 8080," +
            "'maxContentLength': 4325," +
            "'startChain': 'theStartChain'," +
            "'stackDepth': 8" +
            "}, {" +
            "'type': 'https'," +
            "'name': 'anHttpEndpoint'," +
            "'port': 8087," +
            "'maxContentLength': 4325," +
            "'startChain': 'theStartChain'," +
            "'stackDepth': 8," +
            "'certPath': '/etc/server.crt'," +
            "'certPass': '/etc/server.key'" +
            "}, {" +
            "'profile': 'my-profile'," +
            "'skeleton': 'my-skeleton'" +
            "}]}"
            ).replace('\'','"');

    private final String invalidOldStyleConf = ("{'endpoints':[{" +
            "'type':'some-unknown-type'," +
            "'name':'aMysteriousEndpoint'" +
            "}]}").replace('\'','"');

    @Override
    protected void loadPlugins() throws Exception {
        super.loadPlugins();
        load(InitializeConfigurationObjectStrategies.class);
        load(EndpointsSectionCanonizationStrategyPlugin.class);
    }

    @Test
    public void Should_canonizeEndpointConfigs() throws Exception {
        IObject conf = IOC.resolve(Keys.getOrAdd("configuration object"), validMixedStyleConf);

        assertEquals(":8080", ((IObject) ((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(0)).getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "address")));
        assertEquals(null, ((IObject) ((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(2)).getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "address")));
        assertEquals("netty/server/http", ((IObject) ((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(0)).getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "profile")));
        assertEquals("netty/server/https", ((IObject) ((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(1)).getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "profile")));
        assertEquals("my-profile", ((IObject) ((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(2)).getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "profile")));
        assertEquals("netty/server/tcp/single-port", ((IObject) ((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(0)).getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "skeleton")));
        assertEquals("netty/server/tcp/single-port", ((IObject) ((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(1)).getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "skeleton")));
        assertEquals("my-skeleton", ((IObject) ((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(2)).getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "skeleton")));
    }

    @Test(expected = ReadValueException.class)
    public void Should_failWhenInvalidTypeGiven() throws Exception {
        IObject conf = IOC.resolve(Keys.getOrAdd("configuration object"), invalidOldStyleConf);

        conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"));
    }
}
