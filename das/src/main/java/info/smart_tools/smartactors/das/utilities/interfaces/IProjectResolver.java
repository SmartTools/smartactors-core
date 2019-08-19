package info.smart_tools.smartactors.das.utilities.interfaces;


import info.smart_tools.smartactors.das.models.Feature;
import info.smart_tools.smartactors.das.models.Project;
import info.smart_tools.smartactors.das.utilities.exception.ProjectCreationException;
import info.smart_tools.smartactors.das.utilities.exception.ProjectResolutionException;

import java.nio.file.Path;

public interface IProjectResolver {

    Project resolveProject()
            throws ProjectResolutionException;

    Project createProject(String name, String groupId, String version, Path path)
            throws ProjectCreationException;

    Feature getCurrentFeature();
}
