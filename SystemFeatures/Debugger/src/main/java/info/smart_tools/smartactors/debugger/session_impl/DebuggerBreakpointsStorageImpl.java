package info.smart_tools.smartactors.debugger.session_impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerBreakpointsStorage;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSequence;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.BreakpointStorageException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IDebuggerBreakpointsStorage}.
 */
public class DebuggerBreakpointsStorageImpl implements IDebuggerBreakpointsStorage {
    /**
     * Breakpoint.
     */
    private final class Breakpoint {
        private IObject asIObject;
        private boolean enabled;

        private Breakpoint(final Object chainName, final int stepId, final String id, final boolean enabled, final IObject args)
                throws ResolutionException, ChangeValueException, InvalidArgumentException {
            this.enabled = enabled;

            this.asIObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            this.asIObject.setValue(chainFN, chainName);
            this.asIObject.setValue(stepFN, stepId);
            this.asIObject.setValue(enabledFN, true);
            this.asIObject.setValue(idFN, id);
            this.asIObject.setValue(argsFN, args);
        }

        private boolean isEnabled() {
            return enabled;
        }

        private void setEnabled(final boolean enabled)
                throws ChangeValueException, InvalidArgumentException, ResolutionException {
            this.enabled = enabled;
            this.asIObject.setValue(enabledFN, enabled);
        }

        private IObject getAsIObject() {
            return asIObject;
        }
    }

    private final IFieldName chainFN;
    private final IFieldName stepFN;
    private final IFieldName enabledFN;
    private final IFieldName idFN;
    private final IFieldName argsFN;

    private final Map<IObject, Breakpoint> breakpointByStepArgs = new HashMap<>();
    private final Map<String, Breakpoint> breakpointById = new HashMap<>();

    private int idCounter = 0;

    private String nextId() {
        return String.valueOf(++idCounter);
    }

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public DebuggerBreakpointsStorageImpl()
            throws ResolutionException {
        chainFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain");
        stepFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "step");
        enabledFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "enabled");
        idFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id");
        argsFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "args");
    }

    @Override
    public String addBreakpoint(final IObject desc) throws BreakpointStorageException {
        try {
            int stepId = ((Number) desc.getValue(stepFN)).intValue();
            Object chainName = (String) desc.getValue(chainFN);
            Object chainId = IOC.resolve(
                    Keys.getKeyByName("chain_id_from_map_name"),
                    chainName
            );
            IChainStorage chainStorage = IOC.resolve(Keys.getKeyByName(IChainStorage.class.getCanonicalName()));
            IReceiverChain chain = chainStorage.resolve(chainId);

            IObject stepArgs = chain.getArguments(stepId);

            if (null == stepArgs) {
                throw new BreakpointStorageException("Step index out of range.", null);
            }

            if (breakpointByStepArgs.containsKey(stepArgs)) {
                throw new BreakpointStorageException("There already is a breakpoint for that step.", null);
            }

            Boolean enabled = (Boolean) desc.getValue(enabledFN);

            if (null == enabled) {
                enabled = true;
            }

            String id = nextId();

            Breakpoint breakpoint = new Breakpoint(chainName, stepId, id, enabled, stepArgs);

            breakpointById.put(id, breakpoint);
            breakpointByStepArgs.put(stepArgs, breakpoint);

            return id;
        } catch (ChainNotFoundException e) {
            throw new BreakpointStorageException("No such chain to set breakpoint in.", null);
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | ChangeValueException e) {
            throw new BreakpointStorageException("Error creating breakpoint.", e);
        }
    }

    @Override
    public boolean shouldBreakAt(final IDebuggerSequence sequence) throws BreakpointStorageException {
        IObject stepArgs = sequence.getRealSequence().getCurrentReceiverArguments();

        return breakpointByStepArgs.containsKey(stepArgs) && breakpointByStepArgs.get(stepArgs).isEnabled();
    }

    @Override
    public List<IObject> listBreakpoints() throws BreakpointStorageException {
        List<IObject> lst = new ArrayList<>(breakpointById.size());

        for (Breakpoint bp : breakpointById.values()) {
            lst.add(bp.getAsIObject());
        }

        return lst;
    }

    @Override
    public void modifyBreakpoint(final String id, final IObject modification) throws BreakpointStorageException {
        Breakpoint bp = breakpointById.get(id);

        if (null == bp) {
            throw new BreakpointStorageException("No such breakpoint.", null);
        }

        try {
            Boolean enable = (Boolean) modification.getValue(enabledFN);

            if (null != enable) {
                bp.setEnabled(enable);
            }
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException | ResolutionException e) {
            throw new BreakpointStorageException("Error occurred modifying the breakpoint.", e);
        }
    }
}
