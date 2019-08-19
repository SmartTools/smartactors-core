package info.smart_tools.smartactors.das.commands;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.das.models.Feature;
import info.smart_tools.smartactors.das.models.Plugin;
import info.smart_tools.smartactors.das.models.Project;
import info.smart_tools.smartactors.das.models.UploadRepository;
import info.smart_tools.smartactors.das.utilities.exception.InvalidCommandLineArgumentException;
import info.smart_tools.smartactors.das.utilities.exception.ProjectResolutionException;
import info.smart_tools.smartactors.das.utilities.interfaces.ICommandLineArgsResolver;
import info.smart_tools.smartactors.das.utilities.interfaces.IProjectResolver;

import java.util.List;

public class CreatePlugin implements IAction {

    @Override
    public void execute(final Object o)
            throws ActionExecutionException, InvalidArgumentException {
        System.out.println("Creating plugin ...");

        try {
            ICommandLineArgsResolver clar = (ICommandLineArgsResolver) ((Object[]) o)[0];
            IProjectResolver pr = (IProjectResolver) ((Object[]) o)[1];
            Project project = pr.resolveProject();
            String name = clar.getPluginName();
            String version = null;
            Feature feature = null;
            String featureName = clar.getFeatureName();
            if (null != featureName) {
                feature = project.getFeatures().stream().filter(f -> f.getName().equals(featureName)).findFirst().orElse(null);
            }
            if (null == feature) {
                feature = pr.getCurrentFeature();
            }
            if (clar.isVersion()) {
                version = clar.getVersion();
            } else {
                version = feature.getVersion();
            }

            Plugin plugin = new Plugin(name, version, feature);

            // Addition the plugin directory
            plugin.makePluginDirectory();

            // Addition the plugin pom.xml file
            plugin.makePomFile();

            // Addition upload repositories
            List<UploadRepository> repositories = project.getPluginOnCreationUploadRepositories();
            for (UploadRepository repository : repositories) {
                plugin.addOrUpdateUploadRepository(repository);
                plugin.addOrUpdateUploadRepositoryToPom(repository.getId(), repository.getUrl());
            }

            // Creation dir src/main/java/ ${feat.group.id}/ ${feat.name}/ ${plugin.name}
            plugin.makeClassDirectory();

            // Creation plugin class file in the src/main/java/ ${feat.group.id}/ ${feat.name}/ ${plugin.name}
            plugin.makeClassFile();

            // Creation dir src/test/java/ ${feat.group.id}/ ${feat.name}/ ${plugin.name}
            plugin.makeTestDirectory();

            // Creation plugin test class file in the src/test/java/ ${feat.group.id}/ ${feat.name}/ ${plugin.name}Test
            plugin.makeTestClass();

            // Addition the module section to the project pom
            plugin.updateFeaturePom();

            // Addition plugin to the feature
            feature.addOrUpdatePlugin(plugin);

            // Save project meta data file
            plugin.getOwnerFeature().getOwnerProject().saveMetaDataFile();

        } catch (InvalidCommandLineArgumentException | ProjectResolutionException e) {
            System.out.println(e.getMessage());

            return;
        } catch (Exception e) {
            System.out.println("Plugin creation has been failed.");
            System.err.println(e);

            throw new ActionExecutionException(e);
        }
        System.out.println("Plugin has been created successful.");
    }
}
