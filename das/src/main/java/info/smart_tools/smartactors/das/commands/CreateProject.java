package info.smart_tools.smartactors.das.commands;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.das.models.Project;
import info.smart_tools.smartactors.das.utilities.exception.InvalidCommandLineArgumentException;
import info.smart_tools.smartactors.das.utilities.exception.ProjectCreationException;
import info.smart_tools.smartactors.das.utilities.interfaces.ICommandLineArgsResolver;
import info.smart_tools.smartactors.das.utilities.interfaces.IProjectResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateProject implements IAction {

    private static final String defGroupId = "com.my-project";
    private static final String defVersion = "0.1.0-SNAPSHOT";
    private static final String defProjectName = "MyProject";

    @Override
    public void execute(final Object o)
            throws ActionExecutionException, InvalidArgumentException {
        System.out.println("Creating project ...");
        try {
            ICommandLineArgsResolver clar = (ICommandLineArgsResolver) ((Object[]) o)[0];
            IProjectResolver pr = (IProjectResolver) ((Object[]) o)[1];
            String name = defProjectName;
            String group = defGroupId;
            String version = defVersion;
            Path path = Paths.get("");
            if (clar.isProjectName()) {
                name = clar.getProjectName();
            }
            if (clar.isGroupId()) {
                group = clar.getGroupId();
            }
            if (clar.isVersion()) {
                version = clar.getVersion();
            }
            if (clar.isPath()) {
                path = Paths.get(clar.getPath());
            }

            Project project = pr.createProject(name, group, version, path);

            project.makeProjectDirectory();

            project.makePomFile();

            project.saveMetaDataFile();
        } catch (InvalidCommandLineArgumentException | ProjectCreationException e) {
            System.out.println(e.getMessage());

            return;
        } catch (Exception e) {
            System.out.println("Project creation has been failed.");
            System.err.println(e);

            throw new ActionExecutionException(e);
        }
        System.out.println("Project has been created successful.");
    }
}
