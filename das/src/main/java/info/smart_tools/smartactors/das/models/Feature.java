package info.smart_tools.smartactors.das.models;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.das.utilities.FileBuilder;
import info.smart_tools.smartactors.das.utilities.ParameterResolver;
import info.smart_tools.smartactors.das.utilities.PomBuilder;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Feature {

    private static final String POM_NAME = "pom.xml";

    private static final String POM_TEMPLATE = "feature_pom.template";
    private static final String CONFIG_JSON_TEMPLATE = "feature_config_json.template";
    private static final String DISTRIBUTION_POM_TEMPLATE = "distribution_feature_pom.template";
    private static final String DISTRIBUTION_BIN_TEMPLATE = "distribution_feature_bin.template";
    private static final String DEPLOY_PLUGIN_TEMPLATE = "feature_pom_deploy_execution_section.template";

    private static final String MODULE_SECTION_TEMPLATE = "module_section.template";

    private static final String CONFIG_JSON = "config.json";
    private static final String BIN_XML = "bin.xml";

    private static final String PROJECT_NAME_TOKEN = "${proj.name}";
    private static final String PROJECT_GROUP_ID_TOKEN = "${proj.group.id}";
    private static final String PROJECT_VERSION_TOKEN = "${proj.version}";

    private static final String FEATURE_NAME_TOKEN = "${feat.name}";
    private static final String FEATURE_GROUP_ID_TOKEN = "${feat.group.id}";
    private static final String FEATURE_VERSION_TOKEN = "${feat.version}";

    private static final String MODULE_NAME_TOKEN = "${module.name}";

    private static final String DEPLOY_REPOSITORY_ID_TOKEN = "${upload.repository.id}";
    private static final String DEPLOY_REPOSITORY_URL_TOKEN = "${upload.repository.url}";


    private static final String DISTRIBUTION_FEATURE_NAME_TOKEN = "${feat.distribution.name}";

    private static final String DISTRIBUTION_FEATURE_NAME_POSTFIX = "Distribution";

    private static final String END_MODULES_TAG = "</modules>";

    private String rawName;
    private String groupId;
    private String artifactId;
    private String version;
    private List<UploadRepository> uploadRepositories = new ArrayList<>();
    private Project ownerProject;
    private List<Actor> actors = new ArrayList<>();
    private List<Plugin> plugins = new ArrayList<>();

    public Feature(
            final String name,
            final String artifactId,
            final String groupId,
            final String version,
            final Project project
    ) throws Exception {
        if (null == name) {
            System.out.println("Feature name could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        if (null == groupId) {
            System.out.println("Feature groupId could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        if (null == version) {
            System.out.println("Feature version could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        if (null == project) {
            System.out.println("Initial project could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        if (null == artifactId) {
            System.out.println("Feature artifactId could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        this.rawName = name;
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
        this.ownerProject = project;
    }

    public Feature(
            final String name,
            final String groupId,
            final String version,
            final Project project
    ) throws Exception {
        if (null == name) {
            System.out.println("Feature name could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        if (null == groupId) {
            System.out.println("Feature groupId could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        if (null == version) {
            System.out.println("Feature version could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        if (null == project) {
            System.out.println("Initial project could not be null.");
            throw new Exception("Feature:Constructor - failed.");
        }
        this.rawName = name;
        this.artifactId = ParameterResolver.getArtifactId(name);
        this.groupId = groupId;
        this.version = version;
        this.ownerProject = project;
    }

    @SuppressWarnings("unchecked")
    public Feature(final IObject feature, final Project project)
            throws Exception {
        try {
            this.rawName = (String) feature.getValue(new FieldName("rawName"));
            this.groupId = (String) feature.getValue(new FieldName("groupId"));
            this.version = (String) feature.getValue(new FieldName("version"));
            this.artifactId = (String) feature.getValue(new FieldName("artifactId"));
            this.ownerProject = project;

            List<IObject> actors = (List<IObject>) feature.getValue(new FieldName("actors"));
            for (IObject actor : actors) {
                Actor restoredActor = new Actor(actor, this);
                this.actors.add(restoredActor);
            }

            List<IObject> plugins = (List<IObject>) feature.getValue(new FieldName("plugins"));
            for (IObject plugin : plugins) {
                Plugin restoredPlugin = new Plugin(plugin, this);
                this.plugins.add(restoredPlugin);
            }

            List<IObject> repositories = (List<IObject>) feature.getValue(new FieldName("uploadRepositories"));
            for (IObject repository : repositories) {
                UploadRepository restoredRepository = new UploadRepository(repository);
                this.uploadRepositories.add(restoredRepository);
            }

        } catch (InvalidArgumentException | ReadValueException e) {
            throw new Exception("Feature:Constructor - failed.", e);
        }
    }

    public IObject asIObject()
            throws Exception {
        try {
            IObject feature = new DSObject();
            feature.setValue(new FieldName("rawName"), this.rawName);
            feature.setValue(new FieldName("groupId"), this.groupId);
            feature.setValue(new FieldName("version"), this.version);
            feature.setValue(new FieldName("artifactId"), this.artifactId);

            List<IObject> actorsList = new ArrayList<>();
            for (Actor actor : this.actors) {
                actorsList.add(actor.asIObject());
            }
            feature.setValue(new FieldName("actors"), actorsList);

            List<IObject> pluginsList = new ArrayList<>();
            for (Plugin plugin : this.plugins) {
                pluginsList.add(plugin.asIObject());
            }
            feature.setValue(new FieldName("plugins"), pluginsList);

            List<IObject> repositoriesList = new ArrayList<>();
            for (UploadRepository repository : this.uploadRepositories) {
                repositoriesList.add(repository.asIObject());
            }
            feature.setValue(new FieldName("uploadRepositories"), repositoriesList);

            return feature;
        } catch (ChangeValueException e) {
            throw new Exception("Actor:asIObject - failed.", e);
        }
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(final String rawName) {
        this.rawName = rawName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Project getOwnerProject() {
        return ownerProject;
    }

    public void setOwnerProject(final Project ownerProject) {
        this.ownerProject = ownerProject;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public String getName() {
        return this.rawName;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(final List<Actor> actors) {
        this.actors = actors;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(final List<Plugin> plugins) {
        this.plugins = plugins;
    }

    public String getFeatureDistributionModuleName() {
        return this.rawName + DISTRIBUTION_FEATURE_NAME_POSTFIX;
    }

    public void addOrUpdateActor(final Actor actor) {
        Actor act = this.actors.stream().filter(
                a ->
                        a.getArtifactId().equals(actor.getArtifactId())
        ).findFirst().orElse(null);
        if (null == act) {
            this.actors.add(actor);
        } else {
            act = actor;
        }
    }

    public void removeActor(final Actor actor) {
        this.actors.removeIf(
                a -> a.getArtifactId().equals(actor.getArtifactId())
        );
    }

    public void addOrUpdatePlugin(final Plugin plugin) {
        Plugin pl = this.plugins.stream().filter(
                p ->
                        p.getArtifactId().equals(plugin.getArtifactId())
        ).findFirst().orElse(null);
        if (null == pl) {
            this.plugins.add(plugin);
        } else {
            pl = plugin;
        }
    }

    public void removePlugin(final Plugin plugin) {
        this.plugins.removeIf(
                p -> p.getArtifactId().equals(plugin.getArtifactId())
        );
    }

    public void addOrUpdateUploadRepository(final UploadRepository repository) {
        UploadRepository rep = this.uploadRepositories.stream().filter(
                r ->
                        r.getId().equals(repository.getId())
        ).findFirst().orElse(null);
        if (null == rep) {
            this.uploadRepositories.add(repository);
        } else {
            rep = repository;
        }
    }

    public void removeUploadRepository(final UploadRepository repository) {
        this.uploadRepositories.removeIf(
                r -> r.getId().equals(repository.getId())
        );
    }

    public Path getPath() {
        return Paths.get(
                this.ownerProject.getPath().toString(), ParameterResolver.getModuleDirectoryName(this.rawName)
        ).toAbsolutePath();
    }

    public void makeFeatureDirectory()
            throws Exception {
        if (!Files.exists(this.getPath())) {
            try {
                Files.createDirectories(getPath());
            } catch (IOException e) {
                System.out.println("Could not create feature directory: ");
                System.err.println(e);

                throw new Exception("Feature:makeFeatureDirectory - failed.", e);
            }
        } else {
            System.out.println("Directory for given feature name already exists. May be feature with given name already exists? Please remove directory and try again.");

            throw new Exception("Feature:makeFeatureDirectory - failed.");
        }
    }

    public void makePomFile()
            throws Exception {
        Path featurePomFile = Paths.get(this.getPath().toString(), POM_NAME);
        try {
            File file = new File(POM_TEMPLATE);
            final String projectGroupId = this.ownerProject.getGroupId();
            final String projectName = this.ownerProject.getArtifactId();
            final String projectVersion = this.ownerProject.getVersion();
            final String groupId = this.groupId;
            final String name = this.rawName;
            final String artifactId = this.artifactId;
            final String version = this.version;
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    featurePomFile.toFile(),
                    new HashMap<String, String>() {{
                        put(PROJECT_GROUP_ID_TOKEN, projectGroupId);
                        put(PROJECT_NAME_TOKEN, projectName);
                        put(PROJECT_VERSION_TOKEN, projectVersion);
                        put(FEATURE_GROUP_ID_TOKEN, groupId);
                        put(FEATURE_NAME_TOKEN, artifactId);
                        put(FEATURE_VERSION_TOKEN, version);
                        put(DISTRIBUTION_FEATURE_NAME_TOKEN, name + DISTRIBUTION_FEATURE_NAME_POSTFIX);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of feature pom: ");
            System.err.println(e);

            throw new Exception("Feature:makePomFile - failed.");
        }
    }

    public void makeConfigFile()
            throws Exception {
        Path featureConfigJsonFile = Paths.get(this.getPath().toString(), CONFIG_JSON);
        try {
            File file = new File(CONFIG_JSON_TEMPLATE);
            final String groupId = this.groupId;
            final String name = this.artifactId;
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    featureConfigJsonFile.toFile(),
                    new HashMap<String, String>() {{
                        put(FEATURE_GROUP_ID_TOKEN, groupId);
                        put(FEATURE_NAME_TOKEN, name);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of feature config.json: ");
            System.err.println(e);

            throw new Exception("Feature:makeConfigFile - failed.");
        }
    }

    public void makeConfigFile(final IObject object)
            throws Exception {
        Path featureConfigJsonFile = Paths.get(this.getPath().toString(), CONFIG_JSON);

        try (PrintWriter out = new PrintWriter(featureConfigJsonFile.toFile())) {
            out.println((String) object.serialize());
        } catch (NullPointerException  e) {
            System.out.println("Could not find template of feature config.json: ");
            System.err.println(e);

            throw new Exception("Feature:makeConfigFile - failed.");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find feature config.json: ");
            System.err.println(e);

            throw new Exception("Feature:makeConfigFile - failed.", e);
        }
    }

    public void makeDistributionModule()
            throws Exception {
        Path featureDistributionModuleDir = Paths.get(this.getPath().toString(), this.rawName + DISTRIBUTION_FEATURE_NAME_POSTFIX);
        if (!Files.exists(featureDistributionModuleDir)) {
            try {
                Files.createDirectories(featureDistributionModuleDir);
            } catch (IOException e) {
                System.out.println("Could not create distribution feature directory: ");
                System.err.println(e);

                throw new Exception("Feature:makeDistributionModule - failed.", e);
            }
        } else {
            System.out.println("Directory for given feature name already exists. May be feature with given name already exists? Please remove directory and try again.");

            throw new Exception("Feature:makeDistributionModule - failed.");
        }
        Path featureDistributionBin = Paths.get(this.getPath().toString(), this.rawName + DISTRIBUTION_FEATURE_NAME_POSTFIX, BIN_XML);
        try {
            File file = new File(DISTRIBUTION_BIN_TEMPLATE);
            final String groupId = this.groupId;
            final String name = this.artifactId;
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    featureDistributionBin.toFile(),
                    new HashMap<String, String>() {{
                        put(FEATURE_GROUP_ID_TOKEN, groupId);
                        put(FEATURE_NAME_TOKEN, name);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of feature bin.xml: ");
            System.err.println(e);

            throw new Exception("Feature:makeDistributionModule - failed.");
        }
        Path featureDistributionPom = Paths.get(
                this.getPath().toString(), this.rawName + DISTRIBUTION_FEATURE_NAME_POSTFIX, POM_NAME
        );
        try {
            File file = new File(DISTRIBUTION_POM_TEMPLATE);
            final String groupId = this.groupId;
            final String name = this.artifactId;
            final String version = this.version;
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    featureDistributionPom.toFile(),
                    new HashMap<String, String>() {{
                        put(FEATURE_GROUP_ID_TOKEN, groupId);
                        put(FEATURE_NAME_TOKEN, name);
                        put(FEATURE_VERSION_TOKEN, version);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of feature bin.xml: ");
            System.err.println(e);

            throw new Exception("Feature:makeDistributionModule - failed.");
        }
    }

    public void updateProjectPom()
            throws Exception {
        Path projectPomFile = Paths.get(this.getOwnerProject().getPath().toString(), POM_NAME);
        try {
        final String name = this.rawName;
        FileBuilder.insertTemplateWithReplaceBeforeString(
                projectPomFile.toFile(),
                END_MODULES_TAG,
                new File(MODULE_SECTION_TEMPLATE),
                new HashMap<String, String>() {{
                    put(MODULE_NAME_TOKEN, name);
                }}
        );
        } catch (Throwable e) {
            System.out.println("Could not update project pom file: ");
            System.err.println(e);

            throw new Exception("Feature:updateProjectPom - failed.", e);
        }
    }

    public void addOrUpdateUploadRepositoryToPom(final String repId, final String repUrl)
            throws Exception {
        Path featurePomFile = Paths.get(this.getPath().toString(), POM_NAME);
        PomBuilder.addOrUpdateExecutionSectionToDeployPlugin(
                new File(DEPLOY_PLUGIN_TEMPLATE),
                featurePomFile.toFile(),
                new HashMap<String, String>() {{
                    put(DEPLOY_REPOSITORY_ID_TOKEN, repId);
                    put(DEPLOY_REPOSITORY_URL_TOKEN, repUrl);
                }}
        );
    }

    public void updateVersionInPom(final String newVersion)
            throws Exception {
        Path featurePomFile = Paths.get(this.getPath().toString(), POM_NAME);
        PomBuilder.updateVersion(featurePomFile.toFile(), newVersion);
        for (Actor actor : this.actors) {
            actor.updateParentVersionInPom(newVersion);
        }
        for (Plugin plugin : this.plugins) {
            plugin.updateParentVersionInPom(newVersion);
        }
        Path featureDistributionPom = Paths.get(
                this.getPath().toString(), this.rawName + DISTRIBUTION_FEATURE_NAME_POSTFIX, POM_NAME
        );
        PomBuilder.updateParentVersion(featureDistributionPom.toFile(), newVersion);
    }
}
