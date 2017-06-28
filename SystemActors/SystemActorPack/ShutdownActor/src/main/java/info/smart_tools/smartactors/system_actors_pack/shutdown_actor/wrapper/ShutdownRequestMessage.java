package info.smart_tools.smartactors.system_actors_pack.shutdown_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 *
 */
public interface ShutdownRequestMessage {
    /**
     * @return shutdown mode identifier
     * @throws ReadValueException if error occurs reading value
     */
    Object getShutdownMode() throws ReadValueException;

    /**
     * @return name of up-counter
     * @throws ReadValueException if error occurs reading value
     */
    Object getUpCounterName() throws ReadValueException;
}
