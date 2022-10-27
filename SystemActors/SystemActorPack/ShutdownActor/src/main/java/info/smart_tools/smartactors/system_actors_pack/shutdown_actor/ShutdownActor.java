package info.smart_tools.smartactors.system_actors_pack.shutdown_actor;

import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.IllegalUpCounterState;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.system_actors_pack.shutdown_actor.wrapper.ForceShutdownRequestMessage;
import info.smart_tools.smartactors.system_actors_pack.shutdown_actor.wrapper.ShutdownRequestMessage;

public class ShutdownActor {
    private IUpCounter resolveUpCounter(final Object name)
            throws ResolutionException {
        return IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), name));
    }

    /**
     * Shutdown system.
     *
     * @param message    the request message
     * @throws ReadValueException if error occurs reading message fields
     * @throws ResolutionException if error occurs resolving upcounter
     * @throws UpCounterCallbackExecutionException if error occurs executing upcounter callback(s)
     * @throws IllegalUpCounterState if the system associated with upcounter is already down
     */
    public void shutdown(final ShutdownRequestMessage message)
            throws ReadValueException, ResolutionException, UpCounterCallbackExecutionException, IllegalUpCounterState {
        resolveUpCounter(message.getUpCounterName()).shutdown(message.getShutdownMode());
    }

    /**
     * Force shutdown system.
     *
     * @param message    the request message
     * @throws ReadValueException if error occurs reading message fields
     * @throws ResolutionException if error occurs resolving upcounter
     * @throws UpCounterCallbackExecutionException if error occurs executing upcounter callback(s)
     * @throws IllegalUpCounterState if the system associated with upcounter is already down
     */
    public void forceShutdown(final ForceShutdownRequestMessage message)
            throws ReadValueException, ResolutionException, UpCounterCallbackExecutionException, IllegalUpCounterState {
        resolveUpCounter(message.getUpCounterName()).forceShutdown();
    }
}
