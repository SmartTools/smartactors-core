package info.smart_tools.smartactors.launcher;

import info.smart_tools.smartactors.class_management.class_loaders.SmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.launcher.interfaces.ILauncher;

import java.io.File;

public class Starter {
    public static void main(final String[] args)
            throws Exception {
        Thread.sleep(5000);
        ISmartactorsClassLoader classLoader = SmartactorsClassLoader.newInstance(
                "info.smart_tools:smartactors", "0.6.0"
        );
        File f = new File("min-core.jar");
        classLoader.addURL(f.toURL());
        Class<?> clazz = classLoader.loadClass("info.smart_tools.smartactors.launcher.Launcher");
        ILauncher launcher = ((ILauncher)clazz.newInstance());
        launcher.initialize();
        launcher.start();
        Thread.sleep(5000);
    }
}
