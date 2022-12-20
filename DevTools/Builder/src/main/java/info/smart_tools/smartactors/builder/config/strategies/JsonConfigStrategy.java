package info.smart_tools.smartactors.builder.config.strategies;

import info.smart_tools.smartactors.builder.config.IConfigStrategy;

import java.nio.file.Path;

public class JsonConfigStrategy implements IConfigStrategy {

    @Override
    public void apply(Path configPath) {
        // nothing to do, SmartActors already can handle json configs
    }

    @Override
    public void cleanUp(Path featurePath) {
        // nothing to do, SmartActors already can handle json configs
    }
}
