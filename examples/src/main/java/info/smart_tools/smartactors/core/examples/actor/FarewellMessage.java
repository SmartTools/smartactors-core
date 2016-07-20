package info.smart_tools.smartactors.core.examples.actor;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Message for {@link HelloActor}.
 * Provides name. Receives farewell message.
 */
public interface FarewellMessage {

    /**
     * Returns the name of the person to say farewell.
     * @return name of the person
     * @throws ReadValueException when it's not possible to read value from the message
     */
    String getName() throws ReadValueException;

    /**
     * Sets the farewell message produced by the actor.
     * @param farewell the farewell message
     * @throws ChangeValueException when it's not possible to write value to the message
     */
    void setFarewell(String farewell) throws ChangeValueException;

}
