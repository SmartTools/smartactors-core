package info.smart_tools.smartactors.debugger.session_impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerBreakpointsStorage;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerCommand;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSequence;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSession;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.BreakpointStorageException;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.CommandExecutionException;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.InterruptProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a debugger session.
 */
public class DebuggerSessionImpl implements IDebuggerSession {

    private static final int DEFAULT_STACK_DEPTH = 5;

    private final IFieldName sessionIdFieldName;

    private final Map<String, IDebuggerCommand> commands = new HashMap<>();
    private final String id;
    private final Object debuggerAddress;

    private IDebuggerSequence sequence;
    private IMessageProcessor processor;

    private IObject message;
    private IReceiverChain mainChain;

    private boolean paused;
    private boolean prePaused;
    private boolean breakOnException;

    private int stackDepth = DEFAULT_STACK_DEPTH;

    private int stepModeMaxDepth = Integer.MAX_VALUE;

    private final IDebuggerBreakpointsStorage breakpointsStorage;

    /**
     * The constructor.
     *
     * @param id                 identifier of this session
     * @param debuggerAddress    address of the debugger actor
     * @throws ResolutionException if resolution of any dependency fails
     */
    public DebuggerSessionImpl(final String id, final Object debuggerAddress)
            throws ResolutionException {
        this.id = id;
        this.debuggerAddress = debuggerAddress;

        this.breakpointsStorage = IOC.resolve(Keys.getOrAdd(IDebuggerBreakpointsStorage.class.getCanonicalName()));

        sessionIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "sessionId");

        commands.put("start", this::startDebugging);
        commands.put("continue", this::continueDebugging);
        commands.put("processException", this::processException);
        commands.put("stop", this::stopDebugging);
        commands.put("pause", runModeCommand(args -> prePaused = true));

        commands.put("stepMode", this::setStepMode);
        commands.put("getStepMode", args -> stepModeMaxDepth);

        //noinspection ThrowableResultOfMethodCallIgnored
        commands.put("getException", args -> ((sequence == null) ? null : sequence.getException()));
        commands.put("getMessage", args -> message);
        commands.put("getChainName", args -> ((mainChain == null) ? null : mainChain.getName()));
        commands.put("isRunning", args -> isRunning());
        commands.put("isPaused", args -> isPaused());
        commands.put("isCompleted", args -> (sequence != null && sequence.isCompleted()));
        commands.put("getBreakOnException", args -> breakOnException);
        commands.put("getStackTrace", this::getStackTrace);

