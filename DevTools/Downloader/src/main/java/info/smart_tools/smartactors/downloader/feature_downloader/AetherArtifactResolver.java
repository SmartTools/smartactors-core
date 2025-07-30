package info.smart_tools.smartactors.downloader.feature_downloader;

import com.jcabi.aether.Aether;
import info.smart_tools.smartactors.downloader.Const__FeatureTypes;
import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.features.FeatureNamespace;
import org.apache.commons.io.FileUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AetherArtifactResolver implements IFeatureDownloader {
    private final static String URL_DELIMITER = "/";
    private final static String MAVEN_REPOSITORY_LAYOUT = "default";
    private static final String DEF_CLASSIFIER = "";
    private final static String MAVEN_ARTIFACT_SCOPE = "runtime";
    private final static String MAVEN_REPOSITORY_DEFAULT_PATH = ".m2";
    private final static String MAVEN_REPOSITORY_PATH_PARAMETER = "maven.repository.path";

    private File targetDirectory;
    private final Collection<RemoteRepository> repositories = new ArrayList<>();

    @Override
    public void initialize(final Object object) {
        this.targetDirectory = new File(MAVEN_REPOSITORY_DEFAULT_PATH);
        if (object instanceof Map) {
            try {
                String location = (String) ((Map<String, Object>) object).get(MAVEN_REPOSITORY_PATH_PARAMETER);
                this.targetDirectory = new File(location);
            } catch (ClassCastException ignore) {}
        }
        this.repositories.clear();
    }

    @Override
    public void addRepository(final Repository repository) {
        RemoteRepository rr = new RemoteRepository(
                repository.getId(),
                MAVEN_REPOSITORY_LAYOUT,
                repository.getUrl() + URL_DELIMITER + repository.getId()
        );
        if (null != repository.getUsername() && null != repository.getPassword()) {
            rr.setAuthentication(
                    new Authentication(repository.getUsername(), repository.getPassword())
            );
        }
        this.repositories.add(rr);
    }

    @Override
    public void addRepositories(final List<Repository> repositories) {
        if (null != repositories) {
            repositories.forEach(this::addRepository);
        }
    }


    @Override
    public List<File> download(final FeatureNamespace featureNamespace, final String featureType) {
        Aether aether = new Aether(this.repositories, this.targetDirectory);
        try {
            DefaultArtifact defaultArtifact = new DefaultArtifact(
                    featureNamespace.getGroup(),
                    featureNamespace.getName(),
                    DEF_CLASSIFIER,
                    featureType != null ? featureType : Const__FeatureTypes.JAR_PACKAGE_TYPE,
                    featureNamespace.getVersion()
            );
            List<Artifact> artifacts = aether.resolve(
                    defaultArtifact,
                    MAVEN_ARTIFACT_SCOPE
            );
            return artifacts.stream().map(Artifact::getFile).collect(Collectors.toList());
        } catch (Throwable e) {
            if (null != featureType && featureType.equals(Const__FeatureTypes.JSON_PACKAGE_TYPE)) {
                try {
                    DefaultArtifact defaultArtifact1 = new DefaultArtifact(
                            featureNamespace.getGroup(),
                            featureNamespace.getName(),
                            DEF_CLASSIFIER,
                            Const__FeatureTypes.JAR_PACKAGE_TYPE,
                            featureNamespace.getVersion()
                    );
                    List<Artifact> artifacts = aether.resolve(
                            defaultArtifact1,
                            MAVEN_ARTIFACT_SCOPE
                    );
                    List<File> files = artifacts.stream().map(Artifact::getFile).collect(Collectors.toList());
                    URL url = new URL("jar:file:" + files.get(0).getAbsolutePath() + "!" + File.separator + "config.json");
                    InputStream is = url.openStream();
                    File outputFile = new File(this.targetDirectory + File.separator + featureNamespace.getName() + ":" + featureNamespace.getVersion() + ".json");
                    FileUtils.copyInputStreamToFile(is, outputFile);
                    is.close();
                    return new ArrayList<File>() {{
                        add(outputFile);
                    }};
                } catch (Throwable ex) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
