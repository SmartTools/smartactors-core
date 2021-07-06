package info.smart_tools.smartactors.downloader;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Downloader {

    private static final String PACKAGE_TYPE_JSON = "json";
    private static final String PACKAGE_TYPE_JAR = "jar";
    private static final String PACKAGE_TYPE_ZIP = "zip";
    private static ObjectMapper objectMapper = new ObjectMapper();


    public static void main(final String[] args)
            throws Exception {
        Args arguments = new Args();
        JCommander
                .newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);
        List<Repository> repositories = new ArrayList<>();
        Feature feature = readFeatureData(Paths.get(arguments.getFileName()).toFile());
        Set<Feature> features = collectLinkedFeatureNameByOne(feature, repositories);
        downloadFeatures(features, repositories, new File("downloads"));
        return;
    }

    private static Set<Feature> collectLinkedFeatureNameByOne(
            final Feature feature, final List<Repository> repositories
    )
            throws Exception {
        final Set<String> featureNames = new HashSet<>();
        Set<Feature> features = new HashSet<>();
        featureNames.add(feature.getFeatureName());
        features.add(feature);
        repositories.add(feature.getRepository());
        List<String> loadingList = new ArrayList<>();
        if (null != feature.getAfterFeatures() && !feature.getAfterFeatures().isEmpty()) {
            loadingList.addAll(feature.getAfterFeatures());
        }
        if (null != feature.getPlugins() && !feature.getPlugins().isEmpty()) {
            loadingList.addAll(feature.getPlugins());
        }
        while (loadingList.size() > 0) {
            String current = loadingList.get(0);
            loadingList.remove(0);
            FeatureNameSpace currentFeatureNameSpace = new FeatureNameSpace(current);
            File file = getObjectFromRepository(currentFeatureNameSpace, repositories, PACKAGE_TYPE_JSON);
            Feature newFeature = readFeatureData(file);
            featureNames.add(newFeature.getFeatureName());
            features.add(newFeature);
            if (null != newFeature.getAfterFeatures() && !newFeature.getAfterFeatures().isEmpty()) {
                loadingList.addAll(
                        newFeature.getAfterFeatures().stream().filter(
                                f -> !featureNames.contains(f)
                        ).collect(Collectors.toList())
                );
            }
            if (null != newFeature.getPlugins() && !newFeature.getPlugins().isEmpty()) {
                loadingList.addAll(
                        newFeature.getPlugins().stream().filter(
                                f -> !featureNames.contains(f)
                        ).collect(Collectors.toList())
                );
            }
        }

        return features;
    }

    private static void downloadFeatures(
            final Set<Feature> features, final List<Repository> repositories, final File directory
    ) throws Exception {
        if (!directory.exists()) {
            Files.createDirectory(Paths.get(directory.toURI()));
        }
        features.forEach(
                f-> {
                    try {
                        File jar = getObjectFromRepository(
                                new FeatureNameSpace(f.getFeatureName()), repositories, PACKAGE_TYPE_JAR
                        );
                        if (null != jar) {
                            Path check = Paths.get(directory.toString(), jar.toPath().getFileName().toString());
                            if (check.toFile().exists()) {
                                Files.delete(check);
                            }
                            Files.copy(
                                    jar.toPath(), Paths.get(directory.toString(), jar.toPath().getFileName().toString())
                            );
                            Files.delete(jar.toPath());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private static File getObjectFromRepository(
            final FeatureNameSpace feature, final List<Repository> repositories, final String featureType
    ) {

        ConfigurableMavenResolverSystem config = Maven.configureResolver();
        repositories.forEach(
                r-> config.withRemoteRepo(r.getId(), r.getUrl() + "/" + r.getId(), "default")
        );
        File[] result = config
                .resolve(
                        String.join(
                                ":",
                                feature.getGroup(), feature.getName(), featureType, feature.getVersion()
                        )
                )
                .withoutTransitivity()
                .asFile();
        if (result != null && result.length > 0) {
            return result[0];
        }

        return null;
    }

    private static Feature readFeatureData(final File file) {
        Feature feature = null;
        if (null != file && file.exists()) {
            try {
                feature = objectMapper.readValue(file, Feature.class);
            } catch (Exception e) {
                System.err.println("Failed to get feature data from file " + file.getName() + " .");
            }
        }

        return feature;
    }
}
