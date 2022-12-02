package info.smart_tools.smartactors.system_actors_pack.actors.object_enumeration_actor;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;

import java.util.List;

/**
 *
 */
public interface EnumerationResult {
    /**
     * Store result of enumeration.
     *
     * @param items    list of identifiers of enumerated objects
     * @throws ChangeValueException if errors occurs writing the value
     */
    void setItems(final List items) throws ChangeValueException;
}
