package info.smart_tools.smartactors.feature_management.download_feature_actor;

import com.jcabi.aether.Aether;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.feature_management.download_feature_actor.exception.DownloadFeatureException;
import info.smart_tools.smartactors.feature_management.download_feature_actor.wrapper.DownloadFeatureWrapper;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Actor that downloads artifact from remote repository.
 */
public class DownloadFeatureActor {

    private final IFieldName repositoryIdFN;
    private final IFieldName repositoryTypeFN;
    private final IFieldName repositoryUrlFN;
    private final IFieldName repositoryUsernameFN;
    private final IFieldName repositoryUserPassFN;
    private final IFieldName repositoryPrivateKeyFileFN;
    private final IFieldName repositoryPassPhraseFN;

    private final static String DOWNLOAD_DIRECTORY = "downloads";
    private final static String IOC_FEATURE_REPOSITORY_STORAGE_NAME = "feature-repositories";
    private final static String MAVEN_ARTIFACT_SCOPE = "runtime";
    private final static String ARCHIVE_POSTFIX = "archive";

    //TODO: this parameters would be took out into the config.json as actor arguments
    private static final String DEF_PACKAGE_TYPE = "jar";
    private final static String FIELD_NAME_FACTORY_STARTEGY_NAME =
            "info.smart_tools.smartactors.iobject.ifield_name.IFieldName";
    private final static List<String> FILE_TYPE_LIST = Arrays.asList("zip", "jar");

    /**
     * Default constructor
     * @throws ResolutionException if any errors occurred on IOC resolution
     */
    public DownloadFeatureActor()
            throws ResolutionException {
        this.repositoryIdFN = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "repositoryId");
        this.repositoryTypeFN = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "type");
        this.repositoryUrlFN = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "url");
        this.repositoryUsernameFN = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "username");
        this.repositoryUserPassFN = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "password");
        this.repositoryPrivateKeyFileFN = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "privateKeyFile");
        this.repositoryPassPhraseFN = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "passphrase");
    }

    /**
     * Download feature (artifact) from remote repository
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws DownloadFeatureException if any errors occurred on feature downloading
     */
    public void download(final DownloadFeatureWrapper wrapper)
            throws DownloadFeatureException {
        IFeature feature;
        try {
            feature = wrapper.getFeature();
        } catch (ReadValueException e) {
            throw new DownloadFeatureException("Feature should not be null.");
        }
        if (null == feature.getDependencies() && null != feature.getGroupId()) {
            System.out.println("[\033[1;34mINFO\033[0m] Start downloading feature - '" + feature.getDisplayName() + "'.");
            for(String type : FILE_TYPE_LIST) {
                if (
                        Paths.get(
                                feature.getDirectory().getPath(),
                                feature.getName() + "-" + feature.getVersion() + "." + type
                        ).toFile().exists() ||
                        Paths.get(
                                feature.getDirectory().getPath(),
                                feature.getName() + "-" + feature.getVersion() + "-" + ARCHIVE_POSTFIX + "." + type
                        ).toFile().exists()
                ) {
                    System.out.println("[\033[1;32mOK\033[0m] -------------- Feature '" + feature.getDisplayName() + "' already downloaded.");
                    return;
                }
            }
            try {
                download0(feature);
                System.out.println("[\033[1;32mOK\033[0m] -------------- Feature '" + feature.getDisplayName() + "' downloaded successfully.");
            } catch (Throwable e) {
                feature.setFailed(true);
                System.out.println("[\033[1;31mERROR\033[0m] ---------- Feature '" + feature.getDisplayName() + "' downloading aborted with exception:");
                System.out.println(e);
            }
        }
    }

    private void download0(final IFeature feature)
            throws Exception {
        try {
            File local = new File(DOWNLOAD_DIRECTORY);
            List<IObject> repositories = IOC.resolve(Keys.getKeyByName(IOC_FEATURE_REPOSITORY_STORAGE_NAME));
            Collection<RemoteRepository> remotes = repositories.stream().map(
                    repository -> {
                        try {
                            RemoteRepository rr = new RemoteRepository(
                                    (String) repository.getValue(this.repositoryIdFN),
                                    (String) repository.getValue(this.repositoryTypeFN),
                                    (String) repository.getValue(this.repositoryUrlFN));
                            if (repository.getValue(this.repositoryUsernameFN) != null) {
                                String userName = (String) repository.getValue(this.repositoryUsernameFN);
                                String userPass = (String) repository.getValue(this.repositoryUserPassFN);
                                String privateKeyFile = (String) repository.getValue(this.repositoryPrivateKeyFileFN);
                                String passPhrase = (String) repository.getValue(this.repositoryPassPhraseFN);
                                rr.setAuthentication(new Authentication(userName, userPass, privateKeyFile, passPhrase));
                            }
                            return rr;
                        } catch (ReadValueException | InvalidArgumentException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).collect(Collectors.toList());

            DefaultArtifact defaultArtifact = new DefaultArtifact(
                    feature.getGroupId(),
                    feature.getName(),
                    "",
                    feature.getPackageType() != null ? feature.getPackageType() : DEF_PACKAGE_TYPE,
                    feature.getVersion()
            );
            Aether aether = new Aether(remotes, local);
            List<Artifact> artifacts = aether.resolve(
                    defaultArtifact,
                    MAVEN_ARTIFACT_SCOPE
            );
            File artifact = artifacts.get(0).getFile();
            String fileName = artifact.getName();
            File location = Paths.get(feature.getDirectory().getPath(), fileName).toFile();
            Files.copy(artifact.toPath(), location.toPath());
            feature.setLocation(new Path(location));
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }
}
