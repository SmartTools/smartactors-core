package info.smart_tools.smartactors.launcher.plugin;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.launcher.feature.Feature;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyLoader;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginCreator;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginLoader;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PluginLoaderTest {

    private IPluginLoader<List<IFeature>> pluginLoader;

    private IDependencyLoader dependencyLoader;
    private IBootstrap bootstrap;
    private IPluginCreator pluginCreator;

    @Before
    public void init() throws Exception {
        System.setProperty("launcher.debug", String.valueOf(true));

        this.dependencyLoader = mock(IDependencyLoader.class);
        this.bootstrap = mock(IBootstrap.class);
        this.pluginCreator = mock(IPluginCreator.class);

        this.pluginLoader = new PluginLoader(dependencyLoader, bootstrap, pluginCreator);
    }

    @Test
    public void testLoadFeatures() throws Exception {
        ISmartactorsClassLoader classLoader = mock(ISmartactorsClassLoader.class);
        IModule module = mock(IModule.class);
        when(module.getClassLoader()).thenReturn(classLoader);
        ModuleManager.setCurrentModule(module);

        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IFeature feature = new Feature(
                UUID.randomUUID(),
                pathToFeature,
                "info.smart_tools.smartactors:simple-feature:0.7.0",
                new ArrayList<>(),
                Arrays.asList("info.smart_tools.smartactors:simple-feature-plugin:0.7.0")
        );
        IFeature plugin = new Feature(
                UUID.randomUUID(),
                pathToPlugin,
                "info.smart_tools.smartactors:simple-feature-plugin:0.7.0",
                Arrays.asList("info.smart_tools.smartactors:simple-feature:0.7.0"),
                new ArrayList<>()
        );
        List<IFeature> features = Arrays.asList(feature, plugin);

        pluginLoader.loadPlugins(features);
    }
}
