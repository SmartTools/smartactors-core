package info.smart_tools.smartactors.endpoint_components_generic_plugins.mime_codec_tables_plugin;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.impl.json.JsonBlockDecoder;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.impl.json.JsonBlockEncoder;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MimeCodecTablesPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public MimeCodecTablesPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("mime_codec_table_strategies")
    @After({
            "global_message_handler_tables_storage",
    })
    public void registerStrategies() throws Exception {
        IAdditionDependencyStrategy handlerTableStorage
                = IOC.resolve(Keys.getOrAdd("expandable_strategy#message handler table"));

        handlerTableStorage.register("default block decoders by mime type", new SingletonStrategy(new ConcurrentHashMap()));
        handlerTableStorage.register("default block encoders by mime type", new SingletonStrategy(new ConcurrentHashMap()));
        handlerTableStorage.register("default stream decoders by mime type", new SingletonStrategy(new ConcurrentHashMap()));
        handlerTableStorage.register("default stream encoders by mime type", new SingletonStrategy(new ConcurrentHashMap()));
    }

    @Item("default_mime_codecs")
    @After({
            "mime_codec_table_strategies"
    })
    public void registerDefaultCodecHandlers() throws Exception {
        Map<String, Object> blockDecoderTable = IOC.resolve(
                Keys.getOrAdd("message handler table"),
                "default block decoders by mime type");
        Map<String, Object> blockEncoderTable = IOC.resolve(
                Keys.getOrAdd("message handler table"),
                "default block encoders by mime type");

        blockDecoderTable.put("application/json", new JsonBlockDecoder());

        blockEncoderTable.put("application/json", new JsonBlockEncoder());
    }
}
