package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyReplacer;
import info.smart_tools.smartactors.launcher.interfaces.core.IServerStarter;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.ServerStarterException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap.ProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap.RevertProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureSortingException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureReader;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureSorting;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginLoader;
import info.smart_tools.smartactors.launcher.logger.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore("missing files for this test")
public class ServerStarterTest {

    private IBootstrap bootstrap;
    private IFeatureReader featureReader;
    private IDependencyReplacer dependencyReplacer;
    private IFeatureSorting featureSorting;
    private IPluginLoader<List<IFeature>> pluginLoader;

    private IServerStarter serverStarter;

    @Before
    public void init() {
        ILogger logger = new Logger();

        this.bootstrap = mock(IBootstrap.class);
        this.featureReader = mock(IFeatureReader.class);
        this.dependencyReplacer = mock(IDependencyReplacer.class);
        this.featureSorting = mock(IFeatureSorting.class);
        this.pluginLoader = mock(IPluginLoader.class);

        this.serverStarter = new ServerStarter(logger, bootstrap, featureReader, dependencyReplacer, featureSorting, pluginLoader);
    }

    @Test
    public void initServerFeatures() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);

        ISmartactorsClassLoader smartactorsClassLoader = mock(ISmartactorsClassLoader.class);
        IModule module = mock(IModule.class);
        when(module.getClassLoader()).thenReturn(smartactorsClassLoader);
        ModuleManager.setCurrentModule(module);

        boolean result = serverStarter.initServerFeatures(classLoader, paths);

        assertTrue(result);
    }

    @Test
    public void failServerFeaturesInitBootstrapStartFail() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);

        ISmartactorsClassLoader smartactorsClassLoader = mock(ISmartactorsClassLoader.class);
        IModule module = mock(IModule.class);
        when(module.getClassLoader()).thenReturn(smartactorsClassLoader);
        ModuleManager.setCurrentModule(module);
        doThrow(new ProcessExecutionException("Something went wrong",
                new Exception(
                        "inner 1",
                        new Exception(
                                "inner 2",
                                new Exception("Bootstrap start fail")
                        )
                )
        )).when(bootstrap).start();

        boolean result = serverStarter.initServerFeatures(classLoader, paths);

        assertFalse(result);
    }

    @Test
    public void failServerFeaturesInitBootstrapRevertFail() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);

        ISmartactorsClassLoader smartactorsClassLoader = mock(ISmartactorsClassLoader.class);
        IModule module = mock(IModule.class);
        when(module.getClassLoader()).thenReturn(smartactorsClassLoader);
        ModuleManager.setCurrentModule(module);
        doThrow(new ProcessExecutionException("Bootstrap start fail")).when(bootstrap).start();
        doThrow(new RevertProcessExecutionException("Bootstrap revert fail")).when(bootstrap).revert();

        boolean result = serverStarter.initServerFeatures(classLoader, paths);

        assertFalse(result);
    }

    @Test(expected = ServerStarterException.class)
    public void failServerFeaturesFeatureSorting() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);

        ISmartactorsClassLoader smartactorsClassLoader = mock(ISmartactorsClassLoader.class);
        IModule module = mock(IModule.class);
        when(module.getClassLoader()).thenReturn(smartactorsClassLoader);
        ModuleManager.setCurrentModule(module);
        when(featureSorting.sortFeatures(any())).thenThrow(new FeatureSortingException("Failed to sort"));

        serverStarter.initServerFeatures(classLoader, paths);
    }

    @Test(expected = ServerStarterException.class)
    public void failServerFeaturesFeatureReader() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);

        ISmartactorsClassLoader smartactorsClassLoader = mock(ISmartactorsClassLoader.class);
        IModule module = mock(IModule.class);
        when(module.getClassLoader()).thenReturn(smartactorsClassLoader);
        ModuleManager.setCurrentModule(module);
        when(featureReader.readFeatures(any())).thenThrow(new FeatureReaderException("Failed to sort"));

        serverStarter.initServerFeatures(classLoader, paths);
    }

    @Test(expected = ServerStarterException.class)
    public void failServerFeaturesPluginLoader() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);

        ISmartactorsClassLoader smartactorsClassLoader = mock(ISmartactorsClassLoader.class);
        IModule module = mock(IModule.class);
        when(module.getClassLoader()).thenReturn(smartactorsClassLoader);
        ModuleManager.setCurrentModule(module);
        doThrow(new PluginLoaderException("Failed to sort")).when(pluginLoader).loadPlugins(any(List.class));

        serverStarter.initServerFeatures(classLoader, paths);
    }
}
