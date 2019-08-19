package info.smart_tools.smartactors.das.commands;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.das.models.Feature;
import info.smart_tools.smartactors.das.models.Project;
import info.smart_tools.smartactors.das.models.UploadRepository;
import info.smart_tools.smartactors.das.utilities.exception.InvalidCommandLineArgumentException;
import info.smart_tools.smartactors.das.utilities.exception.ProjectResolutionException;
import info.smart_tools.smartactors.das.utilities.interfaces.ICommandLineArgsResolver;
import info.smart_tools.smartactors.das.utilities.interfaces.IProjectResolver;

import java.util.List;

public class CreateFeature implements IAction {

    @Override
    public void execute(final Object o)
            throws ActionExecutionException, InvalidArgumentException {
        System.out.println("Creating feature ...");

        try {
            ICommandLineArgsResolver clar = (ICommandLineArgsResolver) ((Object[]) o)[0];
            IProjectResolver pr = (IProjectResolver) ((Object[]) o)[1];
            Project project = pr.resolveProject();
            String name = clar.getFeatureName();
            String groupId = project.getGroupId();
            if (clar.isGroupId()) {
                groupId = clar.getGroupId();
            }
            String version = project.getVersion();
            if (clar.isVersion()) {
                version = clar.getVersion();
            }

            Feature feature = new Feature(name, groupId, version, project);

            // Addition the feature directory
            feature.makeFeatureDirectory();

            // Addition the feature pom.xml file
            feature.makePomFile();

            // Addition upload repositories
            List<UploadRepository> repositories = project.getFeatureOnCreationUploadRepositories();
            for (UploadRepository repository : repositories) {
                feature.addOrUpdateUploadRepository(repository);
                feature.addOrUpdateUploadRepositoryToPom(repository.getId(), repository.getUrl());
            }

            // Addition the feature config.json file
            feature.makeConfigFile();

            // Creation the distribution module
            feature.makeDistributionModule();

            // Addition the module section to the project pom
            feature.updateProjectPom();

            // Addition feature to the project
            project.addOrUpdateFeature(feature);

            // Update the project meta data file
            feature.getOwnerProject().saveMetaDataFile();


        } catch (InvalidCommandLineArgumentException | ProjectResolutionException e) {
            System.out.println(e.getMessage());

            return;
        } catch (Exception e) {
            throw new ActionExecutionException("Could not create instance of Feature.", e);
        }

        System.out.println("Feature has been created successful.");
    }
}
