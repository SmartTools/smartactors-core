package info.smart_tools.smartactors.sandbox.server_with_configuration;

import info.smart_tools.smartactors.core.bootstrap.Bootstrap;
import info.smart_tools.smartactors.core.feature_manager.FeatureManager;
import info.smart_tools.smartactors.core.filesystem_tracker.FilesystemTracker;
import info.smart_tools.smartactors.core.filesystem_tracker.ListenerTask;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ifeature_manager.IFeature;
import info.smart_tools.smartactors.core.ifeature_manager.IFeatureManager;
import info.smart_tools.smartactors.core.ifilesystem_tracker.IFilesystemTracker;
import info.smart_tools.smartactors.core.ipath.IPath;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin_creator.IPluginCreator;
import info.smart_tools.smartactors.core.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.core.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.core.iserver.IServer;
import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import info.smart_tools.smartactors.core.path.Path;
import info.smart_tools.smartactors.core.plugin_creator.PluginCreator;
import info.smart_tools.smartactors.core.plugin_loader_from_jar.ExpansibleURLClassLoader;
import info.smart_tools.smartactors.core.plugin_loader_from_jar.PluginLoader;
import info.smart_tools.smartactors.core.plugin_loader_visitor_empty_implementation.PluginLoaderVisitor;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collection;

/**
 *
 */
public class ServerWithConfiguration implements IServer {

    public static void main(String[] args) throws Exception {
        ServerWithConfiguration server = new ServerWithConfiguration();
        server.initialize();
        server.start();
    }

