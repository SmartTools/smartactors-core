package info.smart_tools.smartactors.uploader;

import com.beust.jcommander.JCommander;
import info.smart_tools.smartactors.uploader.jcommander.Arguments;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeployResult;
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

public class Uploader {

    public static void main(final String[] args) throws Exception {
        Thread.sleep(5000);

        Arguments arguments = new Arguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        run(arguments);
    }

    private static void run(final Arguments arguments) throws DeploymentException {
        String featurePath = arguments.getFeaturePath();
        String username = arguments.getUsername();
        String password = arguments.getPassword();

        File featureFile = new File(featurePath);
        // TODO: add artifact name
        Artifact artifact = new DefaultArtifact("")
                .setFile(featureFile);

        Authentication authentication = new AuthenticationBuilder()
                .addUsername(username)
                .addPassword(password)
                .build();

        // TODO: add url and id for remote repository
        RemoteRepository remoteRepository = new RemoteRepository.Builder(
                "",
                "default",
                ""
        ).setAuthentication(authentication)
                .build();
        DeployRequest deployRequest = new DeployRequest();
        deployRequest.setArtifacts(Arrays.asList(artifact));
        deployRequest.setRepository(remoteRepository);

        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession repositorySystemSession = newSession(repositorySystem);

        DeployResult result = repositorySystem.deploy(repositorySystemSession, deployRequest);

        System.out.println(result.toString());
    }

    private static RepositorySystemSession newSession(final RepositorySystem repositorySystem) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        // TODO: set local repository
        LocalRepository localRepository = new LocalRepository("");
        session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session, localRepository));

        return session;
    }

    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }
}
