package info.smart_tools.smartactors.endpoints_compatibility.endpoints_section_canonization_strategy_plugin;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;

public class EndpointsSectionCanonizationStrategyPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointsSectionCanonizationStrategyPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("endpoints_section_canonization_strategy")
    public void registerCanonizationStrategy() throws Exception {
        IAdditionDependencyStrategy ads = IOC.resolve(Keys.getOrAdd("expandable_strategy#resolve key for configuration object"));

        ads.register("endpoints", new ApplyFunctionToArgumentsStrategy(args -> {
            Object value = args[1];

            if (value instanceof List) {
                for (Object endpointConfUc : (List) value) {
                    IObject endpointConf = (IObject) endpointConfUc;

                    try {
                        canonizeSingleEndpoint(endpointConf);
                    } catch (Exception e) {
                        throw new FunctionExecutionException(e);
                    }
                }
            }

            return value;
        }));
    }

    private void canonizeSingleEndpoint(final IObject conf) throws Exception {
        IFieldName portFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "port");
        IFieldName addressFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "address");
        IFieldName maxContentLengthFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "maxContentLength");
        IFieldName maxAggregatedContentLengthFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "maxAggregatedContentLength");
        IFieldName startChainFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "startChain");
        IFieldName mainInboundChainFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "mainInboundChain");
        IFieldName webSocketInboundChainFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "webSocketInboundChain");
        IFieldName certPathFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "certPath");
        IFieldName certPassFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "certPass");
        IFieldName serverCertificateFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "serverCertificate");
        IFieldName serverCertificateKeyFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "serverCertificateKey");
        IFieldName stackDepthFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "stackDepth");
        IFieldName mainInboundMessageChainStackDepthFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "mainInboundMessageChainStackDepth");
        IFieldName typeFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "type");
        IFieldName profileFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "profile");
        IFieldName skeletonFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "skeleton");
        IFieldName parentEventLoopGroupFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "parentEventLoopGroup");
        IFieldName childEventLoopGroupFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "childEventLoopGroup");
        IFieldName transportFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "transport");
        IFieldName connectPipelineFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectPipeline");
        IFieldName nameFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "name");

        if ((conf.getValue(profileFN) != null) && (conf.getValue(skeletonFN) != null) || (conf.getValue(typeFN) == null)) {
            return;
        }

        // Old-style endpoint features are removed and so will not be changed so it should be ok to hardcode all endpoint types.
        if ("http".equals(conf.getValue(typeFN))) {
            conf.setValue(profileFN, "netty/server/http");
            conf.setValue(skeletonFN, "netty/server/tcp/single-port");
        } else if ("https".equals(conf.getValue(typeFN))) {
            conf.setValue(profileFN, "netty/server/https");
            conf.setValue(skeletonFN, "netty/server/tcp/single-port");
        } else {
            throw new Exception("Unknown old-style endpoint type '" + conf.getValue(typeFN) + "' at endpoint '" + conf.getValue(nameFN) + "'.");
        }

        conf.setValue(addressFN, ":" + conf.getValue(portFN));

        conf.setValue(parentEventLoopGroupFN, "defaultServerParent");
        conf.setValue(childEventLoopGroupFN, "defaultServerChild");

        conf.setValue(transportFN, "prefer-native");
        conf.setValue(connectPipelineFN, "accept-client");

        conf.setValue(maxAggregatedContentLengthFN, conf.getValue(maxContentLengthFN));
        conf.setValue(mainInboundChainFN, conf.getValue(startChainFN));
        conf.setValue(serverCertificateFN, conf.getValue(certPathFN));
        conf.setValue(serverCertificateKeyFN, conf.getValue(certPassFN));

        conf.setValue(mainInboundMessageChainStackDepthFN, conf.getValue(stackDepthFN));

        conf.setValue(webSocketInboundChainFN, "null");
    }
}
