package info.smart_tools.smartactors.system_actors_pack.object_enumeration_actor.wrapper;

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
    void setItems(List items) throws ChangeValueException;
}
