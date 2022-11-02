package info.smart_tools.smartactors.message_processing.message_processing_sequence;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.dumpable_interface.idumpable.IDumpable;
import info.smart_tools.smartactors.dumpable_interface.idumpable.exceptions.DumpException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NoExceptionHandleChainException;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IMessageProcessingSequence}.
 */
public class MessageProcessingSequence implements IMessageProcessingSequence, IDumpable {
    private final IChainStorage chainStorage;
    private final IKey chainIdStrategyKey;
    private final IReceiverChain[] chainStack;
    private final int[] stepStack;
    private final Boolean[] scopeSwitchingStack;
    private IMessageReceiver currentReceiver;
    private IObject currentArguments;
    private IObject message;
    private int stackIndex;
    private Object scopeSwitchingChainName;
    private final IScope[] scopeStack;
    private final IModule[] moduleStack;

    private boolean isException;
    private IAction<IMessageProcessingSequence> afterExceptionAction;

    private final IFieldName causeLevelFieldName;
    private final IFieldName causeStepFieldName;
    private final IFieldName catchLevelFieldName;
    private final IFieldName catchStepFieldName;
    private final IFieldName exceptionFieldName;
    private final IFieldName chainFieldName;
    private final IFieldName afterExceptionActionFieldName;
    private final IFieldName stepsStackFieldName;
    private final IFieldName chainsStackFieldName;
    private final IFieldName scopeSwitchingStackFieldName;
    private final IFieldName scopeSwitchingFieldName;
    private final IFieldName maxDepthFieldName;
    private final IFieldName externalAccessFieldName;
    private final IFieldName fromExternalFieldName;
    private final IFieldName accessForbiddenFieldName;

    {
        IKey iFieldNameStrategyKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

        causeLevelFieldName = IOC.resolve(iFieldNameStrategyKey, "causeLevel");
        causeStepFieldName = IOC.resolve(iFieldNameStrategyKey, "causeStep");
        catchLevelFieldName = IOC.resolve(iFieldNameStrategyKey, "catchLevel");
        catchStepFieldName = IOC.resolve(iFieldNameStrategyKey, "catchStep");
        exceptionFieldName = IOC.resolve(iFieldNameStrategyKey, "exception");
        this.afterExceptionActionFieldName = IOC.resolve(iFieldNameStrategyKey, "after");
        this.chainFieldName = IOC.resolve(iFieldNameStrategyKey, "chain");

        stepsStackFieldName = IOC.resolve(iFieldNameStrategyKey, "stepsStack");
        chainsStackFieldName = IOC.resolve(iFieldNameStrategyKey, "chainsStack");
        scopeSwitchingStackFieldName = IOC.resolve(iFieldNameStrategyKey, "scopeSwitchingStack");
        scopeSwitchingFieldName = IOC.resolve(iFieldNameStrategyKey, "scopeSwitching");
        maxDepthFieldName = IOC.resolve(iFieldNameStrategyKey, "maxDepth");
        externalAccessFieldName = IOC.resolve(iFieldNameStrategyKey, "externalAccess");
        fromExternalFieldName = IOC.resolve(iFieldNameStrategyKey, "fromExternal");
        accessForbiddenFieldName = IOC.resolve(iFieldNameStrategyKey, "accessToChainForbiddenError");

        chainIdStrategyKey = Keys.getKeyByName("chain_id_from_map_name_and_message");
        chainStorage = IOC.resolve(Keys.getKeyByName(IChainStorage.class.getCanonicalName()));
        scopeSwitchingChainName = null;
        stackIndex = -1;
        isException = false;
        afterExceptionAction = null;
    }

