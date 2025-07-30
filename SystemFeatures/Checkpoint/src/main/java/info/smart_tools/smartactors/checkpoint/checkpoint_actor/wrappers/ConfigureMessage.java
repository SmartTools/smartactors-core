package info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 *
 */
public interface ConfigureMessage {
    /**
     * Get configuration object.
     *
     * @return configuration object
     * @throws ReadValueException if error occurs reading value
     */
    IObject getConfig() throws ReadValueException;
}
