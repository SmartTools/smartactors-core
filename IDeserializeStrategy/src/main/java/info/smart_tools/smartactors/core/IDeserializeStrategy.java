package info.smart_tools.smartactors.core;


import info.smart_tools.smartactors.core.exceptions.DeserializationException;
import info.smart_tools.smartactors.core.iobject.IObject;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Strategy for different deserialization logic of http request to message
 */
public interface IDeserializeStrategy {
    /**
     * Deserialize http request
     * @param request request
     * @return deserializated request
     * @throws DeserializationException if there are some problems on resolving
     */
    IObject deserialize(FullHttpRequest request) throws DeserializationException;
}
