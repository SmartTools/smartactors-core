package info.smart_tools.smartactors.launcher;

import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.launcher.core.CoreInitializer;
import info.smart_tools.smartactors.launcher.core.CoreLoader;
import info.smart_tools.smartactors.launcher.interfaces.ILauncher;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.ICoreInitializer;
import info.smart_tools.smartactors.launcher.interfaces.core.ICoreLoader;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.CoreInitializerException;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.CoreLoaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iproperties_reader.PropertiesReaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.launcher.LauncherExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.launcher.LauncherInitializeException;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.interfaces.iproperties_reader.IPropertiesReader;
import info.smart_tools.smartactors.launcher.logger.Logger;
import info.smart_tools.smartactors.launcher.properties_reader.PropertiesReader;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Launcher implements ILauncher {

    private final ILogger log;

    private ICoreLoader coreLoader;
    private ICoreInitializer coreInitializer;

    private Map<Object, Object> properties;

    public Launcher() {
        this.log = new Logger();
    }

    public void initialize() throws LauncherInitializeException {
        try {
            Thread.currentThread().setName("BaseThread");
            ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));

            IPropertiesReader propertiesReader = new PropertiesReader("./launcher.properties");
            this.properties = propertiesReader.readProperties();

            this.coreLoader = new CoreLoader();
            this.coreInitializer = new CoreInitializer((String) properties.get("launcher.dependencies.path"));
        } catch (PropertiesReaderException e) {
            throw new LauncherInitializeException("Failed to read properties for launcher", e);
        }
    }

    public void start() throws LauncherExecutionException {
        try {
            log.info("Starting to load core");
            LocalTime start = LocalTime.now();
            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_TIME;

            String corePath = (String) properties.get("core.path");
            List<IPath> featureJars = coreLoader.loadCoreFeature(corePath);
            coreInitializer.initializeCoreFeatures(featureJars);

            Duration elapsedTime = Duration.between(start, LocalTime.now());
            LocalTime elapsedTimeToLocalTime = LocalTime.ofNanoOfDay(elapsedTime.toNanos());

            log.info("Stage 1: server core has been loaded");
            log.info("Stage 1: elapsed time - {0}", elapsedTimeToLocalTime.format(df));
        } catch (CoreLoaderException e) {
            throw new LauncherExecutionException("Failed to load core features", e);
        } catch (CoreInitializerException e) {
            throw new LauncherExecutionException("Failed to initialize core features", e);
        }
    }
}
