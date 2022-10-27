package info.smart_tools.smartactors.debugger.interfaces;

import info.smart_tools.smartactors.debugger.interfaces.exceptions.BreakpointStorageException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.util.List;

/**
 * Object that stores list of breakpoints for a debugger session.
 */
public interface IDebuggerBreakpointsStorage {
    /**
     * Create new breakpoint.
     *
     * @param desc    description of the breakpoint
     * @return identifier of the created breakpoint
     * @throws BreakpointStorageException if error occurs creating the breakpoint
     */
    String addBreakpoint(IObject desc) throws BreakpointStorageException;

    /**
     * Check if debugger should pause processing of the message in current state of message processing sequence.
     *
     * @param sequence    the message processing sequence
     * @return {@code true} if debugger should break
     * @throws BreakpointStorageException if error occurs
     */
    boolean shouldBreakAt(IDebuggerSequence sequence) throws BreakpointStorageException;

    /**
     * Get list of descriptions of all breakpoints.
     *
     * @return list of breakpoint descriptions
     * @throws BreakpointStorageException if error occurs
     */
    List<IObject> listBreakpoints() throws BreakpointStorageException;

    /**
     * Modify exist breakpoint.
     *
     * @param id              identifier of the breakpoint
     * @param modification    object describing breakpoint modification
     * @throws BreakpointStorageException if error occurs modifying the breakpoint
     */
    void modifyBreakpoint(String id, IObject modification) throws BreakpointStorageException;
}
