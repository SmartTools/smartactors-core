package info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * Bootstrap item created from a plugin method.
 */
public class MethodBootstrapItem extends BootstrapItem {

    //TODO: why the fuck we need it
    private final Method method1;

    /**
     * The constructor.
     *
     * @param plugin    the plugin instance
     * @param method1   the method annotated with {@link BootstrapPlugin.Item}
     * @param method2   the method annotated with {@link BootstrapPlugin.ItemRevert}
     * @throws InvalidArgumentException if item name is {@code null}
     */
    MethodBootstrapItem(final IPlugin plugin, final Method method1, final Method method2)
            throws InvalidArgumentException {
        super(method1.getAnnotation(BootstrapPlugin.Item.class).value());

        if (method1.getParameterCount() != 0) {
            throw new InvalidArgumentException(
                    MessageFormat.format(
                            "Bootstrap item body method should have no parameters but method {0} of {1} (item ''{2}'') has some.",
                            method1.getName(),
                            method1.getDeclaringClass().getName(),
                            method1.getAnnotation(BootstrapPlugin.Item.class).value()));
        }

        this.method1 = method1;
        BootstrapPlugin.After after = method1.getAnnotation(BootstrapPlugin.After.class);

        if (null != after) {
            for (String name : after.value()) {
                this.after(name);
            }
        }

        BootstrapPlugin.Before before = method1.getAnnotation(BootstrapPlugin.Before.class);

        if (null != before) {
            for (String name : before.value()) {
                this.before(name);
            }
        }

        this.process(() -> {
            try {
                method1.invoke(plugin);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new ActionExecutionException(e);
            }
        });
        this.revertProcess(() -> {
            try {
                if (null != method2 ) {
                    method2.invoke(plugin);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new ActionExecutionException(e);
            }
        });
    }
}
