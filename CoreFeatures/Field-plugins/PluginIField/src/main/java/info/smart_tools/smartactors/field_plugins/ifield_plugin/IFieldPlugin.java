package info.smart_tools.smartactors.field_plugins.ifield_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.field.field.Field;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;

/**
 * Plugin for registration of IOC strategy for Field
 */
public class IFieldPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public IFieldPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {

        try {
            BootstrapItem item = new BootstrapItem("IFieldPlugin");
            item
                .after("IOC")
                .process(() -> {
                    try {
                        IKey fieldKey = Keys.getKeyByName(IField.class.getCanonicalName());
                        IOC.register(
                                fieldKey,
                                new ResolveByNameIocStrategy(
                                        (args) -> {
                                            String fieldName = String.valueOf(args[0]);
                                            try {
                                                return new Field(
                                                        IOC.resolve(
                                                                Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                                                fieldName
                                                        )
                                                );
                                            } catch (InvalidArgumentException | ResolutionException e) {
                                                throw new RuntimeException("Can't resolve IField: ", e);
                                            }
                                        }
                                )
                        );
                    } catch (ResolutionException e) {
                        throw new ActionExecutionException("IField plugin can't load: can't get IField key", e);
                    } catch (InvalidArgumentException e) {
                        throw new ActionExecutionException("IField plugin can't load: can't create strategy", e);
                    } catch (RegistrationException e) {
                        throw new ActionExecutionException("IField plugin can't load: can't register new strategy", e);
                    }
                })
                .revertProcess(() -> {
                    String[] keyNames = { IField.class.getCanonicalName() };
                    Keys.unregisterByNames(keyNames);
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load IField plugin", e);
        }
    }
}
