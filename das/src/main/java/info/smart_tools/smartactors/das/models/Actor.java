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
import org.apache.maven.model.Dependency;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Actor {

    private static final String POM_NAME = "pom.xml";

    private static final String POM_TEMPLATE = "actor_pom.template";
    private static final String ACTOR_POM_FOR_IMPORT_TEMPLATE = "actor_pom_for_import.template";
    private static final String ACTOR_CLASS_TEMPLATE = "actor_class.template";
    private static final String ACTOR_EXCEPTION_CLASS_TEMPLATE = "actor_exception.template";
    private static final String ACTOR_WRAPPER_CLASS_TEMPLATE = "actor_wrapper.template";
    private static final String ACTOR_TEST_CLASS_TEMPLATE = "actor_test_class.template";
    private static final String MODULE_SECTION_TEMPLATE = "module_section.template";
    private static final String DEPLOY_PLUGIN_TEMPLATE = "actor_pom_deploy_execution_section.template";

    private static final String FEATURE_GROUP_ID_TOKEN = "${feat.group.id}";
    private static final String FEATURE_NAME_TOKEN = "${feat.name}";
    private static final String FEATURE_VERSION_TOKEN = "${feat.version}";
    private static final String ACTOR_NAME_TOKEN = "${actor.name}";
    private static final String ACTOR_VERSION_TOKEN = "${actor.version}";
    private static final String PACKAGE_TOKEN = "${package}";
    private static final String MODULE_NAME_TOKEN = "${module.name}";

    private static final String DEPLOY_REPOSITORY_ID_TOKEN = "${upload.repository.id}";
    private static final String DEPLOY_REPOSITORY_URL_TOKEN = "${upload.repository.url}";

    private static final String SRC_MAIN_JAVA = "src/main/java";
    private static final String SRC_TEST_JAVA = "src/test/java";

    private static final String CLASS_FILE_EXTENSION = ".java";
    private static final String TEMPLATE_FILE_EXTENSION = ".template";

    private static final String EXCEPTION_PACKAGE_NAME = "exception";
    private static final String WRAPPER_PACKAGE_NAME = "wrapper";

    private static final String EXCEPTION_FILE_POSTFIX = "Exception";
    private static final String WRAPPER_FILE_POSTFIX = "Wrapper";
    private static final String TEST_FILE_POSTFIX = "Test";
    private static final String DISTRIBUTION_FEATURE_NAME_POSTFIX = "Distribution";

    private static final String START_MODULE_TAG = "<module>";
    private static final String END_MODULE_TAG = "</module>";

    private String rawName;
    private Feature ownerFeature;
    private List<UploadRepository> uploadRepositories = new ArrayList<>();
    private String version;
    private String artifactId;

    public Actor(final String name, final String version, final Feature feature)
            throws Exception {
        if (null == name) {
            System.out.println("Actor name could not be null.");
            throw new Exception("Actor:Constructor - failed.");
        }
        if (null == feature) {
            System.out.println("Initial feature could not be null.");
            throw new Exception("Actor:Constructor - failed.");
        }
        this.rawName = name;
        this.artifactId = ParameterResolver.getArtifactId(name);
        this.ownerFeature = feature;
        if (null == version || version.isEmpty()) {
            this.version = feature.getVersion();
        } else {
            this.version = version;
        }
    }

    public Actor(final String name, final String artifactId, final String version, final Feature feature)
            throws Exception {
        if (null == name) {
            System.out.println("Actor name could not be null.");
            throw new Exception("Actor:Constructor - failed.");
        }
        if (null == artifactId) {
            System.out.println("Actor artifactId could not be null.");
            throw new Exception("Actor:Constructor - failed.");
        }
        if (null == feature) {
            System.out.println("Initialize feature could not be null.");
            throw new Exception("Actor:Constructor - failed.");
        }
        this.rawName = name;
        this.artifactId = artifactId;
        this.ownerFeature = feature;
        if (null == version || version.isEmpty()) {
            this.version = feature.getVersion();
        } else {
            this.version = version;
        }
    }


    public Actor(final IObject actor, final Feature feature)
            throws Exception {
        try {
            this.rawName = (String) actor.getValue(new FieldName("rawName"));
            this.ownerFeature = feature;
            this.version = (String) actor.getValue(new FieldName("version"));
            this.artifactId = (String) actor.getValue(new FieldName("artifactId"));

            List<IObject> repositories = (List<IObject>) actor.getValue(new FieldName("uploadRepositories"));
            for (IObject repository : repositories) {
                UploadRepository restoredRepository = new UploadRepository(repository);
                this.uploadRepositories.add(restoredRepository);
            }
        } catch (InvalidArgumentException | ReadValueException e) {
            System.out.println("Could not create instance of Actor.");
            throw new Exception("Actor:Constructor - failed.", e);
        }
    }

    public IObject asIObject()
            throws Exception {
        try {
            IObject actor = new DSObject();
            actor.setValue(new FieldName("rawName"), this.rawName);
            actor.setValue(new FieldName("version"), this.version);
            actor.setValue(new FieldName("artifactId"), this.artifactId);

            List<IObject> repositoriesList = new ArrayList<>();
            for (UploadRepository repository : this.uploadRepositories) {
                repositoriesList.add(repository.asIObject());
            }
            actor.setValue(new FieldName("uploadRepositories"), repositoriesList);

            return actor;
        } catch (ChangeValueException e) {
            throw new Exception("Actor:asIObject - failed.", e);
        }
    }

    public String getRawName() {
        return this.rawName;
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

    public void makeActorDirectory()
            throws Exception {
        if (!Files.exists(this.getPath())) {
            try {
                Files.createDirectories(this.getPath());
            } catch (IOException e) {
                System.out.println("Could not create actor directory: ");
                System.out.println(e.getMessage());
                throw new Exception("Actor:makePluginDirectory - failed.", e);
            }
        } else {
            System.out.println("Directory for given feature name already exists. May be actor with given name already exists? Please remove directory and try again.");
            throw new Exception("Actor:makePluginDirectory - failed.");
        }
    }

    public void makePomFile()
            throws Exception {
        Path actorPomFile = Paths.get(this.getPath().toString(), POM_NAME);
        try {
            final String featureName = this.getOwnerFeature().getArtifactId();
            final String featureGroupId = this.getOwnerFeature().getGroupId();
            final String featureVersion = this.getOwnerFeature().getVersion();
            final String actorName = this.artifactId;
            final String actorVersion = this.version;
            File file = new File(POM_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    actorPomFile.toFile(),
                    new HashMap<String, String>() {{
                        put(FEATURE_GROUP_ID_TOKEN, featureGroupId);
                        put(FEATURE_NAME_TOKEN, featureName);
                        put(FEATURE_VERSION_TOKEN, featureVersion);
                        put(ACTOR_NAME_TOKEN, actorName);
                        put(ACTOR_VERSION_TOKEN, actorVersion);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of feature pom: ");
            System.err.println(e);
            throw new Exception("Actor:makePomFile - failed.", e);
        }
    }

    public void makePomForImport(final List<Dependency> dependencies)
            throws Exception {
        Path actorPomFile = Paths.get(this.getPath().toString(), POM_NAME);
        try {
            final String featureArtifactId = this.getOwnerFeature().getArtifactId();
            final String featureGroupId = this.getOwnerFeature().getGroupId();
            final String featureVersion = this.getOwnerFeature().getVersion();
            final String artifactId = this.artifactId;
            final String moduleVersion = this.version;
            File file = new File(ACTOR_POM_FOR_IMPORT_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    actorPomFile.toFile(),
                    new HashMap<String, String>() {{
                        put(FEATURE_GROUP_ID_TOKEN, featureGroupId);
                        put(FEATURE_NAME_TOKEN, featureArtifactId);
                        put(FEATURE_VERSION_TOKEN, featureVersion);
                        put(ACTOR_NAME_TOKEN, artifactId);
                        put(ACTOR_VERSION_TOKEN, moduleVersion);
                    }}
            );
            PomBuilder.addDependencies(actorPomFile.toFile(), dependencies);
        } catch (NullPointerException e) {
            System.out.println("Could not find template of feature pom: ");
            System.err.println(e);
            throw new Exception("Actor:makePomFile - failed.", e);
        }
    }

    public void makeClassDirectory()
            throws Exception {
        Path actorClassPath = Paths.get(
                this.getPath().toString(),
                SRC_MAIN_JAVA,
                ParameterResolver.getDirectoryStructure(this.getOwnerFeature().getGroupId()),
                ParameterResolver.getDirectoryStructure(this.getOwnerFeature().getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId)
        );
        if (!Files.exists(actorClassPath)) {
            try {
                Files.createDirectories(actorClassPath);
            } catch (IOException e) {
                System.out.println("Could not create actor class directory: ");
                System.err.println(e);
                throw new Exception("Actor:makeClassDirectory - failed.", e);
            }
        }
    }

    public void makeClassFile()
            throws Exception {
        Path actorClassFile = Paths.get(
                this.getPath().toString(),
                SRC_MAIN_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId),
                this.getClassName() + CLASS_FILE_EXTENSION
        );
        try {
            final String packageName = this.getPackageName();
            final String actorName = this.getClassName();
            File file = new File(ACTOR_CLASS_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    actorClassFile.toFile(),
                    new HashMap<String, String>() {{
                        put(PACKAGE_TOKEN, packageName);
                        put(ACTOR_NAME_TOKEN, actorName);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of actor class: ");
            System.err.println(e);

            throw new Exception("Actor:makeClassFile - failed.", e);
        }
    }

    public void makeExceptionDirectory()
            throws Exception {
        Path actorExceptionClassPath = Paths.get(
                this.getPath().toString(),
                SRC_MAIN_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId),
                EXCEPTION_PACKAGE_NAME
        );
        if (!Files.exists(actorExceptionClassPath)) {
            try {
                Files.createDirectories(actorExceptionClassPath);
            } catch (IOException e) {
                System.out.println("Could not create actor class directory: ");
                System.err.println(e);

                throw new Exception("Actor:makeExceptionDirectory - failed.", e);
            }
        }
    }

    public void makeExceptionTemplate()
            throws Exception {
        Path actorExceptionClassFile = Paths.get(
                this.getPath().toString(),
                SRC_MAIN_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId),
                EXCEPTION_PACKAGE_NAME,
                this.getClassName() + EXCEPTION_FILE_POSTFIX + TEMPLATE_FILE_EXTENSION
        );
        try {
            final String packageName = this.getPackageName();
            File file = new File(ACTOR_EXCEPTION_CLASS_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    actorExceptionClassFile.toFile(),
                    new HashMap<String, String>() {{
                        put(PACKAGE_TOKEN, packageName);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of actor exception class: ");
            System.err.println(e);

            throw new Exception("Actor:makeExceptionTemplate - failed.", e);
        }
    }

    public void makeWrapperDirectory()
            throws Exception {
        Path actorWrapperClassPath = Paths.get(
                this.getPath().toString(),
                SRC_MAIN_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId),
                WRAPPER_PACKAGE_NAME
        );
        if (!Files.exists(actorWrapperClassPath)) {
            try {
                Files.createDirectories(actorWrapperClassPath);
            } catch (IOException e) {
                System.out.println("Could not create actor class directory: ");
                System.err.println(e);

                throw new Exception("Actor:makeWrapperDirectory - failed.", e);
            }
        }

    }

    public void makeWrapperTemplate()
            throws Exception {
        Path actorWrapperClassFile = Paths.get(
                this.getPath().toString(),
                SRC_MAIN_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId),
                WRAPPER_PACKAGE_NAME,
                this.getClassName() + WRAPPER_FILE_POSTFIX + TEMPLATE_FILE_EXTENSION
        );
        try {
            final String packageName = this.getPackageName();
            File file = new File(ACTOR_WRAPPER_CLASS_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    actorWrapperClassFile.toFile(),
                    new HashMap<String, String>() {{
                        put(PACKAGE_TOKEN, packageName);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of actor wrapper class: ");
            System.err.println(e);

            throw new Exception("Actor:makeWrapperTemplate - failed.", e);
        }
    }

    public void makeTestDirectory()
            throws Exception {
        Path actorTestClassPath = Paths.get(
                this.getPath().toString(),
                SRC_TEST_JAVA,
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getGroupId()),
                ParameterResolver.getDirectoryStructure(this.ownerFeature.getArtifactId()),
                ParameterResolver.getDirectoryStructure(this.artifactId)
        );
        if (!Files.exists(actorTestClassPath)) {
            try {
                Files.createDirectories(actorTestClassPath);
            } catch (IOException e) {
                System.out.println("Could not create actor test class directory: ");
                System.err.println(e);

                throw new Exception("Actor:makeTestDirectory - failed.", e);
            }
        }
    }

    public void makeTestClass()
            throws Exception {
        Path actorTestClassFile = Paths.get(
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
            File file = new File(ACTOR_TEST_CLASS_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    actorTestClassFile.toFile(),
                    new HashMap<String, String>() {{
                        put(PACKAGE_TOKEN, packageName);
                        put(ACTOR_NAME_TOKEN, className + TEST_FILE_POSTFIX);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of actor test class: ");
            System.err.println(e);

            throw new Exception("Actor:makeTestClass - failed.", e);
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
        Path actorPomFile = Paths.get(this.getPath().toString(), POM_NAME);

        PomBuilder.addOrUpdateExecutionSectionToDeployPlugin(
                new File(DEPLOY_PLUGIN_TEMPLATE),
                actorPomFile.toFile(),
                new HashMap<String, String>() {{
                    put(DEPLOY_REPOSITORY_ID_TOKEN, repId);
                    put(DEPLOY_REPOSITORY_URL_TOKEN, repUrl);
                }}
        );
    }

    public void updateVersionInPom(final String newVersion)
            throws Exception {
        Path actorPomFile = Paths.get(this.getPath().toString(), POM_NAME);
        PomBuilder.updateVersion(actorPomFile.toFile(), newVersion);
    }

    public void updateParentVersionInPom(final String newVersion)
            throws Exception {
        Path actorPomFile = Paths.get(this.getPath().toString(), POM_NAME);
        PomBuilder.updateParentVersion(actorPomFile.toFile(), newVersion);
    }
}
