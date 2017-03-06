package info.smart_tools.smartactors.ads;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ImportProject implements IAction {

    private static final String POM_NAME = "pom.xml";
    private static final String CONFIG_JSON = "config.json";

    @Override
    public void execute(final Object o)
            throws ActionExecuteException, InvalidArgumentException {
        System.out.println("Importing project ...");
        String[] args = (String[]) o;

        try {
            CommandLineArgsResolver clar = new CommandLineArgsResolver(args);

            String path = clar.getPath();
            String projectName = clar.getProjectName();
            String groupId = null;
            String version = null;
            if (clar.isGroupId()) {
                groupId = clar.getGroupId();
            }
            if (clar.isVersion()) {
                version = clar.getVersion();
            }

            Path sourcePath = Paths.get(path);
            if (null == sourcePath || !sourcePath.toFile().exists()) {
                System.out.println("Could not find project source by given path.");
                throw new Exception("Could not find project source by given path.");
            }

            if (null == groupId || groupId.isEmpty()) {
                groupId = "com.mydomain";
            }
            if (null == version || version.isEmpty()) {
                version = "0.0.1-SNAPSHOT";
            }

            ProjectResolver pr = new ProjectResolver();
            Project project = pr.createProject(projectName, groupId, version);

            project.makeProjectDirectory();

            copyFilesFromSource(sourcePath, project.getPath());

            project.makePomFile();

            File[] directories = new File(project.getPath().toString()).listFiles(File::isDirectory);
            if (null != directories) {
                for (File file : directories) {
                    createFeature(file, project);
                }
            }

            project.saveMetaDataFile();
        } catch (Exception e) {
            System.out.println("Project import has been failed.");
            System.err.println(e);

            throw new ActionExecuteException(e);
        }
        System.out.println("Project has been imported successful.");
    }

    private void copyFilesFromSource(final Path source, final Path destination)
            throws Exception {
        FileUtils.copyDirectoryStructure(source.toFile(), destination.toFile());
    }

    private void createFeature(final File directory, final Project project)
            throws Exception {

        System.out.println("\tImport feature - " + directory.toString() + "...");
        String artifactId = null;
        String groupId = project.getGroupId();
        String version = project.getVersion();
        String name = directory.getName();
        IObject config = null;

        // read pom file if exists (groupId, artifactId, version, modules, repositories)
        Path featurePath = Paths.get(project.getPath().toString(), directory.getName());
        Path featurePom = Paths.get(featurePath.toString(), POM_NAME);
        if (null != featurePom && featurePom.toFile().exists()) {
            version = PomReader.getVersion(featurePom.toFile());
            groupId = PomReader.getGroupId(featurePom.toFile());
            artifactId = PomReader.getArtifactId(featurePom.toFile());
            name = directory.getName();
        }
        // read config.json
        Path configFile = Paths.get(featurePath.toString(), CONFIG_JSON);
        if (null != configFile && configFile.toFile().exists()) {
            config = readConfigFile(configFile.toFile());
        }

        // create feature
        Feature feature = new Feature(name, artifactId, groupId, version, project);

        // Addition the feature pom.xml file
        feature.makePomFile();

        // Addition the feature config.json file
        if (null == config) {
            feature.makeConfigFile();
        } else {
            config.setValue(new FieldName("featureName"), groupId + ":" + feature.getArtifactId());
            feature.makeConfigFile(config);
        }

        // Creation the distribution module
        feature.makeDistributionModule();

        // Addition the module section to the project pom
        feature.updateProjectPom();

        File[] modules = new File(feature.getPath().toString()).listFiles(File::isDirectory);
        if (null != modules && modules.length > 0) {
            for(File module : modules) {
                if (!module.getName().equals(feature.getFeatureDistributionModuleName())) {
                    createFeatureModule(module, feature);
                }
            }
        }

        // Addition feature to the project
        project.addOrUpdateFeature(feature);
        System.out.println(" ... completed.");
    }

    private IObject readConfigFile(final File file)
            throws Exception {
        try (Scanner scanner = new Scanner(file)) {
            String text = scanner.useDelimiter("\\A").next();
            scanner.close();

            return new DSObject(text);
        } catch (Exception e) {
            System.out.println("Could not read json file:");
            System.err.println(e);
        }
        return null;
    }

    private void createFeatureModule(final File directory, final Feature feature)
            throws Exception {
        System.out.print("\t\tImport feature module - " + directory.toString() + "...");
        String version = feature.getVersion();
        String name = directory.getName();
        String artifactId = null;
        List<Dependency> dependencies = new ArrayList<>();

        // read pom file if exists (artifactId, version, dependencies)
        Path modulePath = Paths.get(feature.getPath().toString(), directory.getName());
        Path modulePom = Paths.get(modulePath.toString(), POM_NAME);
        if (null != modulePom && modulePom.toFile().exists()) {
            version = PomReader.getVersion(modulePom.toFile());
            name = directory.getName();
            artifactId = PomReader.getArtifactId(modulePom.toFile());
            dependencies = PomReader.getDependencies(modulePom.toFile());
        }

        Actor actor = new Actor(name, artifactId, version, feature);

        // Addition the actor pom.xml file
        actor.makePomForImport(dependencies);

        // Addition the module section to the project pom
        actor.updateFeaturePom();

        // Addition actor to the feature
        feature.addOrUpdateActor(actor);
        System.out.println(" ... completed.");
    }
}
