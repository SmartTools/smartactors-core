package info.smart_tools.smartactors.sandbox.server_with_configuration;

import info.smart_tools.smartactors.core.bootstrap.Bootstrap;
import info.smart_tools.smartactors.core.dependency_resolving_feature_manager.DependencyResolvingFeatureManager;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ifeature_manager.IFeature;
import info.smart_tools.smartactors.core.ifeature_manager.IFeatureManager;
import info.smart_tools.smartactors.core.ipath.IPath;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin_creator.IPluginCreator;
import info.smart_tools.smartactors.core.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.core.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.core.iserver.IServer;
import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import info.smart_tools.smartactors.core.plugin_creator.PluginCreator;
import info.smart_tools.smartactors.core.plugin_loader_from_jar.ExpansibleURLClassLoader;
import info.smart_tools.smartactors.core.plugin_loader_from_jar.PluginLoader;
import info.smart_tools.smartactors.core.plugin_loader_visitor_empty_implementation.PluginLoaderVisitor;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 */
public class ServerWithConfiguration implements IServer {

    public static void main(final String[] args) throws Exception {
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

            // FeatureManager & Feature creation
            IFeatureManager featureManager = new DependencyResolvingFeatureManager(
                    System.getProperty("user.home") + "/smartactors-plugins-repo",
                    new HashMap<String, String>() {{
                        put("local_repo", "file://" + System.getProperty("user.home") + "/.m2/repository/");
                    }}
            );

            IFeature coreFeature = featureManager.newFeature("smartactors.core", null);
            coreFeature.whenPresent(files -> {
                try {
                    pluginLoader.loadPlugin(files);
                    bootstrap.start();
                } catch (Throwable e) {
                    throw new RuntimeException("Plugin loading failed.", e);
                }
            });

            String[] coreArtifacts = {
                    "info.smart_tools.smartactors:core.standard_config_sections:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.starter:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.actor_receiver_creator:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.configuration_manager:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.dsobject:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.configuration_object:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.field:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.fieldname:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.handler_routing_receiver_creator:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.http_endpoint:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.ifieldname:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.fieldname:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.immutable_receiver_chain:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.ioc_keys:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.load_scope_provider:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.map_router:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.message_processor_and_sequence:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.messaging_identifiers:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.read_config_file:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.receiver_chains_storage:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.receiver_generator:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.response:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.response_content_json_strategy:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.response_sender_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.sample_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.scoped_ioc:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.scope_provider:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.standard_object_creators:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.http_endpoint:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.wds_object:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.wrapper_generator:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.chain_call_receiver:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.chain_choice_strategy:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.resolve_standard_types_strategies:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.resolve_iobject_strategies:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.get_form_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.postgres_db_tasks:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.postgres_connection_pool:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.cached_collection:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.collection_name:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.create_async_operation_plugin:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.get_async_operation:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.close_async_operation_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.async_ops_collection:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.validation_form_actor:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.create_session_plugin:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.save_session_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.authentication_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.get_header_from_request_rule:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.get_query_parameter_rule:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.get_cookie_from_request:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.password_encoder:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.user_auth_by_login_actor:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.check_validity_async_operation_plugin:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.user_auth_by_login_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.change_password_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.mailing_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.prepare_mail_confirm_registration_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.check_user_is_new_actor:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.change_password_actor:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.connection_options:0.2.0-SNAPSHOT",

                    "info.smart_tools.smartactors:plugin.create_user:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.get_first_not_null_rule:0.2.0-SNAPSHOT",
                    "info.smart_tools.smartactors:plugin.starter:0.2.0-SNAPSHOT"
            };

            for (String artifact : coreArtifacts) {
                coreFeature.requireFile(artifact);
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
