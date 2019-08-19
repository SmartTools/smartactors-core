package info.smart_tools.smartactors.das.commands;

import com.jcabi.aether.Aether;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.das.utilities.JsonFile;
import info.smart_tools.smartactors.das.utilities.exception.InvalidCommandLineArgumentException;
import info.smart_tools.smartactors.das.utilities.interfaces.ICommandLineArgsResolver;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import net.lingala.zip4j.core.ZipFile;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DownloadCore implements IAction {

    private static final String defGroupId = "info.smart_tools.smartactors";
    private static final String defArtifactId = "core-pack";
    private static final String defVersion = "RELEASE";
    private static final String defRepositoryId = "archiva.smartactors-features";
    private static final String defRepositoryUrl = "http://archiva.smart-tools.info/repository/smartactors-features/";
    private static final String defDirectoryName = "core";
    private static final String defPackageType = "jar";

    private static final Map<String, IActionTwoArgs<File, Path>> func = new HashMap<>();

    static {
        func.put("zip", (o1, o2) -> {
            try {
                ZipFile zipFile = new ZipFile(o1);
                zipFile.extractAll(
                        Paths.get(
                                o2.toFile().getAbsolutePath(),
                                defDirectoryName
                        ).toString()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        func.put("jar", (o1, o2) -> {
                try {
                    Path dir = Files.createDirectory(
                            Paths.get(
                                    o2.toFile().getAbsolutePath(),
                                    defDirectoryName,
                                    o1.getName().substring(0, o1.getName().lastIndexOf("."))
                            )
                    );
                    Files.copy(
                            o1.toPath(),
                            Paths.get(dir.toString(), o1.getName()),
                            REPLACE_EXISTING
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        });
    }

    @Override
    public void execute(final Object o)
            throws ActionExecutionException, InvalidArgumentException {
        System.out.println("Download server core ...");
        ICommandLineArgsResolver clar = (ICommandLineArgsResolver) ((Object[]) o)[0];

        try {
            String groupId = defGroupId;
            String artifactId = defArtifactId;
            String version = defVersion;
            String rid = defRepositoryId;
            String rurl = defRepositoryUrl;
            Path  path = Paths.get("");
            Path coreListLocation = null;
            String type = defPackageType;
            if (clar.isArtifactId()) {
                artifactId = clar.getArtifactId();
            }
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
            if (clar.isSourceLocation()) {
                coreListLocation = Paths.get(clar.getSourceLocation());
            }
            if (clar.isPackageType()) {
                type = clar.getPackageType();
            }

            IObject repositoriesAndFeatures;
            if (null != coreListLocation) {
                repositoriesAndFeatures = getRepositoriesAndFeaturesListFromFile(coreListLocation);
            } else {
                repositoriesAndFeatures = getRepositoriesAndFeaturesListFromRepository(
                        path, artifactId, groupId, version, rid, rurl
                );
            }
            loadCoreFeatures(repositoriesAndFeatures, path, type);
        } catch (InvalidCommandLineArgumentException e) {
            System.out.println(e.getMessage());

            return;
        } catch (ZipException e) {
            System.out.println("Could not extract zip archive: " + e.getMessage());

            return;
        } catch (Exception e) {
            System.out.println("Server core downloading has been failed.");
            System.err.println(e);

            throw new ActionExecutionException(e);
        }
        System.out.println("Server core has been downloaded successful.");
    }

    private IObject getRepositoriesAndFeaturesListFromRepository(
            final Path path,
            final String artifactId,
            final String groupId,
            final String version,
            final String rid,
            final String rurl
    )
            throws Exception {
        File destination = path.toFile();
        RemoteRepository remoteRepository = new RemoteRepository(
                rid, "default", rurl
        );
        Collection<RemoteRepository> repositories = new ArrayList<RemoteRepository>() {{
            add(remoteRepository);
        }};

        List<Artifact> artifacts = new Aether(repositories, Paths.get(
                destination.getAbsolutePath(), "downloads"
        ).toFile()).resolve(
                new DefaultArtifact(
                        groupId,
                        artifactId,
                        "",
                        "json",
                        version
                ),
                "runtime"
        );

        File artifact = artifacts.get(0).getFile();
        IObject repositoriesAndFeatures = JsonFile.load(artifact);
        FileUtils.deleteDirectory(
                Paths.get(
                        destination.getAbsolutePath(), "downloads"
                ).toFile()
        );

        return repositoriesAndFeatures;
    }

    private IObject getRepositoriesAndFeaturesListFromFile(final Path location)
            throws Exception {
        if (null == location) {
            return null;
        }

        return JsonFile.load(location.toFile());
    }

    @SuppressWarnings("unchecked")
    private void loadCoreFeatures(final IObject repositoriesAndFeatures, final Path path, final String type)
            throws Exception {
        List<IObject> repositoriesParams = (List<IObject>) repositoriesAndFeatures.getValue(new FieldName("repositories"));
        List<IObject> featuresParams = (List<IObject>) repositoriesAndFeatures.getValue(new FieldName("features"));
        Collection<RemoteRepository> repositories = repositoriesParams.stream().map(r -> {
            try {
                return new RemoteRepository(
                        (String) r.getValue(new FieldName("repositoryId")),
                        (String) r.getValue(new FieldName("type")),
                        (String) r.getValue(new FieldName("url"))
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        List<Artifact> artifacts = new ArrayList<>();
        for (IObject param : featuresParams) {
            artifacts.addAll(
                    new Aether(repositories, Paths.get(
                            path.toFile().getAbsolutePath(), "downloads"
                    ).toFile()).resolve(
                            new DefaultArtifact(
                                    (String) param.getValue(new FieldName("group")),
                                    (String) param.getValue(new FieldName("name")),
                                    "",
                                    type,
                                    (String) param.getValue(new FieldName("version"))
                            ),
                            "runtime"
                    )
            );
        }
        artifacts.forEach(a -> {
                    try {
                        File f = a.getFile();
                        if (null != f && f.exists()) {
                            IActionTwoArgs<File, Path> action = func.get(type);
                            action.execute(f, path);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        FileUtils.deleteDirectory(
                Paths.get(
                        path.toFile().getAbsolutePath(), "downloads"
                ).toFile()
        );
    }
}
