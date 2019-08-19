package info.smart_tools.smartactors.feature_management.feature_creator_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.feature_management.feature.Feature;
import info.smart_tools.smartactors.feature_management.feature_creator_actor.exception.FeatureCreationException;
import info.smart_tools.smartactors.feature_management.feature_creator_actor.wrapper.CreateFeaturesWrapper;
import info.smart_tools.smartactors.feature_management.feature_creator_actor.wrapper.CreateMessageWrapper;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Actor that creates instance of {@link IFeature}
 * by given feature location and by given other feature description.
 */
public class FeaturesCreatorActor {

    private final IFieldName nameFN;
    private final IFieldName groupFN;
    private final IFieldName versionFN;
    private final IFieldName featureLocationFN;

    private final IFieldName featuresFN;
    private final IFieldName repositoriesFN;
    private final IFieldName packageTypeFN;

    private final IFieldName repositoryIdFN;
    private final IFieldName repositoryTypeFN;
    private final IFieldName repositoryUrlFN;

    private static final String END_OF_INPUT_DELIMITER = "\\Z";
    private static final String EXTENSION_SEPARATOR = ".";
    private static final String IOBJECT_FACTORY_STRATEGY_NAME = "info.smart_tools.smartactors.iobject.iobject.IObject";
    private static final String FIELD_NAME_FACTORY_STARTEGY_NAME =
            "info.smart_tools.smartactors.iobject.ifield_name.IFieldName";
    private static final String IOC_FEATURE_REPOSITORY_STORAGE_NAME = "feature-repositories";

    //TODO: this parameters would be took out into the config.json as actor arguments
    private static final String FILENAME_VERSION_PATTERN = "-\\d+\\.\\d+\\.\\d+";
    private static final String FEATURE_VERSION_PATTERN = "\\d+\\.\\d+\\.\\d+";

    private final Map<String, IActionTwoArgs<File, CreateMessageWrapper>> creationFunctions;

