package info.smart_tools.smartactors.das.models;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.das.utilities.FileBuilder;
import info.smart_tools.smartactors.das.utilities.JsonFile;
import info.smart_tools.smartactors.das.utilities.ParameterResolver;
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

public class Project {

    private static final String POM_NAME = "pom.xml";

    private static final String POM_TEMPLATE = "project_pom.template";

    private static final String PROJECT_NAME_TOKEN = "${proj.name}";
    private static final String PROJECT_GROUP_ID_TOKEN = "${proj.group.id}";
    private static final String PROJECT_VERSION_TOKEN = "${proj.version}";

    private static final String PROJECT_METADATA_FILENAME = "das.data";

    private String name;
    private String version;
    private String groupId;
    private List<Feature> features = new ArrayList<>();
    private Path path;
    private List<UploadRepository> featureOnCreationUploadRepositories = new ArrayList<>();
    private List<UploadRepository> pluginOnCreationUploadRepositories = new ArrayList<>();
    private List<UploadRepository> actorOnCreationUploadRepositories = new ArrayList<>();


    public Project(final String name, final String groupId, final String version, final Path path)
            throws InvalidArgumentException {
        try {
            this.groupId = groupId;
            this.name = name;
            this.version = version;
            this.path = Paths.get(path.toString(), this.name);
        } catch (Throwable e) {
            throw new InvalidArgumentException("Invalid argument(s). Please use follows structure: groupId:projectName:version.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Project(final IObject project, final Path path)
            throws Exception {
        try {
            this.name = (String) project.getValue(new FieldName("name"));
            this.groupId = (String) project.getValue(new FieldName("groupId"));
            this.version = (String) project.getValue(new FieldName("version"));
            this.path = path;

            List<IObject> restoredFeatures = (List<IObject>) project.getValue(new FieldName("features"));
            for (IObject feature : restoredFeatures) {
                Feature restoredFeature = new Feature(feature, this);
                this.features.add(restoredFeature);
            }

            List<IObject> restoredUploadFeatureRepositories = (List<IObject>) project.getValue(new FieldName("on-feature-creation-upload-repositories"));
            for (IObject repository : restoredUploadFeatureRepositories) {
                UploadRepository restoredRepository = new UploadRepository(repository);
                this.featureOnCreationUploadRepositories.add(restoredRepository);
            }

            List<IObject> restoredUploadActorRepositories = (List<IObject>) project.getValue(new FieldName("on-actor-creation-upload-repositories"));
            for (IObject repository : restoredUploadActorRepositories) {
                UploadRepository restoredRepository = new UploadRepository(repository);
                this.actorOnCreationUploadRepositories.add(restoredRepository);
            }

            List<IObject> restoredUploadPluginRepositories = (List<IObject>) project.getValue(new FieldName("on-plugin-creation-upload-repositories"));
            for (IObject repository : restoredUploadPluginRepositories) {
                UploadRepository restoredRepository = new UploadRepository(repository);
                this.pluginOnCreationUploadRepositories.add(restoredRepository);
            }
        } catch (InvalidArgumentException | ReadValueException e) {
            throw new Exception("Feature:Constructor - failed.", e);
        }
    }

    public IObject asIObject()
            throws Exception {
        try {
            IObject project = new DSObject();
            project.setValue(new FieldName("name"), this.name);
            project.setValue(new FieldName("groupId"), this.groupId);
            project.setValue(new FieldName("version"), this.version);

            List<IObject> featuresList = new ArrayList<>();
            for (Feature feature : this.features) {
                featuresList.add(feature.asIObject());
            }
            project.setValue(new FieldName("features"), featuresList);

            List<IObject> featureUploadRepositoriesList = new ArrayList<>();
            for (UploadRepository repository : this.featureOnCreationUploadRepositories) {
                featureUploadRepositoriesList.add(repository.asIObject());
            }
            project.setValue(new FieldName("on-feature-creation-upload-repositories"), featureUploadRepositoriesList);

            List<IObject> actorUploadRepositoriesList = new ArrayList<>();
            for (UploadRepository repository : this.actorOnCreationUploadRepositories) {
                actorUploadRepositoriesList.add(repository.asIObject());
            }
            project.setValue(new FieldName("on-actor-creation-upload-repositories"), actorUploadRepositoriesList);

            List<IObject> pluginUploadRepositoriesList = new ArrayList<>();
            for (UploadRepository repository : this.pluginOnCreationUploadRepositories) {
                pluginUploadRepositoriesList.add(repository.asIObject());
            }
            project.setValue(new FieldName("on-plugin-creation-upload-repositories"), pluginUploadRepositoriesList);

            return project;
        } catch (ChangeValueException e) {
            throw new Exception("Actor:asIObject - failed.", e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getArtifactId() {
        return ParameterResolver.getArtifactId(this.name);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(final List<Feature> features) {
        this.features = features;
    }

    public List<UploadRepository> getFeatureOnCreationUploadRepositories() {
        return featureOnCreationUploadRepositories;
    }

    public void setFeatureOnCreationUploadRepositories(final List<UploadRepository> featureOnCreationUploadRepositories) {
        this.featureOnCreationUploadRepositories = featureOnCreationUploadRepositories;
    }

    public List<UploadRepository> getPluginOnCreationUploadRepositories() {
        return pluginOnCreationUploadRepositories;
    }

    public void setPluginOnCreationUploadRepositories(final List<UploadRepository> pluginOnCreationUploadRepositories) {
        this.pluginOnCreationUploadRepositories = pluginOnCreationUploadRepositories;
    }

    public List<UploadRepository> getActorOnCreationUploadRepositories() {
        return actorOnCreationUploadRepositories;
    }

    public void setActorOnCreationUploadRepositories(final List<UploadRepository> actorOnCreationUploadRepositories) {
        this.actorOnCreationUploadRepositories = actorOnCreationUploadRepositories;
    }

    public void addOrUpdateFeature(final Feature feature) {
        Feature feat = this.features.stream().filter(
                f ->
                        f.getArtifactId().equals(feature.getArtifactId()) &&
                        f.getGroupId().equals(feature.getGroupId())
        ).findFirst().orElse(null);
        if (null == feat) {
            this.features.add(feature);
        } else {
            feat = feature;
        }
    }

    public void removeFeature(final Feature feature) {
        this.features.removeIf(
                f ->
                        f.getArtifactId().equals(feature.getArtifactId()) &&
                        f.getGroupId().equals(feature.getGroupId())
        );
    }

    public void addOrUpdateFeatureUploadRepository(final UploadRepository repository) {
        UploadRepository rep = this.featureOnCreationUploadRepositories.stream().filter(
                r ->
                        r.getId().equals(repository.getId())
        ).findFirst().orElse(null);
        if (null == rep) {
            this.featureOnCreationUploadRepositories.add(repository);
        } else {
            rep = repository;
        }
    }

    public void removeFeatureUploadRepository(final UploadRepository repository) {
        this.featureOnCreationUploadRepositories.removeIf(
                r -> r.getId().equals(repository.getId()));
    }

    public void addOrUpdateActorUploadRepository(final UploadRepository repository) {
        UploadRepository rep = this.actorOnCreationUploadRepositories.stream().filter(
                r ->
                        r.getId().equals(repository.getId())
        ).findFirst().orElse(null);
        if (null == rep) {
            this.actorOnCreationUploadRepositories.add(repository);
        } else {
            rep = repository;
        }
    }

    public void removeActorUploadRepository(final UploadRepository repository) {
        this.actorOnCreationUploadRepositories.removeIf(
                r -> r.getId().equals(repository.getId()));
    }

    public void addOrUpdatePluginUploadRepository(final UploadRepository repository) {
        UploadRepository rep = this.pluginOnCreationUploadRepositories.stream().filter(
                r ->
                        r.getId().equals(repository.getId())
        ).findFirst().orElse(null);
        if (null == rep) {
            this.pluginOnCreationUploadRepositories.add(repository);
        } else {
            rep = repository;
        }
    }

    public void removePluginUploadRepository(final UploadRepository repository) {
        this.pluginOnCreationUploadRepositories.removeIf(
                r -> r.getId().equals(repository.getId()));
    }

    public Path getPath() {
        return this.path;
    }

    public void setPath(final Path path) {
        this.path = path;
    }

    public void saveMetaDataFile()
            throws Exception {
        Path projectMetaDataFile = Paths.get(this.getPath().toString(), PROJECT_METADATA_FILENAME);
        try {
            JsonFile.save(projectMetaDataFile.toFile(), this.asIObject());
        } catch (InvalidArgumentException | ChangeValueException e) {
            System.out.println("Could not generate project meta data: ");
            System.err.println(e);
            System.out.println("Project creation has been failed.");

            throw new Exception("Project:saveMetaDataFile - failed.", e);
        }
    }

    public void makeProjectDirectory()
            throws Exception {
        Path projectPath = this.getPath().toAbsolutePath();
        if (!Files.exists(projectPath)) {
            try {
                Files.createDirectories(projectPath);
            } catch (IOException e) {
                System.out.println("Could not create project directory: ");
                System.err.println(e);
                System.out.println("Project creation has been failed.");

                throw new Exception("Project:makeProjectDirectory - failed.", e);
            }
        }
    }

    public void makePomFile()
            throws Exception {
        Path projectPomFile = Paths.get(this.getPath().toString(), POM_NAME);
        try {
            final String groupId = this.groupId;
            final String name = this.getArtifactId();
            final String version = this.version;
            File file = new File(POM_TEMPLATE);
            FileBuilder.createFileByTemplateWithReplace(
                    file,
                    projectPomFile.toFile(),
                    new HashMap<String, String>() {{
                        put(PROJECT_GROUP_ID_TOKEN, groupId);
                        put(PROJECT_NAME_TOKEN, name);
                        put(PROJECT_VERSION_TOKEN, version);
                    }}
            );
        } catch (NullPointerException e) {
            System.out.println("Could not find template of project pom : ");
            System.err.println(e);
            System.out.println("Project creation has been failed.");

            throw new Exception("Project:makePomFile - failed.", e);
        }
    }
}
