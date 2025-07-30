package info.smart_tools.smartactors.http_endpoint_plugins.get_query_parameter_rule_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.http_endpoint.strategy.get_query_parameter.GetQueryParameterRule;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin for register {@link GetQueryParameterRule}
 */
public class GetQueryParameterRulePlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public GetQueryParameterRulePlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("GetQueryParameterFromRequestRulePlugin");

            item
//                    .after("IOC")
//                    .after("wds_object")
//                    .before("starter")
                    .process(() -> {
                        try {
                            //call IOC.resolve for put GetQueryParameterRule into cache of ResolveByNameDependency strategy
                            IOC.resolve(
                                    Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                                    "getQueryParameterFromRequestRule",
                                    new GetQueryParameterRule()
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException(
                                    "GetQueryParameterFromRequestRule plugin can't load: can't get GetQueryParameterFromRequestRule key", e
                            );
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load GetQueryParameterFromRequestRule plugin", e);
        }
    }
}
