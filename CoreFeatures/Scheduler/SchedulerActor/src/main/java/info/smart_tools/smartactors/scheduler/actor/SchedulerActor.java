package info.smart_tools.smartactors.scheduler.actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.actor.wrappers.AddEntryQueryListMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.AddEntryQueryMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.ConfigureQueryMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.DeleteEntryQueryMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.ListEntriesQueryMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.SetEntryIdMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.StartStopMessage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryNotFoundException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;

import java.util.stream.Collectors;

/**
 * Actor that manages schedules.
 */
public class SchedulerActor {
    private final ISchedulerService service;

    /**
     * The constructor.
     *
     * @param args    constructor arguments
     * @throws ResolutionException if fails to resolve any dependencies
     * @throws ReadValueException if fails to read any value from arguments object
     * @throws EntryStorageAccessException if fails to download entries saved in database
     * @throws InvalidArgumentException if it occurs
     * @throws ActionExecutionException if error occurs executing service activation action
     * @throws UpCounterCallbackExecutionException if the system is shutting down and error occurs executing any callback
     */
    public SchedulerActor(final IObject args)
            throws ResolutionException, ReadValueException, EntryStorageAccessException, InvalidArgumentException, ActionExecutionException,
                   UpCounterCallbackExecutionException {
        String connectionOptionsDependency = (String) args.getValue(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "connectionOptionsDependency"));
        String connectionPoolDependency = (String) args.getValue(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "connectionPoolDependency"));
        String collectionName = (String) args.getValue(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "collectionName"));

        Object connectionOptions = IOC.resolve(Keys.getKeyByName(connectionOptionsDependency));
        IPool connectionPool = IOC.resolve(Keys.getKeyByName(connectionPoolDependency), connectionOptions);
        service = IOC.resolve(Keys.getKeyByName("new scheduler service"),
                connectionPool,
                collectionName);

        IAction<ISchedulerService> activationAction = IOC.resolve(
                Keys.getKeyByName("scheduler service activation action for scheduler actor"));
        activationAction.execute(service);

        IUpCounter upCounter = IOC.resolve(Keys.getKeyByName("root upcounter"));
        upCounter.onShutdownComplete(this.toString(), () -> {
            try {
                service.stop();
            } catch (IllegalServiceStateException ignore) {
                // Service is already stopped, OK
            } catch (ServiceStopException e) {
                throw new ActionExecutionException(e);
            }
        });
        // Execute only entries with "preShutdownExec" flag after shutdown request received
        ISchedulerEntryFilter preShutdownModeFilter = IOC.resolve(Keys.getKeyByName("pre shutdown mode entry filter"));
        upCounter.onShutdownRequest(this.toString(), mode -> service.getEntryStorage().setFilter(preShutdownModeFilter));
    }

    /**
     * Create new entry.
     *
     * @param message    the message
     * @throws ResolutionException if error occurs resolving new entry
     * @throws ReadValueException if error occurs reading value from message
     */
    public void addEntry(final AddEntryQueryMessage message)
            throws ResolutionException, ReadValueException {
        IOC.resolve(Keys.getKeyByName("new scheduler entry"), message.getEntryArguments(), service.getEntryStorage());
    }

    /**
     * Create new entry.
     *
     * @param message    the message
     * @throws ResolutionException if error occurs resolving new entry
     * @throws ReadValueException if error occurs reading value from message
     * @throws ChangeValueException if error occurs setting value from message
     */
    public void addEntryWithSettingId(final SetEntryIdMessage message)
            throws ResolutionException, ReadValueException, ChangeValueException {
        ISchedulerEntry entry = IOC.resolve(Keys.getKeyByName("new scheduler entry"), message.getEntryArguments(), service.getEntryStorage());
        message.setEntryId(entry.getId());
    }

    /**
     * Create list of new entries.
     *
     * @param message    the message
     * @throws ResolutionException if error occurs resolving new entry
     * @throws ReadValueException if error occurs reading value from message
     */
    public void addEntryList(final AddEntryQueryListMessage message)
            throws ResolutionException, ReadValueException {
        for (IObject entry : message.getEntryArgumentsList()) {
            IOC.resolve(Keys.getKeyByName("new scheduler entry"), entry, service.getEntryStorage());
        }
    }

    /**
     * List all locally saved entries.
     *
     * @param message    the query message
     * @throws ChangeValueException if error occurs writing the result
     * @throws EntryStorageAccessException if error occurs accessing entry storage
     */
    public void listEntries(final ListEntriesQueryMessage message)
            throws ChangeValueException, EntryStorageAccessException {
        message.setEntries(
                service.getEntryStorage().listLocalEntries().stream()
                        .map(ISchedulerEntry::getState)
                        .collect(Collectors.toList()));
    }

    /**
     * Delete and cancel a scheduler entry.
     *
     * This handler is idempotent i.e. it has no effect and does not throw any exceptions if the entry is already deleted.
     *
     * @param message    the query message
     * @throws ReadValueException if error occurs reading value from the message
     * @throws EntryStorageAccessException if error occurs accessing entry storage to get or delete the entry
     * @throws EntryScheduleException if error occurs cancelling the entry
     */
    public void deleteEntry(final DeleteEntryQueryMessage message)
            throws ReadValueException, EntryStorageAccessException, EntryScheduleException {
        try {
            service.getEntryStorage().getEntry(message.getEntryId()).cancel();
        } catch (EntryNotFoundException ignore) {
            // Entry is already deleted, OK
        }
    }

    /**
     * Start the scheduler.
     *
     * @param message    the message
     * @throws ServiceStartException if error occurs starting the service
     * @throws IllegalServiceStateException if the service is already running/starting
     */
    public void start(final StartStopMessage message)
            throws ServiceStartException, IllegalServiceStateException {
        service.start();
    }

    /**
     * Stop the scheduler.
     *
     * @param message    the message
     * @throws ServiceStopException if error occurs stopping the service
     * @throws IllegalServiceStateException if the service is already stopped/not running
     */
    public void stop(final StartStopMessage message)
            throws IllegalServiceStateException, ServiceStopException {
        service.stop();
    }

    /**
     * Configure scheduling service.
     *
     * @param message    the message
     * @throws InvalidArgumentException if configuration parameters are invalid
     * @throws ReadValueException if error occurs reading parameters
     */
    public void configure(final ConfigureQueryMessage message)
            throws InvalidArgumentException, ReadValueException {
        service.configure(message.getConfig());
    }
}
