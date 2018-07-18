package info.smart_tools.smartactors.endpoint_components_generic.endpoints_config_section_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;

/**
 * Configuration section that creates endpoints.
 *
 * <p>
 *  Endpoint's behavior is defined by two main parameters - endpoint skeleton name and endpoint profile name.
 * </p>
 *
 * <p>
 *  Skeleton is a code responsible for endpoint initialization.
 *  Endpoint skeletons are provided by endpoint implementation features and contain procedures that are impossible to
 *  describe using configuration (but still having some configurable parameters).
 * </p>
 *
 * <p>
 *  Endpoint profile is a more configurable part of endpoint.
 *  It consists of pipeline descriptions defined in {@code "endpointProfiles"} configuration section.
 * </p>
 *
 * <pre>
 *  {
 *      "endpoints": [
 *          {
 *              "skeleton": ".. skeleton name ..",
 *              "profile": ".. profile name ..",
 *              ...
 *          },
 *          ...
 *      ]
 *  }
 * </pre>
 */
public class EndpointsConfigSectionStrategy implements ISectionStrategy {
    private final IFieldName sectionName;
    private final IFieldName skeletonFN;
    private final IFieldName profileFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public EndpointsConfigSectionStrategy() throws ResolutionException {
        sectionName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints");
        skeletonFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "skeleton");
        profileFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "profile");
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            List section = (List) config.getValue(sectionName);

            for (Object epConf : section) {
                IObject epConfIObj = (IObject) epConf;

                Object profileId = epConfIObj.getValue(profileFN);
                Object skeletonId = epConfIObj.getValue(skeletonFN);

                IEndpointProfile profile = IOC.resolve(Keys.getOrAdd("endpoint profile"), profileId);

                IEndpointPipelineSet pipelineSet = IOC.resolve(
                        Keys.getOrAdd("create endpoint pipeline set"), profile, epConfIObj);

                IOC.resolve(Keys.getOrAdd("create endpoint"), skeletonId, epConfIObj, pipelineSet);
            }
        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
            throw new ConfigurationProcessingException(e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return sectionName;
    }
}
