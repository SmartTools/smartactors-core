package info.smart_tools.smartactors.plugin.get_header_from_request_rule;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.transformation_rules.get_header_from_request.GetHeaderFromRequestRule;

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
                    .after("IOC")
                    .process(() -> {
                        try {
                            IKey ruleKey = Keys.getOrAdd(GetHeaderFromRequestRule.class.toString());
                            IOC.register(ruleKey,
                                    new CreateNewInstanceStrategy(
                                            (args) -> new GetHeaderFromRequestRule()
                                    )
                            );
                        } catch (InvalidArgumentException | ResolutionException | RegistrationException e) {
                            throw new RuntimeException("Failed to register new strategy");
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load GetHeaderFromRequestRule plugin", e);
        }
    }
}
