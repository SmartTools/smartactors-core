package info.smart_tools.smartactors.http_endpoint_plugins.get_cookie_rule_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.http_endpoint.strategy.get_cookie_from_request.GetCookieFromRequestRule;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin for register GetCookieFromRequestRule in IOC
 */
public class GetCookieFromRequestRulePlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public GetCookieFromRequestRulePlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("GetCookieFromRequestRulePlugin");

            item
                    .after("core")
//                    .after("IOC")
//                    .after("wds_object")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IOC.resolve(
                                    Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                                    "getCookieFromRequestRule",
                                    new GetCookieFromRequestRule()
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException(
                                    "GetCookieFromRequestRule plugin can't load: can't get GetCookieFromRequestRule key", e
                            );
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load GetCookieFromRequestRule plugin", e);
        }
    }
}
