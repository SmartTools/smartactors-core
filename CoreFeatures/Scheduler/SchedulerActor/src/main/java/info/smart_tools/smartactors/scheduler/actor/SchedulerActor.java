package info.smart_tools.smartactors.scheduler.actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartupException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.scheduler.actor.wrappers.AddEntryQueryListMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.AddEntryQueryMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.DeleteEntryQueryMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.ListEntriesQueryMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.SetEntryIdMessage;
import info.smart_tools.smartactors.scheduler.actor.wrappers.StartStopMessage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
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
     * @throws ActionExecuteException if error occurs executing service activation action
     */
    public SchedulerActor(final IObject args)
            throws ResolutionException, ReadValueException, EntryStorageAccessException, InvalidArgumentException, ActionExecuteException {
        String connectionOptionsDependency = (String) args.getValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionOptionsDependency"));
        String connectionPoolDependency = (String) args.getValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionPoolDependency"));
        String collectionName = (String) args.getValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "collectionName"));

        Object connectionOptions = IOC.resolve(Keys.getOrAdd(connectionOptionsDependency));
        IPool connectionPool = IOC.resolve(Keys.getOrAdd(connectionPoolDependency), connectionOptions);
        service = IOC.resolve(Keys.getOrAdd("new scheduler service"),
                connectionPool,
                collectionName);

        IAction<ISchedulerService> activationAction = IOC.resolve(
                Keys.getOrAdd("scheduler service activation action for scheduler actor"));
        activationAction.execute(service);
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
        IOC.resolve(Keys.getOrAdd("new scheduler entry"), message.getEntryArguments(), service.getEntryStorage());
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
        ISchedulerEntry entry = IOC.resolve(Keys.getOrAdd("new scheduler entry"), message.getEntryArguments(), service.getEntryStorage());
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
            IOC.resolve(Keys.getOrAdd("new scheduler entry"), entry, service.getEntryStorage());
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
     * @param message    the query message
     * @throws ReadValueException if error occurs reading value from the message
     * @throws EntryStorageAccessException if error occurs accessing entry storage to get or delete the entry
     * @throws EntryScheduleException if error occurs cancelling the entry
     */
    public void deleteEntry(final DeleteEntryQueryMessage message)
            throws ReadValueException, EntryStorageAccessException, EntryScheduleException {
        service.getEntryStorage().getEntry(message.getEntryId()).cancel();
    }

    /**
     * Start the scheduler.
     *
     * @param message    the message
     * @throws ServiceStartupException if error occurs starting the service
     * @throws IllegalServiceStateException if the service is already running/starting
     */
    public void start(final StartStopMessage message)
            throws ServiceStartupException, IllegalServiceStateException {
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
}
