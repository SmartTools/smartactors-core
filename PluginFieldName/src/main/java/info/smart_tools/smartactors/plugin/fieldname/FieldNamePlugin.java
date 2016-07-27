package info.smart_tools.smartactors.plugin.fieldname;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
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
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;

/**
 * Plugin registers strategy for resolving field name by field name class
 */
public class FieldNamePlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public FieldNamePlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {

            IBootstrapItem<String> item = new BootstrapItem("FieldNamePlugin");

            item
                    .after("IOC")
                    .process(() -> {
                        try {
                            IKey iFieldNameKey = Keys.getOrAdd(FieldName.class.toString());
                            IOC.register(iFieldNameKey,
                                    new ResolveByNameIocStrategy(
                                            (args) -> {
                                                try {
                                                    String nameOfFieldName = (String) args[0];
                                                    return new FieldName(nameOfFieldName);
                                                } catch (ClassCastException e) {
                                                    throw new RuntimeException("Can't cast object to String: " + args[0],
                                                            e);
                                                } catch (ArrayIndexOutOfBoundsException e) {
                                                    throw new RuntimeException(
                                                            "Can't get args: args must contain one or more elements " +
                                                                    "and first element must be String",
                                                            e);
                                                } catch (InvalidArgumentException e) {
                                                    throw new RuntimeException(
                                                            "Can't create new field name with this name: " + args[0],
                                                            e);
                                                }
                                            }
                                    )
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("Can't get IFieldName key");
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("Can't get create strategy");
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("Can't get register new strategy");
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't get BootstrapItem from one of reason", e);
        }
    }

}
