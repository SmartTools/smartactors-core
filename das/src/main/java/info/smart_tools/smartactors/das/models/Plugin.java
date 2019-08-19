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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Plugin {

    private static final String POM_NAME = "pom.xml";

    private static final String POM_TEMPLATE = "plugin_pom.template";
    private static final String PLUGIN_CLASS_TEMPLATE = "plugin_class.template";
    private static final String PLUGIN_TEST_CLASS_TEMPLATE = "plugin_test_class.template";
    private static final String MODULE_SECTION_TEMPLATE = "module_section.template";
    private static final String DEPLOY_PLUGIN_TEMPLATE = "plugin_pom_deploy_execution_section.template";

    private static final String FEATURE_GROUP_ID_TOKEN = "${feat.group.id}";
    private static final String FEATURE_NAME_TOKEN = "${feat.name}";
    private static final String FEATURE_VERSION_TOKEN = "${feat.version}";
    private static final String PLUGIN_NAME_TOKEN = "${plugin.name}";
    private static final String PLUGIN_VERSION_TOKEN = "${plugin.version}";
    private static final String PACKAGE_TOKEN = "${package}";
    private static final String MODULE_NAME_TOKEN = "${module.name}";

    private static final String DEPLOY_REPOSITORY_ID_TOKEN = "${upload.repository.id}";
    private static final String DEPLOY_REPOSITORY_URL_TOKEN = "${upload.repository.url}";

    private static final String SRC_MAIN_JAVA = "src/main/java";
    private static final String SRC_TEST_JAVA = "src/test/java";

    private static final String CLASS_FILE_EXTENSION = ".java";

    private static final String TEST_FILE_POSTFIX = "Test";
    private static final String DISTRIBUTION_FEATURE_NAME_POSTFIX = "Distribution";
    private static final String PLUGIN_POSTFIX = "Plugin";

    private static final String START_MODULE_TAG = "<module>";
    private static final String END_MODULE_TAG = "</module>";



    private String rawName;
    private Feature ownerFeature;
    private List<UploadRepository> uploadRepositories = new ArrayList<>();
    private String version;
    private String artifactId;

    public Plugin(final String name, final String version, final Feature feature)
            throws Exception {
        if (null == name) {
            System.out.println("Plugin name could not be null.");
            throw new Exception("Plugin:Constructor - failed.");
        }
        if (null == feature) {
            System.out.println("Initial feature could not be null.");
            throw new Exception("Plugin:Constructor - failed.");
        }
        if (name.endsWith(PLUGIN_POSTFIX)) {
            this.rawName = name;
        } else {
            this.rawName = name + PLUGIN_POSTFIX;
        }
        this.ownerFeature = feature;
        this.artifactId = ParameterResolver.getArtifactId(this.rawName);
        if (null == version || version.isEmpty()) {
            this.version = feature.getVersion();
        } else {
            this.version = version;
        }
    }

    public Plugin(final String name, final String artifactId, final String version, final Feature feature)
            throws Exception {
        if (null == name) {
            System.out.println("Plugin name could not be null.");
            throw new Exception("Plugin:Constructor - failed.");
        }
        if (null == artifactId) {
            System.out.println("Plugin artifactId could not be null.");
            throw new Exception("Plugin:Constructor - failed.");
        }
        if (null == feature) {
            System.out.println("Initial feature could not be null.");
            throw new Exception("Plugin:Constructor - failed.");
        }
        if (name.endsWith(PLUGIN_POSTFIX)) {
            this.rawName = name;
        } else {
            this.rawName = name + PLUGIN_POSTFIX;
        }
        this.ownerFeature = feature;
        this.artifactId = artifactId;
        if (null == version || version.isEmpty()) {
            this.version = feature.getVersion();
        } else {
            this.version = version;
        }
    }

    @SuppressWarnings("unchecked")
    public Plugin(final IObject plugin, final Feature feature)
            throws Exception {
        try {
            this.rawName = (String) plugin.getValue(new FieldName("rawName"));
            this.ownerFeature = feature;
            this.version = (String) plugin.getValue(new FieldName("version"));
            this.artifactId = (String) plugin.getValue(new FieldName("artifactId"));

            List<IObject> repositories = (List<IObject>) plugin.getValue(new FieldName("uploadRepositories"));
            for (IObject repository : repositories) {
                UploadRepository restoredRepository = new UploadRepository(repository);
                this.uploadRepositories.add(restoredRepository);
            }
        } catch (InvalidArgumentException | ReadValueException e) {
            throw new Exception("Plugin:Constructor - failed.", e);
        }
    }

    public IObject asIObject()
            throws Exception {
        try {
            IObject plugin = new DSObject();
            plugin.setValue(new FieldName("rawName"), this.rawName);
            plugin.setValue(new FieldName("version"), this.version);
            plugin.setValue(new FieldName("artifactId"), this.artifactId);

            List<IObject> repositoriesList = new ArrayList<>();
            for (UploadRepository repository : this.uploadRepositories) {
                repositoriesList.add(repository.asIObject());
            }
            plugin.setValue(new FieldName("uploadRepositories"), repositoriesList);

            return plugin;
        } catch (ChangeValueException e) {
            throw new Exception("Plugin:asIObject - failed.", e);
        }
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(final String rawName) {
        this.rawName = rawName;
    }

    public Feature getOwnerFeature() {
        return ownerFeature;
    }

    public void setOwnerFeature(final Feature ownerFeature) {
        this.ownerFeature = ownerFeature;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getArtifactId() {

        return this.artifactId;
    }

    public Path getPath() {
        return Paths.get(
                this.ownerFeature.getPath().toString(), ParameterResolver.getModuleDirectoryName(this.rawName)
        ).toAbsolutePath();
    }

    public String getClassName() {
        return ParameterResolver.getClassName(this.rawName);
    }

    public String getPackageName() {
        return ParameterResolver.getPackageName(
                this.ownerFeature.getGroupId() + "." + this.ownerFeature.getArtifactId() + "." + this.artifactId
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

    public void makePluginDirectory()
            throws Exception {
        if (!Files.exists(this.getPath())) {
            try {
                Files.createDirectories(this.getPath());
            } catch (IOException e) {
                System.out.println("Could not create plugin directory: ");
                System.err.println(e);
                throw new Exception("Plugin:makePluginDirectory - failed.", e);
            }
        } else {
            System.out.println("Directory for given feature name already exists. May be plugin with given name already exists? Please remove directory and try again.");
            throw new Exception("Plugin:makePluginDirectory - failed.");
        }
    }

    public void makePomFile()
            throws Exception {
        Path pluginPomFile = Paths.get(this.getPath().toString(), POM_NAME);
        try {
            final String featureName = this.getOwnerFeature().getArtifactId();
            final String featureGroupId = this.getOwnerFeature().getGroupId();
            final String featureVersion = this.getOwnerFeature().getVersion();
            final String pluginName = this.artifactId;
            final String pluginVersion = this.version;
            File file = new File(POM_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    pluginPomFile.toFile(),
                    new HashMap<String, String>() {{
                        put(FEATURE_GROUP_ID_TOKEN, featureGroupId);
                        put(FEATURE_NAME_TOKEN, featureName);
                        put(FEATURE_VERSION_TOKEN, featureVersion);
                        put(PLUGIN_NAME_TOKEN, pluginName);
                        put(PLUGIN_VERSION_TOKEN, pluginVersion);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of feature pom: ");
            System.err.println(e);
            throw new Exception("Plugin:makePomFile - failed.", e);
        }
    }

    public void makeClassDirectory()
            throws Exception {
        Path pluginClassPath = Paths.get(
                this.getPath().toString(),
                SRC_MAIN_JAVA,
                ParameterResolver.getDirectoryStructure(this.getOwnerFeature().getGroupId()),
                ParameterResolver.getDirectoryStructure(this.getOwnerFeature().getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId)
        );
        if (!Files.exists(pluginClassPath)) {
            try {
                Files.createDirectories(pluginClassPath);
            } catch (IOException e) {
                System.out.println("Could not create plugin class directory: ");
                System.err.println(e);
                throw new Exception("Plugin:makeClassDirectory - failed.", e);
            }
        }
    }

    public void makeClassFile()
            throws Exception {
        Path pluginClassFile = Paths.get(
                this.getPath().toString(),
                SRC_MAIN_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId),
                this.getClassName() + CLASS_FILE_EXTENSION
        );
        try {
            final String packageName = this.getPackageName();
            final String pluginName = this.getClassName();
            File file = new File(PLUGIN_CLASS_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    pluginClassFile.toFile(),
                    new HashMap<String, String>() {{
                        put(PACKAGE_TOKEN, packageName);
                        put(PLUGIN_NAME_TOKEN, pluginName);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of plugin class: ");
            System.err.println(e);

            throw new Exception("Plugin:makeClassFile - failed.", e);
        }
    }

    public void makeTestDirectory()
            throws Exception {
        Path pluginTestClassPath = Paths.get(
                this.getPath().toString(),
                SRC_TEST_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId)
        );
        if (!Files.exists(pluginTestClassPath)) {
            try {
                Files.createDirectories(pluginTestClassPath);
            } catch (IOException e) {
                System.out.println("Could not create plugin test class directory: ");
                System.err.println(e);

                throw new Exception("Plugin:makeTestDirectory - failed.", e);
            }
        }
    }

    public void makeTestClass()
            throws Exception {
        Path pluginTestClassFile = Paths.get(
                this.getPath().toString(),
                SRC_TEST_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId),
                this.getClassName() + TEST_FILE_POSTFIX + CLASS_FILE_EXTENSION
        );
        try {
            final String packageName = this.getPackageName();
            final String className = this.getClassName();
            File file = new File(PLUGIN_TEST_CLASS_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    pluginTestClassFile.toFile(),
                    new HashMap<String, String>() {{
                        put(PACKAGE_TOKEN, packageName);
                        put(PLUGIN_NAME_TOKEN, className + TEST_FILE_POSTFIX);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of plugin test class: ");
            System.err.println(e);

            throw new Exception("Plugin:makeTestClass - failed.", e);
        }
    }

    public void updateFeaturePom()
            throws Exception {
        Path featurePomFile = Paths.get(this.ownerFeature.getPath().toString(), POM_NAME);
        final String moduleName = this.getClassName();
        FileBuilder.insertTemplateWithReplaceBeforeString(
                featurePomFile.toFile(),
                START_MODULE_TAG + this.ownerFeature.getName() + DISTRIBUTION_FEATURE_NAME_POSTFIX + END_MODULE_TAG,
                new File(MODULE_SECTION_TEMPLATE),
                new HashMap<String, String>() {{
                    put(MODULE_NAME_TOKEN, moduleName);
                }}
        );
    }

    public void addOrUpdateUploadRepositoryToPom(final String repId, final String repUrl)
            throws Exception {
        Path pluginPomFile = Paths.get(this.getPath().toString(), POM_NAME);

        PomBuilder.addOrUpdateExecutionSectionToDeployPlugin(
                new File(DEPLOY_PLUGIN_TEMPLATE),
                pluginPomFile.toFile(),
                new HashMap<String, String>() {{
                    put(DEPLOY_REPOSITORY_ID_TOKEN, repId);
                    put(DEPLOY_REPOSITORY_URL_TOKEN, repUrl);
                }}
        );
    }

    public void updateVersionInPom(final String newVersion)
            throws Exception {
        Path pluginPomFile = Paths.get(this.getPath().toString(), POM_NAME);
        PomBuilder.updateVersion(pluginPomFile.toFile(), newVersion);
    }

    public void updateParentVersionInPom(final String newVersion)
            throws Exception {
        Path pluginPomFile = Paths.get(this.getPath().toString(), POM_NAME);
        PomBuilder.updateParentVersion(pluginPomFile.toFile(), newVersion);
    }

}
