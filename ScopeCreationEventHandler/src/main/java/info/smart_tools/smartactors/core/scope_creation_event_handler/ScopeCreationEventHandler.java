package info.smart_tools.smartactors.core.scope_creation_event_handler;

import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;

/**
 * Implementation of {@link IAction}
 *
 */
public class ScopeCreationEventHandler implements IAction<IScope> {

    /**
     * ID of current handler
     */
    private IKey id;

    /**
     * Constructor by unique handler identifier
     * @param id unique handler identifier
     * @throws InvalidArgumentException if any errors occurred
     */
    public ScopeCreationEventHandler(final IKey id)
            throws InvalidArgumentException {
        if (null == id) {
            throw new InvalidArgumentException("Key should not be null.");
        }
        this.id = id;
    }

    /**
     * Add instance of {@link IStrategyContainer}
     * @param createdScope instance of {@link IScope}
     * @throws ActionExecuteException if any errors occurred
     * @throws InvalidArgumentException if incoming argument are null
     */
    @Override
    public void execute(final IScope createdScope)
            throws ActionExecuteException, InvalidArgumentException {
        if (null == createdScope) {
            throw new InvalidArgumentException("Argument should not be null.");
        }
        try {
            IStrategyContainer container = new StrategyContainer();
            createdScope.setValue(this.id, container);
        } catch (Exception e) {
            throw new ActionExecuteException("Execution error.", e);
        }
    }
}
