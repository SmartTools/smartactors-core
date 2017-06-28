package info.smart_tools.smartactors.base.iup_counter;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IPoorAction;
import info.smart_tools.smartactors.base.iup_counter.exception.IllegalUpCounterState;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;

/**
 * A counter that counts processes preventing system(or subsystem) shutdown.
 *
 * {@link IUpCounter Upcounter} also provides methods to require system shutdown, add callbacks to be executed on shutdown request or on
 * shutdown completion.
 *
 * {@link IUpCounter Upcounters} may be joined into hierarchy where shutdown completion of a child counter causes call to {@link #down()}
 * method of it's parent.
 */
public interface IUpCounter {
    /**
     * Notify the counter that one more process starts.
     *
     * @throws IllegalUpCounterState if the system is already down
     */
    void up() throws IllegalUpCounterState;

    /**
     * Notify the counter that one of processes is completed.
     *
     * @throws UpCounterCallbackExecutionException if system shuts down after this call and error occurs executing shutdown callback(s)
     * @throws IllegalUpCounterState if the system is already down
     * @throws IllegalUpCounterState if there is 0 processes running
     */
    void down() throws UpCounterCallbackExecutionException, IllegalUpCounterState;

    /**
     * Notify the counter that the system should shutdown.
     *
     * @param mode    shutdown mode
     * @throws UpCounterCallbackExecutionException if error occurs executing callbacks added using {@link #onShutdownRequest(IAction)}
     * @throws UpCounterCallbackExecutionException if system shuts down after this call and error occurs executing shutdown callback(s)
     * @throws IllegalUpCounterState if the system is already down
     */
    void shutdown(Object mode) throws UpCounterCallbackExecutionException, IllegalUpCounterState;

    /**
     * Forcibly shutdown the system (even if there are processes running).
     *
     * @throws UpCounterCallbackExecutionException if error occurs executing shutdown callbacks
     * @throws IllegalUpCounterState if the system is already down
     */
    void forceShutdown() throws UpCounterCallbackExecutionException, IllegalUpCounterState;

    /**
     * Add a callback to be called when shutdown request (call to {@link #shutdown(Object)}) occurs.
     *
     * @param callback    the callback to call, first argument of callback is the shutdown mode passed to {@link #shutdown(Object)}
     * @throws UpCounterCallbackExecutionException if there is a pending shutdown request and error occurs calling callback synchronously
     */
    void onShutdownRequest(IAction<Object> callback) throws UpCounterCallbackExecutionException;

    /**
     * Add a callback to be called when shutdown is done.
     *
     * A callback is guaranteed to be executed exactly once if {@code #onShutdownComplete(IPoorAction)} call finishes before first
     * {@link #shutdown(Object)} or {@link #forceShutdown()} shutdown call starts. Otherwise the callback will not be executed more than
     * once.
     *
     * @param callback    the callback
     * @throws UpCounterCallbackExecutionException if system is already down and error occurs executing callback synchronously
     */
    void onShutdownComplete(IPoorAction callback) throws UpCounterCallbackExecutionException;
}
