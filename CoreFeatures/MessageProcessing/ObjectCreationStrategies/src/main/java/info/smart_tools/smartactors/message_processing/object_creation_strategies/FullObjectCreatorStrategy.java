package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;

import java.util.List;

/**
 * Strategy that resolves the object creator from configuration object.
 *
 * Expects the following format of configuration:
 *
 * <pre>
 *     {
 *         "filters": [                         // Pipeline steps in creation order.
 *                                              // I.e. user object creation goes first and resulting receiver is last.
 *             {
 *                 "dependency": " ... ",       // Name of dependency that will resolve the creator
 *                 ...                          // Individual parameters of pipeline steps
 *             },
 *             ...                              // Other pipeline steps
 *         ]
 *     }
 * </pre>
 *
 * {@link IReceiverObjectCreator Object creator} for each next step of pipeline is resolved with the following parameters:
 *
 * <ul>
 *     <li>Creator from previous step (or {@code null} for first step)</li>
 *     <li>Step parameters object</li>
 *     <li>Configuration object</li>
 * </ul>
 */
public class FullObjectCreatorStrategy implements IStrategy {
    private final IFieldName filtersFieldName;
    private final IFieldName dependencyFieldName;

    public FullObjectCreatorStrategy()
            throws ResolutionException {
        filtersFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "filters");
        dependencyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Object... args) throws StrategyException {
        IObject config = (IObject) args[0];

        try {
            IReceiverObjectCreator creator = null;
            List<IObject> filtersList = (List) config.getValue(filtersFieldName);

            for (IObject fDesc : filtersList) {
                creator = IOC.resolve(
                        IOC.resolve(
                                IOC.getKeyForKeyByNameStrategy(),
                                fDesc.getValue(dependencyFieldName)
                        ),
                        creator,
                        fDesc,
                        config
                );
            }

            return (T) creator;
        } catch (InvalidArgumentException | ReadValueException | ResolutionException e) {
            throw new StrategyException(e);
        }
    }
}
