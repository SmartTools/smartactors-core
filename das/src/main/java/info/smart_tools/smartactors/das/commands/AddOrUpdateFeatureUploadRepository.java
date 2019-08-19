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

import java.util.ArrayList;
import java.util.List;

public class AddOrUpdateFeatureUploadRepository implements IAction {

    @Override
    public void execute(final Object o) throws ActionExecutionException, InvalidArgumentException {
        System.out.println("Adding/updating repository to the feature ...");

        try {
            ICommandLineArgsResolver clar = (ICommandLineArgsResolver) ((Object[]) o)[0];
            IProjectResolver pr = (IProjectResolver) ((Object[]) o)[1];
            Project project = pr.resolveProject();
            Feature feature = null;
            String featureName = clar.getFeatureName();
            List<Feature> features = new ArrayList<>();
            if (null != featureName) {
                feature = project.getFeatures().stream().filter(f -> f.getName().equals(featureName)).findFirst().orElse(null);
            }
            if (null == feature) {
                feature = pr.getCurrentFeature();
            }
            if (null != feature) {
                features.add(feature);
            }
            if (null == featureName || featureName.toLowerCase().equals("all")) {
                features = project.getFeatures();
            }
            String repId = clar.getUploadRepositoryId();
            String repUrl = clar.getUploadRepositoryUrl();

            for (Feature f : features) {
                System.out.println("Feature - " + f.getName() + " ...");
                forOneFeature(f, repId, repUrl);
                System.out.println("... processed.");
            }

            // Save project meta data file
            project.saveMetaDataFile();

        } catch (InvalidCommandLineArgumentException | ProjectResolutionException e) {
            System.out.println(e.getMessage());

            return;
        } catch (Exception e) {
            System.out.println("Addition/update repository has been failed.");
            System.err.println(e);

            throw new ActionExecutionException(e);
        }
        System.out.println("Repository has been added/updated successful.");
    }

    private void forOneFeature(final Feature feature, final String repId, final String repUrl)
            throws Exception {
        // Addition or update feature repository to pom file
        feature.addOrUpdateUploadRepositoryToPom(repId, repUrl);

        // Addition or update feature repository to meta data
        feature.addOrUpdateUploadRepository(new UploadRepository(repId, repUrl));

    }
}
