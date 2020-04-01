package info.smart_tools.smartactors.checkpoint.failure_action;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Strategy for "checkpoint_failure_action" configuration section. That section defines the action that should be executed on the message
 * lost by a checkpoint.
 *
 * <pre>
 *     {
 *         ...
 *         "checkpoint_failure_action": {
 *             "action": "send to chain checkpoint failure action",     // Name of the action dependency
 *
 *                                                                      // Action-specific parameters (example for "send to chain" action):
 *             "targetChain": "myLastResortChain",                      // Chain to send envelope to
 *             "messageField": "message"                                // Field of the envelope where to store the message
 *         }
 *     }
 * </pre>
 */
public class CheckpointFailureActionSectionStrategy implements ISectionStrategy {
    private final IFieldName sectionName;

    private final IFieldName actionNameFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public CheckpointFailureActionSectionStrategy()
            throws ResolutionException {
        sectionName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpoint_failure_action");

        actionNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "action");
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            IObject section = (IObject) config.getValue(sectionName);
            Object actionKeyName = section.getValue(actionNameFieldName);

            if (null == actionKeyName) {
                actionKeyName = "default configurable checkpoint failure action";
            }

            IAction<IObject> action = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), actionKeyName), section);

            IOC.register(Keys.getKeyByName("checkpoint failure action"),
                    new SingletonStrategy(action));
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | RegistrationException e) {
            throw new ConfigurationProcessingException("Error occurred processing checkpoint_failure_action section.", e);
        }
    }

    @Override
    public void onRevertConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            IOC.unregister(Keys.getKeyByName("checkpoint failure action"));
        } catch(DeletionException e) {
            throw new ConfigurationProcessingException("Error occurred while reverting checkpoint_failure_action section.", e);
        } catch (ResolutionException e) { }
    }

    @Override
    public IFieldName getSectionName() {
        return sectionName;
    }
}