    /**
     * Default constructor
     * @throws ResolutionException if any errors occurred on IOC resolution
     */
    public FeaturesCreatorActor()
            throws ResolutionException {
        this.featuresFN =        IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "features");
        this.repositoriesFN =    IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "repositories");
        this.nameFN =            IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "name");
        this.groupFN =           IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "group");
        this.versionFN =         IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "version");
        this.featureLocationFN = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "featureLocation");
        this.packageTypeFN =     IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "packageType");
        this.repositoryIdFN =    IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "repositoryId");
        this.repositoryTypeFN =  IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "type");
        this.repositoryUrlFN =   IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "url");

        //TODO: need refactoring. This actions would be took out to the plugin.
        this.creationFunctions = new HashMap<String, IActionTwoArgs<File, CreateMessageWrapper>>() {{
            put("zip", (f, w) -> {
                try {
                    w.setJsonFeaturesDescription(createJsonFeatureDescriptionByZip(f));
                    w.setJsonRepositoriesDescription(new ArrayList<>());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            put("jar", (f, w) -> {
                try {
                    w.setJsonFeaturesDescription(createJsonFeatureDescriptionByZip(f));
                    w.setJsonRepositoriesDescription(new ArrayList<>());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            put("json",  (f, w) -> {
                try {
                    w.setJsonFeaturesDescription(getProperty(f, featuresFN));
                    w.setJsonRepositoriesDescription(getProperty(f, repositoriesFN));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }};
    }

    /**
     * Gathers feature descriptions and puts them to the message
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws FeatureCreationException if any errors occurred on gathering feature description and on message creation
     */
    public void createMessageByFile(final CreateMessageWrapper wrapper)
            throws FeatureCreationException {
        try {
            File file = Paths.get(wrapper.getObservedDirectory(), wrapper.getFileName()).toFile();
            if (file.exists() && !file.isDirectory()) {

                IActionTwoArgs<File, CreateMessageWrapper> action = this.creationFunctions.get(
                        getExtension(file)
                );
                if (null != action) {
                    action.execute(file, wrapper);
                }
            }
        } catch (ReadValueException | InvalidArgumentException | ActionExecutionException e) {
            throw new FeatureCreationException("Could not create features by given file.", e);
        }
    }

    /**
     * Creates instance of {@link IFeature} by feature descriptions and puts them to the message
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws FeatureCreationException if any errors occurred on feature creation
     */
    public void createFeaturesByMessage(final CreateFeaturesWrapper wrapper)
            throws FeatureCreationException {
        try {
            Map<String, IFeature> features = new HashMap<>();
            List<IObject> repositories = wrapper.getRepositoriesDescription();
            List<IObject> repositoryStorage = IOC.resolve(Keys.getKeyByName(IOC_FEATURE_REPOSITORY_STORAGE_NAME));

            for (IObject repository : repositories) {
                boolean found = false;
                for (IObject stored : repositoryStorage) {
                    if (stored.getValue(this.repositoryIdFN).equals(repository.getValue(this.repositoryIdFN)) &&
                        stored.getValue(this.repositoryTypeFN).equals(repository.getValue(this.repositoryTypeFN)) &&
                        stored.getValue(this.repositoryUrlFN).equals(repository.getValue(this.repositoryUrlFN))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    repositoryStorage.add(repository);
                }
            }
            List<IObject> featuresFromJson = wrapper.getFeaturesDescription();
            IPath directory = new Path(wrapper.getFeatureDirectory());
            if (null != featuresFromJson) {
                for (IObject feature : featuresFromJson) {
                    String name = (String) feature.getValue(this.nameFN);
                    String groupId = (String) feature.getValue(this.groupFN);
                    String version = (String) feature.getValue(this.versionFN);
                    String packageType = (String) feature.getValue(this.packageTypeFN);
                    IPath location = (IPath) feature.getValue(this.featureLocationFN);
                    features.put(name, new Feature(
                            groupId,
                            name,
                            version,
                            null,
                            location,
                            directory,
                            packageType
                    ));
                }
            }

            wrapper.setFeatures(features.values());
        } catch (ResolutionException | ChangeValueException | ReadValueException | InvalidArgumentException e) {
            throw new FeatureCreationException(e);
        }
    }

    private List<IObject> createJsonFeatureDescriptionByZip(final File file)
            throws FeatureCreationException {
        try {
            String packageType = getExtension(file);
            String name = file.getName().split(FILENAME_VERSION_PATTERN)[0];
            Pattern pattern = Pattern.compile(FEATURE_VERSION_PATTERN);
            Matcher matcher = pattern.matcher(file.getName());
            String version = matcher.find() ? matcher.group() : null;
            List<IObject> featuresDescription = new ArrayList<>();
            IObject featureDescription = IOC.resolve(Keys.getKeyByName(IOBJECT_FACTORY_STRATEGY_NAME));
            featureDescription.setValue(this.nameFN, name);
            featureDescription.setValue(this.groupFN, null);
            featureDescription.setValue(this.versionFN, version);
            featureDescription.setValue(this.featureLocationFN, new Path(file.getPath()));
            featureDescription.setValue(this.packageTypeFN, packageType);
            featuresDescription.add(featureDescription);

            return featuresDescription;
        } catch (ChangeValueException | InvalidArgumentException | ResolutionException e) {
            throw new FeatureCreationException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<IObject> getProperty(final File file, final IFieldName fieldName)
            throws FeatureCreationException {
        try {
            IObject jsonConfig = IOC.resolve(
                    Keys.getKeyByName(IOBJECT_FACTORY_STRATEGY_NAME),
                    new Scanner(file).useDelimiter(END_OF_INPUT_DELIMITER).next()
            );
            return (List<IObject>) jsonConfig.getValue(fieldName);
        } catch (ResolutionException | IOException | ReadValueException | InvalidArgumentException e) {
            throw new FeatureCreationException(e);
        }
    }

    private String getExtension(final File f) {
        return f.getName().substring(f.getName().lastIndexOf(EXTENSION_SEPARATOR) + 1);
    }
}
