package info.smart_tools.smartactors.feature_generator.jcommander;

import com.beust.jcommander.Parameter;
import info.smart_tools.simpleactors.commons.IArguments;

public class Arguments implements IArguments {

    @Parameter(
            names = {"command", "cmd", "c", "-command", "-cmd", "-c"},
            description = "Command. Available values: 'create_feature'. Default value is 'create_feature'."
    )
    private String command = "create_feature";

    @Parameter(
            names = {"-gId", "-groupId", "groupId"},
            description = "Group ID of feature.",
            required = true
    )
    private String groupId;

    @Parameter(
            names = {"-pPath", "-projectPath", "projectPath"},
            description = "Path to the project, default is current directory"
    )
    private String projectPath = "";

    @Parameter(
            names = {"-n", "-name", "name"},
            description = "Name for feature.",
            required = true
    )
    private String name;

    @Parameter(
            names = {"-v", "-version", "version"},
            description = "Set specific version for feature, default - 0.1.0"
    )
    private String version = "0.1.0";

    @Override
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
