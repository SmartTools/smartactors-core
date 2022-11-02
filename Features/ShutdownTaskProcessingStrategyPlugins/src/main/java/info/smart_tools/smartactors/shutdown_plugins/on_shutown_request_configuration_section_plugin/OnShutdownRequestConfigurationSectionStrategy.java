package info.smart_tools.smartactors.shutdown_plugins.on_shutown_request_configuration_section_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.List;

/**
 * Strategy for configuration section assigning actions to be executed on shutdown request sent to a instance of {@link IUpCounter
 * upcounter}.
 *
 * <p>
 *     Expected configuration format:
 * </p>
 * <pre>
 *     {
 *         "onShutdownRequest": [
 *             {
 *                 "upcounter":     "root upcounter",                   // Upcounter to subscribe to
 *                 "action":        "..action name..",                  // Name of action to perform on shutdown request
 *                 . . .
 *             },
 *             {
 *                 "upcounter":     "root upcounter",
 *                 "messages": [                                        // Action defaults to sending a list of messages to a specified
 *                                                                      // chain
 *                     {                                                // Messages to send
 *                         . . .
 *                     },
 *                     . . .
 *                 ],
 *                 "chain": "..chain name..",                           // Chain to send messages to
 *                 "modeField": "..field name.."                        // Name of message field where to save shutdown mode object
 *             },
 *             . . .
 *         ]
 *     }
 * </pre>
 */
public class OnShutdownRequestConfigurationSectionStrategy implements ISectionStrategy {
    private final IFieldName sectionNameFieldName;
    private final IFieldName upcounterFieldName;
    private final IFieldName actionFieldName;

    private final Object defaultActionKeyName;

    public OnShutdownRequestConfigurationSectionStrategy(final Object defaultActionKeyName)
            throws ResolutionException {
        this.defaultActionKeyName = defaultActionKeyName;
        sectionNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "onShutdownRequest");
        upcounterFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "upcounter");
        actionFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "action");
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            List<IObject> section = (List) config.getValue(sectionNameFieldName);

            for (IObject obj : section) {
                IUpCounter upCounter = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), obj.getValue(upcounterFieldName)));

                Object actionKeyName = obj.getValue(actionFieldName);

                if (null == actionKeyName) {
                    actionKeyName = defaultActionKeyName;
                }

                upCounter.onShutdownRequest( "cfg-"+actionKeyName,
                        IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), actionKeyName)));
            }
        } catch (ReadValueException | InvalidArgumentException | ClassCastException | ResolutionException
                | UpCounterCallbackExecutionException e) {
            throw new ConfigurationProcessingException(e);
        }
    }

    @Override
    public void onRevertConfig(final IObject config) throws ConfigurationProcessingException {
        ConfigurationProcessingException exception = new ConfigurationProcessingException("Error occurred reverting \"onShutdownRequest\" configuration section.");
        try {
            List<IObject> section = (List) config.getValue(sectionNameFieldName);

            for (IObject obj : section) {
                try {
                    IUpCounter upCounter = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), obj.getValue(upcounterFieldName)));

                    Object actionKeyName = obj.getValue(actionFieldName);

                    if (null == actionKeyName) {
                        actionKeyName = defaultActionKeyName;
                    }

                    upCounter.removeFromShutdownRequest("cfg-" + actionKeyName);
                } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    exception.addSuppressed(e);
                }
            }
        } catch (ReadValueException | InvalidArgumentException | ClassCastException e) {
            exception.addSuppressed(e);
        }
        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
    }

    @Override
    public IFieldName getSectionName() {
        return sectionNameFieldName;
    }

    @Override
    public String getSectionSchema() {
        return null;
    }
}
