package info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy;


import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Strategy for different deserialization logic of http request to message
 */
public interface IDeserializeStrategy <Type> {
    /**
     * Deserialize http request
     * @param request request
     * @return deserializated request
     * @throws DeserializationException if there are some problems on resolving
     */
    IObject deserialize(Type request) throws DeserializationException;
}
