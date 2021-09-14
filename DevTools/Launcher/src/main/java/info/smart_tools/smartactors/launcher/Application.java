package info.smart_tools.smartactors.launcher;

import info.smart_tools.smartactors.launcher.core.DependencyLoader;
import info.smart_tools.smartactors.launcher.core.DependencyReplacer;
import info.smart_tools.smartactors.launcher.core.FeatureLoader;
import info.smart_tools.smartactors.launcher.feature.FeatureSorting;
import info.smart_tools.smartactors.launcher.interfaces.ILauncher;
import info.smart_tools.smartactors.launcher.interfaces.core.IFeatureLoader;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.interfaces.iproperties_reader.IPropertiesLoader;
import info.smart_tools.smartactors.launcher.logger.Logger;
import info.smart_tools.smartactors.launcher.properties_reader.PropertiesLoader;

import java.util.HashMap;
import java.util.Map;

public class Application {

    public static void main(final String[] args) throws Exception {
        ILogger log = new Logger();
        log.info("Initializing launcher...");

        if (Boolean.parseBoolean(System.getProperty("launcher.late_init"))) {
            Thread.sleep(10000);
        }

        IPropertiesLoader propertiesReader = new PropertiesLoader("./launcher.properties");
        IFeatureLoader featureLoader = new FeatureLoader();

        Map<String, Object> launcherParameters = new HashMap<>();
        launcherParameters.put("dependency loader", new DependencyLoader());
        launcherParameters.put("dependency replacer", new DependencyReplacer());
        launcherParameters.put("feature sorting", new FeatureSorting());
        Map<Object, Object> launcherProperties = propertiesReader.loadProperties();

        ILauncher launcher = new Launcher(
                launcherProperties,
                log,
                featureLoader
        );
        Boolean hasInitialized = launcher.initialize(launcherParameters);
        if (hasInitialized) {
            log.info("Launcher has been initialized, starting server...");
        }
        Boolean hasStarted = launcher.start();
        if (hasStarted) {
            log.info("Server has been started.");
        }
    }
}
