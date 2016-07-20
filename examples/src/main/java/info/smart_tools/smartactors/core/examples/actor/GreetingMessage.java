package info.smart_tools.smartactors.core.examples.actor;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Message for {@link HelloActor}.
 * Provides name. Receives greeting message.
 */
public interface GreetingMessage {

    /**
     * Returns the name of the person to greet.
     * @return name of the person
     * @throws ReadValueException when it's not possible to read value from the message
     */
    String getName() throws ReadValueException;

    /**
     * Sets the greeting message produced by the actor.
     * @param greeting the greeting message
     * @throws ChangeValueException when it's not possible to write value to the message
     */
    void setGreeting(String greeting) throws ChangeValueException;

}
