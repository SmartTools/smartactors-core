package info.smart_tools.smartactors.das.utilities;


import info.smart_tools.smartactors.das.models.Feature;
import info.smart_tools.smartactors.das.models.Project;
import info.smart_tools.smartactors.das.utilities.exception.ProjectCreationException;
import info.smart_tools.smartactors.das.utilities.exception.ProjectResolutionException;
import info.smart_tools.smartactors.das.utilities.interfaces.IProjectResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectResolver implements IProjectResolver {

    private Project project = null;
    private Feature currentFeature = null;

    private static final String PROJECT_META_DATA_FILE = "das.data";

    public Project resolveProject()
            throws ProjectResolutionException {
        try {
            Path projectPath = Paths.get("");
            Path path = Paths.get("", PROJECT_META_DATA_FILE);
            if (path.toFile().exists()) {
                this.project = new Project(JsonFile.load(path.toFile()), projectPath);

                return this.project;
            }

            projectPath = Paths.get(Paths.get("").toAbsolutePath().getParent().toString());
            path = Paths.get(Paths.get("").toAbsolutePath().getParent().toString(), PROJECT_META_DATA_FILE);
            if (path.toFile().exists()) {
                this.project = new Project(JsonFile.load(path.toFile()), projectPath);

                Path featurePath = Paths.get("").toAbsolutePath();
                String featureName = featurePath.relativize(projectPath).toString();
                this.currentFeature = this.project.getFeatures().stream()
                        .filter(f -> f.getName().equals(featureName))
                        .findFirst()
                        .orElse(null);

                return this.project;
            }
            throw new Exception("ProjectResolver:resolveProject - failed. ");
        } catch (Exception e) {
            throw new ProjectResolutionException("Could not find project meta data. Please enter to the project dir (feature dir) and try again.");
        }
    }

    public Project createProject(final String name, final String groupId, final String version, final Path path)
            throws ProjectCreationException {
        try {
            this.project = new Project(name, groupId, version, path);

            return this.project;
        } catch (Exception e) {
            throw new ProjectCreationException("Could not create new project: " + e.getMessage());
        }
    }

    public Feature getCurrentFeature() {
        return this.currentFeature;
    }
}
