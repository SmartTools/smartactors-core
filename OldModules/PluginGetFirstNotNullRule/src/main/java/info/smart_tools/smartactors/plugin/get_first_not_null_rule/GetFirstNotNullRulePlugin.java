package info.smart_tools.smartactors.plugin.get_first_not_null_rule;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.transformation_rules.get_first_not_null.GetFirstNotNullRule;

public class GetFirstNotNullRulePlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public GetFirstNotNullRulePlugin(IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("GetFirstNotNullRulePlugin");

            item
                    .after("IOC")
                    .after("wds_object")
                    .before("starter")
                    .process(() -> {
                        try {
                            IOC.resolve(
                                    Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                                    "getFirstNotNullRule",
                                    new GetFirstNotNullRule()
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException(
                                    "GetFirstNotNullRule plugin can't load: can't get GetFirstNotNullRule key", e
                            );
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't create BootstrapItem", e);
        }
    }
}
