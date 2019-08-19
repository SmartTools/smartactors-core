package info.smart_tools.smartactors.das.commands;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.das.models.Actor;
import info.smart_tools.smartactors.das.models.Feature;
import info.smart_tools.smartactors.das.models.Project;
import info.smart_tools.smartactors.das.models.UploadRepository;
import info.smart_tools.smartactors.das.utilities.exception.InvalidCommandLineArgumentException;
import info.smart_tools.smartactors.das.utilities.exception.ProjectResolutionException;
import info.smart_tools.smartactors.das.utilities.interfaces.ICommandLineArgsResolver;
import info.smart_tools.smartactors.das.utilities.interfaces.IProjectResolver;

import java.util.List;

public class CreateActor implements IAction {

    @Override
    public void execute(final Object o)
            throws ActionExecutionException, InvalidArgumentException {
        System.out.println("Creating actor ...");

        try {
            ICommandLineArgsResolver clar = (ICommandLineArgsResolver) ((Object[]) o)[0];
            IProjectResolver pr = (IProjectResolver) ((Object[]) o)[1];
            Project project = pr.resolveProject();
            String name = clar.getActorName();
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

            Actor actor = new Actor(name, version, feature);

            // Addition the actor directory
            actor.makeActorDirectory();

            // Addition the actor pom.xml file
            actor.makePomFile();

            // Addition upload repositories
            List<UploadRepository> repositories = project.getActorOnCreationUploadRepositories();
            for (UploadRepository repository : repositories) {
                actor.addOrUpdateUploadRepository(repository);
                actor.addOrUpdateUploadRepositoryToPom(repository.getId(), repository.getUrl());
            }

            // Creation dir src/main/java/ ${feat.group.id}/ ${feat.name}/ ${actor.name}
            actor.makeClassDirectory();

            // Creation actor class file in the src/main/java/ ${feat.group.id}/ ${feat.name}/ ${actor.name}
            actor.makeClassFile();

            // Creation dir src/main/java/ ${feat.group.id}/ ${feat.name}/ ${actor.name} / exception
            actor.makeExceptionDirectory();

            // Creation exception template file in the src/main/java/ ${feat.group.id}/ ${feat.name}/ ${actor.name} / exception
            actor.makeExceptionTemplate();

            // Creation dir src/main/java/ ${feat.group.id}/ ${feat.name}/ ${actor.name} / wrapper
            actor.makeWrapperDirectory();

            // Creation wrapper template file in the src/main/java/java/ ${feat.group.id}/ ${feat.name}/ ${actor.name} / wrapper
            actor.makeWrapperTemplate();

            // Creation dir src/test/java/ ${feat.group.id}/ ${feat.name}/ ${actor.name}
            actor.makeTestDirectory();

            // Creation actor test class file in the src/test/java/ ${feat.group.id}/ ${feat.name}/ ${actor.name}Test
            actor.makeTestClass();

            // Addition the module section to the project pom
            actor.updateFeaturePom();

            // Addition actor to the feature
            feature.addOrUpdateActor(actor);

            // Save project meta data file
            actor.getOwnerFeature().getOwnerProject().saveMetaDataFile();
        } catch (InvalidCommandLineArgumentException | ProjectResolutionException e) {
            System.out.println(e.getMessage());

            return;
        } catch (Exception e) {
            System.out.println("Actor creation has been failed.");
            System.err.println(e);

            throw new ActionExecutionException(e);
        }
        System.out.println("Actor has been created successful.");
    }
}