    /**
     * The constructor.
     *
     * @param stackDepth maximum depth of stack of nested chains
     * @param mainChainName the {@link IReceiverChain} to start message processing with
     * @param message to resolve chains in sequence
     * @throws InvalidArgumentException if stack depth is not a positive number
     * @throws InvalidArgumentException if main chain is {@code null}
     * @throws InvalidArgumentException if main chain contains no receivers
     * @throws ResolutionException if resolution of any dependencies fails
     */
    public MessageProcessingSequence(final int stackDepth, final Object mainChainName, final IObject message, final boolean switchScopeOnStartup)
            throws InvalidArgumentException, ResolutionException, ChainNotFoundException {

        this.message = message;

        if (stackDepth < 1) {
            throw new InvalidArgumentException("Chain stack depth should be a positive number.");
        }
        this.chainStack = new IReceiverChain[stackDepth];
        this.stepStack = new int[stackDepth];
        this.scopeSwitchingStack = new Boolean[stackDepth];
        this.scopeStack = new IScope[stackDepth];
        this.moduleStack = new IModule[stackDepth];

        if (null == mainChainName) {
            throw new InvalidArgumentException("Main chain should not be null.");
        }

        if (switchScopeOnStartup) {
            setScopeSwitchingChainName(mainChainName);
        }
        try {
            callChain(mainChainName);
        } catch(NestedChainStackOverflowException | ScopeProviderException e) {}

        if (!next()) {
            throw new InvalidArgumentException("Main chain should contain at least one receiver.");
        }
    }

    /**
     * The constructor.
     *
     * @param dump the of sequence to recover from
     * @param message to resolve chains in sequence
     * @throws InvalidArgumentException if stack depth is not a positive number
     * @throws InvalidArgumentException if main chain is {@code null}
     * @throws InvalidArgumentException if main chain contains no receivers
     * @throws ResolutionException if resolution of any dependencies fails
     */
    public MessageProcessingSequence(final IObject dump, final IObject message)
            throws InvalidArgumentException, ResolutionException, ReadValueException, ChainNotFoundException {

        this.message = message;

        int stackDepth = ((Number) dump.getValue(maxDepthFieldName)).intValue();
        if (stackDepth < 1) {
            throw new InvalidArgumentException("Chain stack depth should be a positive number.");
        }
        this.chainStack = new IReceiverChain[stackDepth];
        this.stepStack = new int[stackDepth];
        this.scopeSwitchingStack = new Boolean[stackDepth];
        this.scopeStack = new IScope[stackDepth];
        this.moduleStack = new IModule[stackDepth];

        Iterator stepStack = ((Collection) dump.getValue(stepsStackFieldName)).iterator();
        Iterator chainsStack = ((Collection) dump.getValue(chainsStackFieldName)).iterator();
        Iterator scopeSwitchingStack = ((Collection) dump.getValue(scopeSwitchingStackFieldName)).iterator();

        int level = 0;

        while (stepStack.hasNext()) {
            Object chainName = chainsStack.next();
            if ((Boolean) scopeSwitchingStack.next()) {
                setScopeSwitchingChainName(chainName);
            }
            try {
                callChain(chainName);
            } catch(NestedChainStackOverflowException | ScopeProviderException e) {}
            int pos = ((Number) stepStack.next()).intValue();
            uncheckedGoTo(level++, pos + 1);
        }

        next();
    }

    private IReceiverChain resolveChain(final Object chainName)
            throws ChainNotFoundException {
        try {
            Object chainId = IOC.resolve(chainIdStrategyKey, chainName, message);
            return chainStorage.resolve(chainId);
        } catch (ResolutionException ex) {
            throw new ChainNotFoundException(chainName, ex);
        }
    }

    /* Call it carefully since it does not change scope and module context */
    private void uncheckedGoTo(final int level, final int step) {
        this.stackIndex = level;
        this.stepStack[level] = step - 1;
    }

