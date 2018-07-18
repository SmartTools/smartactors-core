package info.smart_tools.smartactors.endpoint_components_generic.endpoint_profiles_configuration_section;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;

/**
 * Strategy for configuration section containing endpoint profile description. Expects the following format:
 *
 * <pre>
 *  {
 *      "endpointProfiles": [
 *          {
 *              "id": "profile-id",
 *              // All the rest canonical profile description goes here
 *              ...
 *          },
 *          ...
 *      ]
 *  }
 * </pre>
 *
 * <p>
 *  Endpoint profiles replace previously defined profiles with the same identifier.
 *  But when a profile is described as extending some other profile it will keep extending the profile that had that
 *  identifier at moment of it's creation.
 *  I.e. in the following example the second profile will extend the first one even after the third one is created so
 *  there is no dependency loop:
 * </p>
 *
 * <pre>
 *  {
 *      "endpointProfiles": [
 *          {
 *              "id": "1",
 *              "extend": [],
 *              ...
 *          },
 *          {
 *              "id": "2",
 *              "extend": ["1"],
 *              ...
 *          },
 *          {
 *              "id": "1",
 *              "extend": ["2"],
 *              ...
 *          },
 *          ...
 *      ]
 *  }
 * </pre>
 *
 * <p>
 *  A profile may also extend the previous profile that had the same identifier:
 * </p>
 *
 * <pre>
 *  {
 *      "endpointProfiles": [
 *          {
 *              "id": "1",
 *              "extend": [],
 *              ...
 *          },
 *          {
 *              "id": "1",
 *              "extend": ["1"],
 *              ...
 *          }
 *      ]
 *  }
 * </pre>
 */
public class EndpointProfilesConfigurationSectionStrategy implements ISectionStrategy {
    private final IFieldName endpointProfilesFieldName;
    private final IFieldName idFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public EndpointProfilesConfigurationSectionStrategy() throws ResolutionException {
        endpointProfilesFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpointProfiles");
        idFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "id");
    }

    @Override
    public void onLoadConfig(final IObject config)
            throws ConfigurationProcessingException {
        try {
            IAdditionDependencyStrategy profilesStrategy = IOC.resolve(Keys.getOrAdd("expandable_strategy#endpoint profile"));

            List section = (List) config.getValue(endpointProfilesFieldName);

            for (Object description : section) {
                IObject descriptionIObject = (IObject) description;
                Object id = descriptionIObject.getValue(idFieldName);

                IEndpointProfile profile = IOC.resolve(Keys.getOrAdd("parse endpoint profile"), descriptionIObject);
                profilesStrategy.register(id, new SingletonStrategy(profile));
            }
        } catch (ResolutionException | ReadValueException | InvalidArgumentException | AdditionDependencyStrategyException e) {
            throw new ConfigurationProcessingException(e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return endpointProfilesFieldName;
    }
}