        commands.put("setMessage", stopModeCommand(args -> message = (IObject) args));
        commands.put("setMessageField", this::setMessageField);
        commands.put("setChain", stopModeCommand(args -> {
            try {
                IChainStorage storage = IOC.resolve(Keys.getOrAdd(IChainStorage.class.getCanonicalName()));
                mainChain = storage.resolve(IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), args));
            } catch (ResolutionException e) {
                throw new CommandExecutionException(e);
            } catch (ChainNotFoundException e) {
                return "NO SUCH CHAIN";
            }

            return "OK";
        }));
        commands.put("setBreakOnException", args -> breakOnException = (Boolean) args);

        commands.put("setStackDepth", stopModeCommand(args -> stackDepth = ((Number) args).intValue()));
        commands.put("getStackDepth", args -> stackDepth);

        commands.put("goTo", pauseModeCommand(this::goTo));
        commands.put("call", pauseModeCommand(this::call));

        commands.put("listBreakpoints", args -> {
            try {
                return breakpointsStorage.listBreakpoints();
            } catch (BreakpointStorageException e) {
                throw new CommandExecutionException(e);
            }
        });

        commands.put("setBreakpoint", args -> {
            try {
                return breakpointsStorage.addBreakpoint((IObject) args);
            } catch (BreakpointStorageException e) {
                throw new CommandExecutionException(e);
            }
        });

        commands.put("modifyBreakpoint", args -> {
            try {
                IObject arg = (IObject) args;
                String bpId = (String) arg.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "id"));

                breakpointsStorage.modifyBreakpoint(bpId, arg);

                return "OK";
            } catch (ResolutionException | BreakpointStorageException | ReadValueException | InvalidArgumentException e) {
                throw new CommandExecutionException(e);
            }
        });
    }

    private void pauseProcessor() throws AsynchronousOperationException {
        processor.pauseProcess();
        paused = true;
        prePaused = false;
    }

    @Override
    public void handleInterrupt(final IMessageProcessor messageProcessor)
            throws InterruptProcessingException {
        if (messageProcessor != processor || !isRunning()) {
            return;
        }

        try {
            if (prePaused
                    || sequence.getCurrentLevel() <= stepModeMaxDepth
                    || sequence.isCompleted()
                    || breakpointsStorage.shouldBreakAt(sequence)) {
                pauseProcessor();
            } else
            //noinspection ThrowableResultOfMethodCallIgnored
            if (sequence.getException() != null) {
                if (breakOnException) {
                    pauseProcessor();
                } else if (!sequence.processException()) {
                    pauseProcessor();
                }
            }
        } catch (AsynchronousOperationException | BreakpointStorageException e) {
            throw new InterruptProcessingException("Error occurred pausing processing of debuggable message.", e);
        }
    }

    @Override
    public Object executeCommand(final String name, final Object args) throws InvalidArgumentException, CommandExecutionException {
        if (!commands.containsKey(name)) {
            throw new InvalidArgumentException(MessageFormat.format("No such debugger command: ''{0}''.", name));
        }

        return commands.get(name).execute(args);
    }

    @Override
    public void close() {
        if (isRunning() || isPaused()) {
            sequence.stop();
        }
    }

    private boolean isRunning() {
        return processor != null && !paused;
    }

    private boolean isPaused() {
        return processor != null && paused;
    }

    private IDebuggerCommand runModeCommand(final IDebuggerCommand command) {
        return args -> {
            if (isRunning()) {
                return command.execute(args);
            } else {
                throw new CommandExecutionException("Not debugging now.");
            }
        };
    }

    private IDebuggerCommand stopModeCommand(final IDebuggerCommand command) {
        return args -> {
            if (!isRunning() && !isPaused()) {
                return command.execute(args);
            } else {
                throw new CommandExecutionException("Debugger is not stopped now.");
            }
        };
    }

    private IDebuggerCommand pauseModeCommand(final IDebuggerCommand command) {
        return args -> {
            if (isPaused()) {
                return command.execute(args);
            } else {
                throw new CommandExecutionException("Debugger is not paused now.");
            }
        };
    }

    private Object startDebugging(final Object arg)
            throws CommandExecutionException {
        if (message == null || mainChain == null || isRunning() || isPaused()) {
            throw new CommandExecutionException("Can not start debugging.");
        }

        try {
            IMessageProcessingSequence innerSequence = IOC.resolve(
                    Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()), stackDepth, mainChain);

            sequence = IOC.resolve(Keys.getOrAdd("new debugger sequence"), innerSequence, debuggerAddress);

            Object taskQueue = IOC.resolve(Keys.getOrAdd("task_queue"));

            processor = IOC.resolve(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()), taskQueue, sequence);

            IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

            context.setValue(sessionIdFieldName, id);

            processor.process(message, context);

            paused = false;
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException | MessageProcessorProcessException e) {
            throw new CommandExecutionException(e);
        }

        return "OK";
    }

    private Object continueDebugging(final Object arg)
            throws CommandExecutionException {
        if (!isPaused()) {
            throw new CommandExecutionException("Not paused now.");
        }

        if (sequence.isCompleted()) {
            throw new CommandExecutionException("Sequence is already completed.");
        }

        try {
            processor.continueProcess(null);
            paused = false;
        } catch (AsynchronousOperationException e) {
            throw new CommandExecutionException(e);
        }

        return "OK";
    }

    private Object processException(final Object arg)
            throws CommandExecutionException {
        //noinspection ThrowableResultOfMethodCallIgnored
        if (!isPaused() || sequence.getException() == null) {
            throw new CommandExecutionException("No exception occurred.");
        }

        if (sequence.processException()) {
            return "OK";
        } else {
            return "FAIL";
        }
    }

    private Object stopDebugging(final Object arg)
            throws CommandExecutionException {
        if (!isPaused() && !isRunning()) {
            throw new CommandExecutionException("Not debugging now.");
        }

        sequence.stop();

        sequence = null;
        processor = null;

        return "OK";
    }

    private Object setStepMode(final Object arg)
            throws CommandExecutionException {
        if ("none".equals(arg)) {
            stepModeMaxDepth = -1;
        } else if (null == arg || "all".equals(arg)) {
            stepModeMaxDepth = Integer.MAX_VALUE;
        } else {
            if (arg instanceof Number) {
                stepModeMaxDepth = ((Number) arg).intValue();
            } else {
                try {
                    stepModeMaxDepth = Integer.parseInt(arg.toString());
                } catch (NumberFormatException e) {
                    throw new CommandExecutionException("Step mode should be \"none\", \"all\" (or null) or a number.");
                }
            }
        }

        return "OK";
    }

    private Object getStackTrace(final Object arg)
            throws CommandExecutionException {
        if (!isPaused()) {
            throw new CommandExecutionException("Not paused now.");
        }

        try {
            return IOC.resolve(Keys.getOrAdd("make dump"), sequence, arg);
        } catch (ResolutionException e) {
            throw new CommandExecutionException(e);
        }
    }

    private Object setMessageField(final Object arg)
            throws CommandExecutionException {
        if (isRunning()) {
            throw new CommandExecutionException("Is not stopped or paused now.");
        }

        try {
            IObject args = (IObject) arg;

            IFieldName fieldNameFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "name");
            IFieldName fieldValueFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "value");
            IFieldName dependencyFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "dependency");

            IFieldName fieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), args.getValue(fieldNameFieldName));
            Object dependencyName = args.getValue(dependencyFieldName);
            Object value = args.getValue(fieldValueFieldName);

            if (dependencyName != null) {
                value = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), dependencyName), value);
            }

            message.setValue(fieldName, value);

            return "OK";
        } catch (ClassCastException | ResolutionException | ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new CommandExecutionException(e);
        }
    }

    private Object goTo(final Object arg)
            throws CommandExecutionException {
        try {
            IObject args = (IObject) arg;

            IFieldName levelFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "level");
            IFieldName stepFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "step");

            int level = ((Number) args.getValue(levelFieldName)).intValue();
            int step = ((Number) args.getValue(stepFieldName)).intValue();

            try {
                sequence.goTo(level, step);
            } catch (InvalidArgumentException e) {
                return e.getMessage();
            }

            return "OK";
        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
            throw new CommandExecutionException(e);
        }
    }

    private Object call(final Object arg)
            throws CommandExecutionException {
        try {
            IChainStorage chainStorage = IOC.resolve(Keys.getOrAdd(IChainStorage.class.getCanonicalName()));
            IReceiverChain chain = chainStorage.resolve(IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), arg));

            sequence.callChain(chain);

            return "OK";
        } catch (NestedChainStackOverflowException e) {
            return "STACK OVERFLOW";
        } catch (ChainNotFoundException e) {
            return "NO SUCH CHAIN";
        } catch (ResolutionException e) {
            throw new CommandExecutionException(e);
        }
    }
}