    @Override
    public void reset() {
        this.stepStack[0] = 0;
        this.currentReceiver = chainStack[0].get(0);
        this.currentArguments = chainStack[0].getArguments(0);
        this.stackIndex = 0;
        try {
            if (scopeSwitchingStack[0]) {
                ModuleManager.setCurrentModule(chainStack[0].getModule());
                ScopeProvider.setCurrentScope(chainStack[0].getScope());
            } else {
                ModuleManager.setCurrentModule(moduleStack[0]);
                ScopeProvider.setCurrentScope(scopeStack[0]);
            }
        } catch (ScopeProviderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean next() {
        while (stackIndex >= 0) {
            int step = ++stepStack[stackIndex];
            currentReceiver = chainStack[stackIndex].get(step);

            if (null != currentReceiver) {
                currentArguments = chainStack[stackIndex].getArguments(step);
                return true;
            }

            if (this.isException) {
                this.isException = false;
                try {
                    this.afterExceptionAction.execute(this);
                } catch (Throwable e) {
                    ModuleManager.setCurrentModule(moduleStack[0]);
                    try {
                        ScopeProvider.setCurrentScope(scopeStack[0]);
                    } catch (ScopeProviderException e1) {
                        throw new RuntimeException(e1);
                    }
                    return false;
                }
            }

            if(scopeSwitchingStack[stackIndex]) {
                ModuleManager.setCurrentModule(moduleStack[stackIndex]);
                try {
                    ScopeProvider.setCurrentScope(scopeStack[stackIndex]);
                } catch (ScopeProviderException e) {
                    throw new RuntimeException(e);
                }
            }

            --stackIndex;
        }

        stackIndex = 0;
        return false;
    }

    @Override
    public void goTo(final int level, final int step) throws InvalidArgumentException {
        if (level > stackIndex || level < 0) {
            throw new InvalidArgumentException("Invalid level value");
        }

        int lastScopeSwitchingLevel = -1;
        for(int idx=0; idx <= level; idx++) {
            if (scopeSwitchingStack[idx]) {
                lastScopeSwitchingLevel = idx;
            }
        }
        try {
            if (lastScopeSwitchingLevel > -1) {
                ModuleManager.setCurrentModule(chainStack[lastScopeSwitchingLevel].getModule());
                ScopeProvider.setCurrentScope(chainStack[lastScopeSwitchingLevel].getScope());
            } else {
                ModuleManager.setCurrentModule(moduleStack[0]);
                ScopeProvider.setCurrentScope(scopeStack[0]);
            }
        } catch (ScopeProviderException e) {
            throw new RuntimeException(e);
        }

        uncheckedGoTo(level, step);
    }

    @Override
    public void end() {
        uncheckedGoTo(0, -1);
    }

    @Override
    public int getCurrentLevel() {
        return stackIndex;
    }

    @Override
    public int getStepAtLevel(final int level) throws InvalidArgumentException {
        if (level < 0 || level > stackIndex) {
            throw new InvalidArgumentException("Level index is out of range.");
        }

        return stepStack[level];
    }

    @Override
    public IMessageReceiver getCurrentReceiver() {
        return currentReceiver;
    }

    @Override
    public IObject getCurrentReceiverArguments() {
        return currentArguments;
    }

    @Override
    public void setScopeSwitchingChainName(Object chainName) { scopeSwitchingChainName = chainName; }

    @Override
    public void callChainSecurely(final Object chainName, IMessageProcessor processor)
            throws NestedChainStackOverflowException, ResolutionException, ChainNotFoundException,
            ChainChoiceException, ScopeProviderException {

        IReceiverChain chain = resolveChain(chainName);
        checkAccess(chain, processor);
        putChainToStack(chain);
    }

    @Override
    public void callChain(final Object chainName)
            throws NestedChainStackOverflowException, ResolutionException, ChainNotFoundException, ScopeProviderException {

        IReceiverChain chain = resolveChain(chainName);
        putChainToStack(chain);
    }

    private void putChainToStack(IReceiverChain chain)
            throws NestedChainStackOverflowException, ScopeProviderException {
        int newStackIndex = stackIndex + 1;

        if (newStackIndex >= chainStack.length) {
            throw new NestedChainStackOverflowException(
                    Arrays.copyOf(chainStack, stackIndex + 1),
                    Arrays.copyOf(stepStack, stackIndex + 1));
        }

        chainStack[newStackIndex] = chain;
        stepStack[newStackIndex] = -1;
        scopeStack[newStackIndex] = ScopeProvider.getCurrentScope();
        moduleStack[newStackIndex] = ModuleManager.getCurrentModule();

        scopeSwitchingStack[newStackIndex] = chain.getName().equals(scopeSwitchingChainName);
        if (scopeSwitchingStack[newStackIndex]) {
            ScopeProvider.setCurrentScope(chain.getScope());
            ModuleManager.setCurrentModule(chain.getModule());
            setScopeSwitchingChainName(null);
        }

        stackIndex = newStackIndex;
    }

    private void checkAccess(final IReceiverChain chain, final IMessageProcessor processor)
            throws ChainChoiceException {
        try {
            boolean isExternal = (boolean) chain.getChainDescription().getValue(this.externalAccessFieldName);
            Boolean fromExternal = (Boolean) processor.getContext().getValue(fromExternalFieldName);
            if (null != fromExternal && fromExternal) {
                processor.getContext().setValue(fromExternalFieldName, false);
                if (!isExternal) {
                    processor.getContext().setValue(this.accessForbiddenFieldName, true);
                    throw new ChainChoiceException("External access forbidden to chain - " + chain.getId() + ".");
                }
            }
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new ChainChoiceException("Access forbidden.", e);
        }
    }

    @Override
    public void catchException(final Throwable exception, final IObject context)
            throws NoExceptionHandleChainException,
                   NestedChainStackOverflowException,
                   ChangeValueException,
                   InvalidArgumentException,
                   ChainNotFoundException,
                   ResolutionException,
                   ReadValueException,
                   ScopeProviderException {
        int causedLevel = stackIndex;
        int causedStep = stepStack[causedLevel];
        int caughtLevel;
        int caughtStep;

        for (int i = stackIndex; i >= 0; --i) {
            IObject exceptionalChainAndEnv = chainStack[i].getExceptionalChainNamesAndEnvironments(exception);
            if (null != exceptionalChainAndEnv) {
                this.afterExceptionAction = (IAction<IMessageProcessingSequence>)exceptionalChainAndEnv.getValue(this.afterExceptionActionFieldName);
                caughtLevel = i;
                caughtStep = stepStack[caughtLevel];

                context.setValue(causeLevelFieldName, causedLevel);
                context.setValue(causeStepFieldName, causedStep);
                context.setValue(catchLevelFieldName, caughtLevel);
                context.setValue(catchStepFieldName, caughtStep);
                context.setValue(exceptionFieldName, exception);

                this.isException = true;
                Object chainName = exceptionalChainAndEnv.getValue(this.chainFieldName);
                Boolean scopeSwitching = (Boolean) exceptionalChainAndEnv.getValue(this.scopeSwitchingFieldName);
                if (scopeSwitching == null || scopeSwitching == true) {
                    setScopeSwitchingChainName(chainName);
                }
                callChain(chainName);
                return;
            }
        }

        throw new NoExceptionHandleChainException(exception,
                Arrays.copyOf(chainStack, stackIndex + 1),
                Arrays.copyOf(stepStack, stackIndex + 1));
    }

    /**
     * Create a description (dump) of current state of this sequence.
     *
     * <p>
     *     The returned object will look like the following:
     * </p>
     * <pre>
     *     {
     *         "stepsStack": [1, 3, 0, 2],               // Step indexes: steps[i] = getStepAtLevel(i)
     *         "chainsStack":                            // Names of chains at levels
     *              ["rootChain", "nestedChain",
     *              "otherChain", "oneMreChain"],
     *         "scopeSwitchingStack":
     *              [ true, true, false, true],
     *         "maxDepth": 5                             // Depth limit
     *     }
     * </pre>
     *
     * <p>
     *     Options may contain the following fields:
     * </p>
     * <ul>
     *     <li>{@code "excludeExceptional"} - {@code true} if exceptional chains should not be included in {@code "chainsDump"}</li>
     *     <li>{@code "skipChains"} - {@code true} if {@code "chainsDump"} should be empty</li>
     * </ul>
     *
     * @param options    dump creation options
     * @return the description of current sequence's state
     * TODO: Replace by serialization method
     */
    @Override
    public IObject dump(final IObject options) throws DumpException {
        try {
            IObject dump = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

            dump.setValue(maxDepthFieldName, chainStack.length);
            dump.setValue(stepsStackFieldName,
                    Arrays.stream(Arrays.copyOf(stepStack, stackIndex + 1)).boxed().collect(Collectors.toList()));
            dump.setValue(chainsStackFieldName,
                    Arrays.stream(Arrays.copyOf(chainStack, stackIndex + 1)).map(IReceiverChain::getName).collect(Collectors.toList()));

            dump.setValue(scopeSwitchingStackFieldName,
                    Arrays.stream(Arrays.copyOf(scopeSwitchingStack, stackIndex + 1)).collect(Collectors.toList()));

            // KAA
            // ToDo: should we put to dump the module and scope of module in which sequence was initialized
            // ToDo: to restore in this module and scope context

            return dump;
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException
                /*| ReadValueException | ChainNotFoundException*/ e) {
            throw new DumpException("Error occurred creating dump of message processing sequence.", e);
        }
    }
}
