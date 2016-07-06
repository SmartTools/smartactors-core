package info.smart_tools.smartactors.actors.get_form.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for GetFormActor
 */
public interface ActorParams {
    /**
     *
     * @return name of cached collection
     * @throws ReadValueException
     */
    String getCollectionName() throws ReadValueException;
}
