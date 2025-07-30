package info.smart_tools.smartactors.uploader.feature_uploader;

import info.smart_tools.smartactors.uploader.features.Feature;
import info.smart_tools.smartactors.uploader.repositories.RepositoryConfig;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

import java.io.File;
import java.util.Arrays;

public class MavenFeatureUploader implements IFeatureUploader {

    @Override
    public Boolean upload(
            final File file,
            final Feature feature,
            final RepositoryConfig repositoryConfig,
            final String username,
            final String password
    ) {
        Artifact artifact = new DefaultArtifact(feature.getFeatureName())
                .setFile(file);

        Authentication authentication = new AuthenticationBuilder()
                .addUsername(username)
                .addPassword(password)
                .build();

        RemoteRepository remoteRepository = new RemoteRepository.Builder(
                repositoryConfig.getRepositoryId(),
                "default",
                repositoryConfig.getRepositoryUrl()
        ).setAuthentication(authentication)
                .build();
        DeployRequest deployRequest = new DeployRequest();
        deployRequest.setArtifacts(Arrays.asList(artifact));
        deployRequest.setRepository(remoteRepository);

        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession repositorySystemSession = newSession(repositorySystem);

        try {
            repositorySystem.deploy(repositorySystemSession, deployRequest);
            return Boolean.TRUE;
        } catch (DeploymentException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private RepositorySystemSession newSession(final RepositorySystem repositorySystem) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepository = new LocalRepository("uploader_cache");
        session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session, localRepository));

        return session;
    }

    private RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }
}
