package info.smart_tools.smartactors.builder.config;

import java.nio.file.Path;

public interface IConfigStrategy {

    void apply(Path featurePath);

    void cleanUp(Path featurePath);
}
