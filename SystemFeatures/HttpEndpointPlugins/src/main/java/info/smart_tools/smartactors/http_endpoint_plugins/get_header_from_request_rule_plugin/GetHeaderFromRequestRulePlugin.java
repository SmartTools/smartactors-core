package info.smart_tools.smartactors.http_endpoint_plugins.get_header_from_request_rule_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.http_endpoint.strategy.get_header_from_request.GetHeaderFromRequestRule;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin for register GetHeaderFromRequestRule in IOC
 */
public class GetHeaderFromRequestRulePlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public GetHeaderFromRequestRulePlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("GetHeaderFromRequestRulePlugin");

            item
//                .after("IOC")
//                .after("wds_object")
//                .before("starter")
                    .process(() -> {
                        try {
                            //call IOC.resolve for put GetHeaderFromRequestRule into cache of ResolveByNameDependency strategy
                            IOC.resolve(
                                Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                                "getHeaderFromRequestRule",
                                new GetHeaderFromRequestRule()
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException(
                                "GetHeaderFromRequestRule plugin can't load: can't get GetHeaderFromRequestRule key", e
                            );
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load GetHeaderFromRequestRule plugin", e);
        }
    }
}
