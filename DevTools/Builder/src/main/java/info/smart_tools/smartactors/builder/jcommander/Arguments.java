package info.smart_tools.smartactors.builder.jcommander;

import com.beust.jcommander.Parameter;
import info.smart_tools.simpleactors.commons.IArguments;

public class Arguments implements IArguments {

    @Parameter(
            names = {"command", "cmd", "c", "-command", "-cmd", "-c"},
            description = "Command. Available values: 'build'. Default value is 'build'."
    )
    private String command = "build";

    @Parameter(
            names = {"-featurePath", "featurePath", "-fp"},
            description = "Path to the feature archive for upload, can be .zip or .jar"
    )
    private String featurePath;

    @Override
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFeaturePath() {
        return featurePath;
    }

    public void setFeaturePath(String featurePath) {
        this.featurePath = featurePath;
    }
}
