package info.smart_tools.smartactors.downloader.feature_downloader;

import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.features.FeatureNamespace;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MavenFeatureDownloader implements IFeatureDownloader {
    private final static String URL_DELIMITER = "/";
    private final static String MAVEN_REPOSITORY_LAYOUT = "default";
    private final static String FEATURE_NAMESPACE_DELIMITER = ":";

    private ConfigurableMavenResolverSystem downloaderSystem;

    @Override
    public void initialize(final Object object) {
        this.downloaderSystem = (ConfigurableMavenResolverSystem) object;
    }

    @Override
    public void addRepository(final Repository repository) {
        if (null != downloaderSystem) {
            downloaderSystem.withRemoteRepo(
                    repository.getId(),
                    repository.getUrl() + URL_DELIMITER + repository.getId(),
                    MAVEN_REPOSITORY_LAYOUT
            );
        }
    }

    @Override
    public void addRepositories(final List<Repository> repositories) {
        if (null != repositories) {
            repositories.forEach(this::addRepository);
        }
    }

    @Override
    public List<File> download(final FeatureNamespace featureNamespace, final String featureType) {
        return
                null != this.downloaderSystem && null != featureNamespace && null != featureType ?
                Arrays.asList(
                        this.downloaderSystem
                                .resolve(
                                        String.join(
                                                FEATURE_NAMESPACE_DELIMITER,
                                                featureNamespace.getGroup(),
                                                featureNamespace.getName(),
                                                featureType,
                                                featureNamespace.getVersion()
                                        )
                                )
                                .withoutTransitivity()
                                .asFile()
                ) :
                null;
    }
}
