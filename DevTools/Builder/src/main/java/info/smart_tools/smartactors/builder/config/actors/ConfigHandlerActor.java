package info.smart_tools.smartactors.builder.config.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.builder.config.IConfigStrategy;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigHandlerActor extends StatelessActor {

    private final Map<String, IConfigStrategy> strategies;

    private final String extensions;

    public ConfigHandlerActor(final Map<String, IConfigStrategy> strategies) {
        this.strategies = strategies;

        this.extensions = String.join(",", strategies.keySet());
    }

    @Executable
    public Path parseConfig(final String featurePath) throws IOException {
        Path fp = Paths.get(featurePath);
        PathMatcher matcher = FileSystems.getDefault()
            .getPathMatcher("glob:**/config.{" + extensions + "}");
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attributes) -> matcher.matches(path);

        try (Stream<Path> configsStream = Files.find(fp, 1, predicate)) {
            Path config = getConfigPath(configsStream);
            Path configBackup = config.resolveSibling(config.getFileName() + "_backup");
            Files.copy(config, configBackup, StandardCopyOption.REPLACE_EXISTING);

            String configExt = getExtension(config);

            IConfigStrategy extStrategy = this.strategies.get(configExt);
            extStrategy.apply(fp);

            return config;
        }
    }

    @Executable
    public void restoreConfig(final String featurePath, final Path configPath) throws IOException {
        Path configBackup = configPath.resolveSibling(configPath.getFileName() + "_backup");
        String configExt = getExtension(configPath);

        Files.copy(configBackup, configPath, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(configBackup);

        IConfigStrategy extStrategy = this.strategies.get(configExt);
        extStrategy.cleanUp(Paths.get(featurePath));
    }

    private Path getConfigPath(final Stream<Path> pathStream) {
        List<Path> paths = pathStream.collect(Collectors.toList());
        if (paths.isEmpty()) {
            throw new RuntimeException("Found no config for this feature.");
        }
        if (paths.size() > 1) {
            throw new RuntimeException("Found more than one config for this feature.");
        }

        return paths.get(0);
    }

    private String getExtension(final Path path) {
        String stringPath = path.toString();
        int separatorIndex = stringPath.lastIndexOf(".");

        return stringPath.substring(separatorIndex + 1);
    }
}
