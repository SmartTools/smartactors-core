package info.smart_tools.smartactors.das.commands;

import com.jcabi.aether.Aether;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.das.utilities.exception.InvalidCommandLineArgumentException;
import info.smart_tools.smartactors.das.utilities.interfaces.ICommandLineArgsResolver;
import net.lingala.zip4j.core.ZipFile;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipException;

public class CreateServer implements IAction {

    private static final String defGroupId = "info.smart_tools.smartactors";
    private static final String defArtifactId = "servers.server2";
    private static final String defVersion = "RELEASE";
    private static final String defRepositoryId = "archiva.servers";
    private static final String defRepositoryUrl = "http://archiva.smart-tools.info/repository/servers/";
    private static final String defDirectoryName = "server";

    @Override
    public void execute(final Object o)
            throws ActionExecutionException, InvalidArgumentException {
        System.out.println("Creating server ...");
        ICommandLineArgsResolver clar = (ICommandLineArgsResolver) ((Object[]) o)[0];

        try {
            String groupId = defGroupId;
            String artifactId = defArtifactId;
            String version = defVersion;
            Path path = Paths.get("");
            String rid = defRepositoryId;
            String rurl = defRepositoryUrl;
            String serverDirectory = defDirectoryName;
            if (clar.isGroupId()) {
                groupId = clar.getGroupId();
            }
            if (clar.isVersion()) {
                version = clar.getVersion();
            }
            if (clar.isPath()) {
                path = Paths.get(clar.getPath());
            }
            if (clar.isUploadRepositoryId()) {
                rid = clar.getUploadRepositoryId();
            }
            if (clar.isUploadRepositoryUrl()) {
                rurl = clar.getUploadRepositoryUrl();
            }
            if (clar.isServerName()) {
                serverDirectory = clar.getServerDirectory();
            }
            if (clar.isArtifactId()) {
                artifactId = clar.getArtifactId();
            }

            File destination = path.toFile();
            RemoteRepository remoteRepository = new RemoteRepository(
                    rid, "default", rurl
            );
            Collection<RemoteRepository> repositories = new ArrayList<RemoteRepository>() {{
                add(remoteRepository);
            }};

            List<Artifact> artifacts = new Aether(repositories, Paths.get(
                    destination.getAbsolutePath().toString(), "/downloads"
            ).toFile()).resolve(
                    new DefaultArtifact(
                            groupId,
                            artifactId,
                            "",
                            "zip",
                            version
                    ),
                    "runtime"
            );

            File artifact = artifacts.get(0).getFile();
            if (null != artifact && artifact.exists()) {
                ZipFile zipFile = new ZipFile(artifact);
                zipFile.extractAll(destination.getAbsolutePath().toString() + File.separator + serverDirectory);
            }
            FileUtils.deleteDirectory(
                    Paths.get(
                            destination.getAbsolutePath().toString(), "/downloads"
                    ).toFile()
            );
        } catch (InvalidCommandLineArgumentException e) {
            System.out.println(e.getMessage());

            return;
        } catch (ZipException e) {
            System.out.println("Could not extract zip archive: " + e.getMessage());

            return;
        } catch (Exception e) {
            System.out.println("Server creation has been failed.");
            System.err.println(e);

            throw new ActionExecutionException(e);
        }
        System.out.println("Server has been created successful.");
    }

}
