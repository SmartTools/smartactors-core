package info.smart_tools.smartactors.core.ideserialize_strategy;


import info.smart_tools.smartactors.core.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Strategy for different deserialization logic of http request to message
 */
public interface IDeserializeStrategy<TRequest> {

    /**
     * Deserialize http request
     * @param request request
     * @return deserializated request
     * @throws DeserializationException if there are some problems on resolving
     */
    IObject deserialize(TRequest request) throws DeserializationException;
}
