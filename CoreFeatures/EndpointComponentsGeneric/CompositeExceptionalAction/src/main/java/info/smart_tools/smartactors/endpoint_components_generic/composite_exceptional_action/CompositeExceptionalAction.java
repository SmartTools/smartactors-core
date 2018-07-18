package info.smart_tools.smartactors.endpoint_components_generic.composite_exceptional_action;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;

import java.util.Map;

/**
 * Composite exceptional action for exception interceptor.
 *
 * <p>
 *  This action chooses some other action depending on exception class.
 * </p>
 *
 * @param <TCtx>
 */
public class CompositeExceptionalAction<TCtx> implements IBiAction<TCtx, Throwable> {
    private final IBiAction<TCtx, Throwable> defaultAction;
    private final Map<Class<? extends Throwable>, IBiAction<TCtx, Throwable>> classActions;

    /**
     * The constructor.
     *
     * @param defaultAction default action to execute when no matching actions found for he exception or it's causes
     * @param classActions  map from exception class to action to be executed when exception of that class occurs
     */
    public CompositeExceptionalAction(
            final IBiAction<TCtx, Throwable> defaultAction,
            final Map<Class<? extends Throwable>, IBiAction<TCtx, Throwable>> classActions) {
        this.defaultAction = defaultAction;
        this.classActions = classActions;
    }

    @Override
    public void execute(
        final TCtx context,
        final Throwable error)
            throws ActionExecuteException, InvalidArgumentException {
        getAction(error).execute(context, error);
    }

    private IBiAction<TCtx, Throwable> getAction(final Throwable error) {
        if (null == error) {
            return defaultAction;
        }

        Class<? extends Throwable> errorClass = error.getClass();

        IBiAction<TCtx, Throwable> action = classActions.get(errorClass);

        if (null == action) {
            for (Map.Entry<Class<? extends Throwable>, IBiAction<TCtx, Throwable>> entry : classActions.entrySet()) {
                if (entry.getKey().isAssignableFrom(errorClass)) {
                    action = entry.getValue();
                    classActions.put(errorClass, action);
                    return action;
                }
            }
        }

        return action == null ? getAction(error.getCause()) : action;
    }
}
