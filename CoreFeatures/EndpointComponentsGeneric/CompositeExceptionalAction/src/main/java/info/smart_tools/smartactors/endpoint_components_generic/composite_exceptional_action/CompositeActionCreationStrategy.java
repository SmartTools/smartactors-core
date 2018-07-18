package info.smart_tools.smartactors.endpoint_components_generic.composite_exceptional_action;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Strategy that parses configuration describing a {@link CompositeExceptionalAction composite exceptional action}.
 *
 * <p>
 *  Configuration should look like this:
 * </p>
 *
 * <pre>
 *  {
 *      ..
 *      "exceptionClassActions": [
 *          {
 *              "class": ".. exception class ..",
 *              "action": ".. action name ..",
 *              .. action parameters ..
 *          },
 *          .. more actions ..
 *      ],
 *      "defaultAction": {
 *          "action": ".. action name ..",
 *          .. action parameters ..
 *      }
 *  }
 * </pre>
 */
public class CompositeActionCreationStrategy implements IResolveDependencyStrategy {
    private final IFieldName exceptionClassActionsFN, classFN, actionFN, defaultActionFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public CompositeActionCreationStrategy() throws ResolutionException {
        defaultActionFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "defaultAction");
        actionFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "action");
        classFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "class");
        exceptionClassActionsFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "exceptionClassActions");
    }

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        IObject conf = (IObject) args[1];

        try {
            List classActions = (List) conf.getValue(exceptionClassActionsFN);

            Map<Class<? extends Throwable>, IBiAction<?, Throwable>> actionMap = new ConcurrentHashMap<>();

            for (Object actionDesc : classActions) {
                parseClassAction((IObject) actionDesc, actionMap);
            }

            IBiAction<?, Throwable> defaultAction = parseAction((IObject) conf.getValue(defaultActionFN));

            return (T) new CompositeExceptionalAction(defaultAction, actionMap);
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | ClassNotFoundException e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }

    private void parseClassAction(
        final IObject actionDesc,
        final Map<Class<? extends Throwable>, IBiAction<?, Throwable>> actionMap)
            throws ReadValueException, InvalidArgumentException, ResolutionException, ClassNotFoundException {
        String className = (String) actionDesc.getValue(classFN);

        Class<? extends Throwable> clz = getClass().getClassLoader().loadClass(className).asSubclass(Throwable.class);

        actionMap.put(clz, parseAction(actionDesc));
    }

    private IBiAction<?, Throwable> parseAction(final IObject actionDesc)
            throws ReadValueException, InvalidArgumentException, ResolutionException {
        String actionName = (String) actionDesc.getValue(actionFN);

        return IOC.resolve(
                Keys.getOrAdd("exceptional endpoint action"),
                actionName, actionDesc
        );
    }
}
