package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Strategy that parses canonical descriptions of {@link IEndpointProfile endpoint profiles}.
 *
 * <p>
 *  This strategy assumes that all named endpoint profiles are registered in IOC with key {@code "endpoint profile"}.
 * </p>
 */
public class ProfileCreationStrategy implements IResolveDependencyStrategy {
    private final IFieldName extendFieldName;
    private final IFieldName pipelinesFieldName;
    private final IFieldName idFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public ProfileCreationStrategy() throws ResolutionException {
        extendFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "extend");
        pipelinesFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "pipelines");
        idFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "id");
    }

    @Override
    public <T> T resolve(final Object... args)
            throws ResolveDependencyStrategyException {
        IObject config = (IObject) args[0];

        try {
            IEndpointProfile profile = new DefaultEndpointProfile(resolveParent(config), parseDescriptions(config));
            return (T) profile;
        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }

    private IEndpointProfile resolveParent(final IObject config)
            throws ReadValueException, InvalidArgumentException, ResolutionException, ResolveDependencyStrategyException {
        List extendList = (List) config.getValue(extendFieldName);
        List<IEndpointProfile> parents = new LinkedList<>();

        for (Object parentName : extendList) {
            parents.add(IOC.resolve(Keys.getOrAdd("endpoint profile"), parentName));
        }

        return new MultiParentEndpointProfile(parents);
    }

    private Map<String, IObject> parseDescriptions(final IObject config)
            throws ReadValueException, InvalidArgumentException, ResolveDependencyStrategyException {
        List pipelinesList = (List) config.getValue(pipelinesFieldName);
        Map<String, IObject> descriptions = new HashMap<>();
        int index = 1;

        for (Object pipelineDesc : pipelinesList) {
            IObject pipelineDescIObject;

            try {
                pipelineDescIObject = (IObject) pipelineDesc;
            } catch (ClassCastException e) {
                throw new ResolveDependencyStrategyException("Pipeline description at index " + index + " is not a object.", e);
            }

            descriptions.put((String) pipelineDescIObject.getValue(idFieldName), pipelineDescIObject);

            ++index;
        }

        return descriptions;
    }
}