    @Override
    public void initialize() throws ServerInitializeException {
        try {
            IBootstrap bootstrap = new Bootstrap();
            IPluginCreator creator = new PluginCreator();
            IPluginLoaderVisitor<String> visitor = new PluginLoaderVisitor<>();
            ExpansibleURLClassLoader urlClassLoader = new ExpansibleURLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
            IPluginLoader<Collection<IPath>> pluginLoader = new PluginLoader(
                    urlClassLoader,
                    (t) -> {
                        try {
                            IPlugin plugin = creator.create(t, bootstrap);
                            plugin.load();
                        } catch (Exception e) {
                            throw new RuntimeException("Could not create instance of IPlugin");
                        }
                    },
                    visitor
            );

            // FS listener creation
            // TODO: Get from configuration
            IPath coreJarsDir = new Path("plugins");
            Files.createDirectories(FileSystems.getDefault().getPath(coreJarsDir.getPath()));

            IFilesystemTracker jarFilesTracker = new FilesystemTracker(
                    (path) -> path.getPath().endsWith(".jar"),
                    ListenerTask::new);

            jarFilesTracker.start(coreJarsDir);
            jarFilesTracker.addErrorHandler((e) -> {
                System.out.println("Server initialization failed!");
                e.printStackTrace();
            });

            // FeatureManager & Feature creation
            IFeatureManager featureManager = new FeatureManager();


            IFeature coreFeature = featureManager.newFeature("smartactors.core", jarFilesTracker);
            coreFeature.whenPresent(files -> {
                try {
                    pluginLoader.loadPlugin(files);
                    bootstrap.start();
                } catch (Throwable e) {
                    throw new RuntimeException("Plugin loading failed.", e);
                }
            });

            String[] coreJars = {
                    "actor.response_sender-0.2.0-SNAPSHOT.jar",


                    "actors.sample_actor-0.2.0-SNAPSHOT.jar",
                    "plugin.sample_actor-0.2.0-SNAPSHOT.jar",

                    "core.chain_call_receiver-0.2.0-SNAPSHOT.jar",
                    "plugin.chain_call_receiver-0.2.0-SNAPSHOT.jar",
                    "plugin.chain_choice_strategy-0.2.0-SNAPSHOT.jar",

                    "core.actor_receiver-0.2.0-SNAPSHOT.jar",
                    "core.actor_receiver_creator-0.2.0-SNAPSHOT.jar",
                    "core.blocking_queue-0.2.0-SNAPSHOT.jar",
                    "core.bootstrap-0.2.0-SNAPSHOT.jar",
                    "core.bootstrap_item-0.2.0-SNAPSHOT.jar",
                    "core.chain_storage-0.2.0-SNAPSHOT.jar",
                    "core.channel_handler_netty-0.2.0-SNAPSHOT.jar",
                    "core.class_generator-0.2.0-SNAPSHOT.jar",
                    "core.client-0.2.0-SNAPSHOT.jar",
                    "core.completable_netty_future-0.2.0-SNAPSHOT.jar",
                    "core.configuration_manager-0.2.0-SNAPSHOT.jar",
                    "core.create_new_instance_strategy-0.2.0-SNAPSHOT.jar",
                    "core.db_storage-0.2.0-SNAPSHOT.jar",
                    "core.deserialize_strategy_post_json-0.2.0-SNAPSHOT.jar",
                    "core.ds_object-0.2.0-SNAPSHOT.jar",
                    "core.endpoint_channel_inbound_handler-0.2.0-SNAPSHOT.jar",
                    "core.endpoint_handler-0.2.0-SNAPSHOT.jar",
                    "core.environment_handler-0.2.0-SNAPSHOT.jar",
                    "core.feature_manager-0.2.0-SNAPSHOT.jar",
                    "core.field-0.2.0-SNAPSHOT.jar",
                    "core.field_name-0.2.0-SNAPSHOT.jar",
                    "core.filesystem_tracker-0.2.0-SNAPSHOT.jar",
                    "core.handler_routing_receiver-0.2.0-SNAPSHOT.jar",
                    "core.handler_routing_receiver_creator-0.2.0-SNAPSHOT.jar",
                    "core.http_endpoint-0.2.0-SNAPSHOT.jar",
                    "core.http_environment_extractor-0.2.0-SNAPSHOT.jar",
                    "core.https_endpoint-0.2.0-SNAPSHOT.jar",
                    "core.http_request_handler-0.2.0-SNAPSHOT.jar",
                    "core.http_response_sender-0.2.0-SNAPSHOT.jar",
                    "core.https_server-0.2.0-SNAPSHOT.jar",
                    "core.http_server-0.2.0-SNAPSHOT.jar",
                    "core.iaction-0.2.0-SNAPSHOT.jar",
                    "core.iasync_service-0.2.0-SNAPSHOT.jar",
                    "core.ibootstrap-0.2.0-SNAPSHOT.jar",
                    "core.ibootstrap_item-0.2.0-SNAPSHOT.jar",
                    "core.ichain_storage-0.2.0-SNAPSHOT.jar",
                    "core.ichannel_handler-0.2.0-SNAPSHOT.jar",
                    "core.iclass_generator-0.2.0-SNAPSHOT.jar",
                    "core.iconfiguration_manager-0.2.0-SNAPSHOT.jar",
                    "core.icookies_extractor-0.2.0-SNAPSHOT.jar",
                    "core.idatabase_task-0.2.0-SNAPSHOT.jar",
                    "core.ideserialize_strategy-0.2.0-SNAPSHOT.jar",
                    "core.ienvironment_handler-0.2.0-SNAPSHOT.jar",
                    "core.ienvironment_extractor-0.2.0-SNAPSHOT.jar",
                    "core.ifeature_manager-0.2.0-SNAPSHOT.jar",
                    "core.ifield-0.2.0-SNAPSHOT.jar",
                    "core.ifield_name-0.2.0-SNAPSHOT.jar",
                    "core.ifilesystem_tracker-0.2.0-SNAPSHOT.jar",
                    "core.iheaders_extractor-0.2.0-SNAPSHOT.jar",
                    "core.iioccontainer-0.2.0-SNAPSHOT.jar",
                    "core.ikey-0.2.0-SNAPSHOT.jar",
                    "core.imessage-0.2.0-SNAPSHOT.jar",
                    "core.imessage_mapper-0.2.0-SNAPSHOT.jar",
                    "core.immutable_receiver_chain-0.2.0-SNAPSHOT.jar",
                    "core.invalid_argument_exception-0.2.0-SNAPSHOT.jar",
                    "core.invalid_state_exception-0.2.0-SNAPSHOT.jar",
                    "core.iobject-0.2.0-SNAPSHOT.jar",
                    "core.configuration_object-0.2.0-SNAPSHOT.jar",
                    "core.iobject_wrapper-0.2.0-SNAPSHOT.jar",
                    "core.ioc-0.2.0-SNAPSHOT.jar",
                    "core.ioc_container-0.2.0-SNAPSHOT.jar",
                    "core.ipath-0.2.0-SNAPSHOT.jar",
                    "core.iplugin-0.2.0-SNAPSHOT.jar",
                    "core.iplugin_creator-0.2.0-SNAPSHOT.jar",
                    "core.iplugin_loader-0.2.0-SNAPSHOT.jar",
                    "core.iplugin_loader_visitor-0.2.0-SNAPSHOT.jar",
                    "core.ipool-0.2.0-SNAPSHOT.jar",
                    "core.iqueue-0.2.0-SNAPSHOT.jar",
                    "core.ireceiver_generator-0.2.0-SNAPSHOT.jar",
                    "core.iresolve_dependency_strategy-0.2.0-SNAPSHOT.jar",
                    "core.iresource_source-0.2.0-SNAPSHOT.jar",
                    "core.iresponse-0.2.0-SNAPSHOT.jar",
                    "core.iresponse_body_strategy-0.2.0-SNAPSHOT.jar",
                    "core.iresponse-sender-0.2.0-SNAPSHOT.jar",
                    "core.iresponse_status_extractor-0.2.0-SNAPSHOT.jar",
                    "core.irouted_object_creator-0.2.0-SNAPSHOT.jar",
                    "core.irouter-0.2.0-SNAPSHOT.jar",
                    "core.iscope-0.2.0-SNAPSHOT.jar",
                    "core.iscope_provider_container-0.2.0-SNAPSHOT.jar",
                    "core.iserver-0.2.0-SNAPSHOT.jar",
                    "core.istrategy_container-0.2.0-SNAPSHOT.jar",
                    "core.itask-0.2.0-SNAPSHOT.jar",
                    "core.itask_dispatcher-0.2.0-SNAPSHOT.jar",
                    "core.ithread_pool-0.2.0-SNAPSHOT.jar",
                    "core.iwrapper_generator-0.2.0-SNAPSHOT.jar",
                    "core.map_router-0.2.0-SNAPSHOT.jar",
                    "core.message_processing-0.2.0-SNAPSHOT.jar",
                    "core.message_processing_sequence-0.2.0-SNAPSHOT.jar",
                    "core.message_processor-0.2.0-SNAPSHOT.jar",
                    "core.message_to_bytes_mapper-0.2.0-SNAPSHOT.jar",
                    "core.named_keys_storage-0.2.0-SNAPSHOT.jar",
                    "core.netty_client-0.2.0-SNAPSHOT.jar",
                    "core.netty_server-0.2.0-SNAPSHOT.jar",
                    "core.path-0.2.0-SNAPSHOT.jar",
                    "core.plugin_creator-0.2.0-SNAPSHOT.jar",
                    "core.plugin_loader_from_jar-0.2.0-SNAPSHOT.jar",
                    "core.plugin_loader_visitor_empty_implementation-0.2.0-SNAPSHOT.jar",
                    "core.pool-0.2.0-SNAPSHOT.jar",
                    "core.pool_guard-0.2.0-SNAPSHOT.jar",
                    "core.receiver_generator-0.2.0-SNAPSHOT.jar",
                    "core.recursive_scope-0.2.0-SNAPSHOT.jar",
                    "core.resolve_by_name_ioc_strategy-0.2.0-SNAPSHOT.jar",
                    "core.resolve_by_name_ioc_with_lambda_strategy-0.2.0-SNAPSHOT.jar",
                    "core.response-0.2.0-SNAPSHOT.jar",
                    "core.response_content_json_strategy-0.2.0-SNAPSHOT.jar",
                    "core.scope_provider-0.2.0-SNAPSHOT.jar",
                    "core.scope_provider_container-0.2.0-SNAPSHOT.jar",
                    "core.singleton_strategy-0.2.0-SNAPSHOT.jar",
                    "core.sql_commons-0.2.0-SNAPSHOT.jar",
                    "core.standard_config_sections-0.2.0-SNAPSHOT.jar",
                    "core.strategy_container-0.2.0-SNAPSHOT.jar",
                    "core.string_ioc_key-0.2.0-SNAPSHOT.jar",
                    "core.task_dispatcher-0.2.0-SNAPSHOT.jar",
                    "core.tcp_server-0.2.0-SNAPSHOT.jar",
                    "core.thread_pool-0.2.0-SNAPSHOT.jar",
                    "core.wds_object-0.2.0-SNAPSHOT.jar",
                    "core.wrapper_generator-0.2.0-SNAPSHOT.jar",
                    "guava-19.0.jar",
                    "hamcrest-core-1.3.jar",
                    "InMemoryJavaCompiler-1.2.jar",
                    "jackson-annotations-2.5.2.jar",
                    "jackson-core-2.5.2.jar",
                    "jackson-databind-2.5.2.jar",
                    "junit-4.12.jar",
                    "mockito-all-2.0.2-beta.jar",
                    "netty-all-4.1.2.Final.jar",
                    "plugin.actor_receiver_creator-0.2.0-SNAPSHOT.jar",
                    "plugin.configuration_manager-0.2.0-SNAPSHOT.jar",
                    "plugin.dsobject-0.2.0-SNAPSHOT.jar",
                    "plugin.configuration_object-0.2.0-SNAPSHOT.jar",
                    "plugin.field-0.2.0-SNAPSHOT.jar",
                    "plugin.fieldname-0.2.0-SNAPSHOT.jar",
                    "plugin.handler_routing_receiver_creator-0.2.0-SNAPSHOT.jar",
                    "plugin.https_endpoint-0.2.0-SNAPSHOT.jar",
                    "plugin.ifieldname-0.2.0-SNAPSHOT.jar",
                    "plugin.fieldname-0.2.0-SNAPSHOT.jar",
                    "plugin.immutable_receiver_chain-0.2.0-SNAPSHOT.jar",
                    "plugin.ioc_keys-0.2.0-SNAPSHOT.jar",
                    "plugin.load_scope_provider-0.2.0-SNAPSHOT.jar",
                    "plugin.map_router-0.2.0-SNAPSHOT.jar",
                    "plugin.message_processor_and_sequence-0.2.0-SNAPSHOT.jar",
                    "plugin.messaging_identifiers-0.2.0-SNAPSHOT.jar",
                    "plugin.read_config_file-0.2.0-SNAPSHOT.jar",
                    "plugin.receiver_chains_storage-0.2.0-SNAPSHOT.jar",
                    "plugin.receiver_generator-0.2.0-SNAPSHOT.jar",
                    "plugin.response-0.2.0-SNAPSHOT.jar",
                    "plugin.response_content_json_strategy-0.2.0-SNAPSHOT.jar",
                    "plugin.response_sender_actor-0.2.0-SNAPSHOT.jar",
                    "plugin.scoped_ioc-0.2.0-SNAPSHOT.jar",
                    "plugin.scope_provider-0.2.0-SNAPSHOT.jar",
                    "plugin.standard_object_creators-0.2.0-SNAPSHOT.jar",
                    //"plugin.http_endpoint-0.2.0-SNAPSHOT.jar",
                    "plugin.wds_object-0.2.0-SNAPSHOT.jar",
                    "plugin.wrapper_generator-0.2.0-SNAPSHOT.jar",
                    "plugin.starter-0.2.0-SNAPSHOT.jar",
                    "strategy.apply_function_to_arguments-0.2.0-SNAPSHOT.jar",
                    "strategy.cookies_setter-0.2.0-SNAPSHOT.jar",
                    "strategy.http_headers_setter-0.2.0-SNAPSHOT.jar",
                    "strategy.response_status_extractor-0.2.0-SNAPSHOT.jar",
                    "core.ssl_engine_provider-0.2.0-SNAPSHOT.jar",
                    "core.issl_engine_provider-0.2.0-SNAPSHOT.jar"
            };

            for (String jarName : coreJars) {
                coreFeature.requireFile(jarName);
            }

            coreFeature.listen();

            while (!Thread.interrupted()) {
                Thread.sleep(1L);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ServerInitializeException(e);
        }
    }

    @Override
    public void start() throws ServerExecutionException {

    }
}
