package info.smart_tools.smartactors.scheduler.actor.impl.actions;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryPauseException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ISchedulerAction scheduler action} that sends a message stored in scheduler entry, but unlike {@link DefaultSchedulerAction}
 * this one pauses entry processing util processing of sent message is completed.
 *
 * <p>
 *     Uses the following entry arguments object fields:
 * </p>
 *
 * <pre>
 *     {
 *         "message": { ... },                      // the message to send, should be serializable even if the entry
 *                                                  // is not saved in remote storage
 *         "setEntryId": "[some field name]",       // name of field of the message where to store id of the entry (optional)
 *         "preShutdownExec": true,                 // true if the entry should be executed even in pre-shutdown state
 *                                                  // (optional, defaults to false)
 *         "chain": "[chain name]",                 // name of receiver chain where to sent the message
 *         "stackDepth": 5                          // depth of message processing sequence stack (optional, defaults to value returned
 *                                                  // by "default_stack_depth" strategy)
 *     }
 * </pre>
 */
public class BlockingMessageSchedulerAction implements ISchedulerAction {
    private final IFieldName messageFN;
    private final IFieldName setEntryIdFN;
    private final IFieldName preShutdownExecFN;
    private final IFieldName chainFN;
    private final IFieldName finalActionsFN;
    private final IFieldName stackDepthFN;
    private final IChainStorage chainStorage;

    public BlockingMessageSchedulerAction()
            throws ResolutionException {
        messageFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "message");
        setEntryIdFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "setEntryId");
        preShutdownExecFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "preShutdownExec");
        chainFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "chain");
        finalActionsFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "finalActions");
        stackDepthFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "stackDepth");
        chainStorage = IOC.resolve(Keys.getKeyByName(IChainStorage.class.getCanonicalName()));
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args) throws SchedulerActionInitializationException {
        try {
            Object message = args.getValue(messageFN);
            Object chainId = args.getValue(chainFN);

            if (message == null || !(message instanceof IObject)) {
                throw new SchedulerActionInitializationException("\"message\" field of arguments should contain a message object", null);
            }

            if (null == chainId) {
                throw new SchedulerActionInitializationException("\"chain\" field of arguments should contain name of the chain", null);
            }

            String entryIdFieldFN = (String) args.getValue(setEntryIdFN);

            if (entryIdFieldFN != null && !entryIdFieldFN.isEmpty()) {
                ((IObject) message).setValue(IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), entryIdFieldFN), entry.getId());
            }

            entry.getState().setValue(messageFN, message);
            entry.getState().setValue(chainFN, chainId);

            Object preShutdownExecConfig = args.getValue(preShutdownExecFN);
            entry.getState().setValue(preShutdownExecFN,
                    (preShutdownExecConfig == null || preShutdownExecConfig == Boolean.FALSE) ? Boolean.FALSE : Boolean.TRUE);

            Number stackDepth = (Number) args.getValue(stackDepthFN);

            if (null == stackDepth) {
                stackDepth = IOC.resolve(Keys.getKeyByName("default_stack_depth"));
            }

            entry.getState().setValue(stackDepthFN, stackDepth);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException | ResolutionException e) {
            throw new SchedulerActionInitializationException("Error occurred copying message from arguments to entry state.", e);
        }
    }

    @Override
    public void execute(final ISchedulerEntry entry) throws SchedulerActionExecutionException {
        try {
            String serializedMessage = ((IObject) entry.getState().getValue(messageFN)).serialize();
            IObject message = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()), serializedMessage);
            IObject context = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));
            List<IAction<IObject>> finalActionsList = new ArrayList<>(1);

            finalActionsList.add(env -> {
                try {
                    entry.unpause();
                } catch (EntryPauseException e) {
                    throw new ActionExecutionException(e);
                }
            });

            context.setValue(finalActionsFN, finalActionsList);

            Object chainId = IOC.resolve(Keys.getKeyByName("chain_id_from_map_name_and_message"), entry.getState().getValue(chainFN), message);
            IReceiverChain chain = chainStorage.resolve(chainId);
            IMessageProcessingSequence sequence = IOC.resolve(Keys.getKeyByName(IMessageProcessingSequence.class.getCanonicalName()),
                    entry.getState().getValue(stackDepthFN), chain);
            Object taskQueue = IOC.resolve(Keys.getKeyByName("task_queue"));
            IMessageProcessor messageProcessor = IOC.resolve(Keys.getKeyByName(IMessageProcessor.class.getCanonicalName()),
                    taskQueue, sequence);

            entry.pause();

            try {
                messageProcessor.process(message, context);
            } catch (MessageProcessorProcessException e) {
                try {
                    // (try to) unpause entry if message processor fails to start
                    entry.unpause();
                } catch (EntryPauseException ee) {
                    e.addSuppressed(ee);
                }

                throw e;
            }
        } catch (ResolutionException | ReadValueException | InvalidArgumentException | ChainNotFoundException | SerializeException
                | ChangeValueException | MessageProcessorProcessException | EntryPauseException e) {
            throw new SchedulerActionExecutionException("Error occurred sending a blocking message.", e);
        }
    }
}
