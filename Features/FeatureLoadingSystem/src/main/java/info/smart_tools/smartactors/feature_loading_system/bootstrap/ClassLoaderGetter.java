package info.smart_tools.smartactors.feature_loading_system.bootstrap;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.MethodBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassLoaderGetter {

    public ISmartactorsClassLoader getClassLoader(
            final IBootstrapItem<String> item
    ) throws NoSuchFieldException, IllegalAccessException {
        if (item instanceof MethodBootstrapItem) {
            Field field = item.getClass().getDeclaredField("method1");
            field.setAccessible(true);
            Method itemCore = (Method) field.get(item);
            return  (ISmartactorsClassLoader) itemCore.getDeclaringClass().getClassLoader();
        } else {
            Field field = item.getClass().getDeclaredField("item");
            field.setAccessible(true);
            Object itemCore = field.get(item);
            Field field2 = itemCore.getClass().getDeclaredField("process");
            field2.setAccessible(true);
            Object process = field2.get(itemCore);
            return (ISmartactorsClassLoader) process.getClass().getClassLoader();
        }
    }
}
