package info.smart_tools.smartactors.http_endpoint.http_response_deserialization_strategy;

import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.endpoint.interfaces.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Deserialization strategy for {@link FullHttpResponse}
 */
public class HttpResponseDeserializationStrategy implements IDeserializeStrategy<FullHttpResponse> {
    private final IMessageMapper<byte[]> messageMapper;

    /**
     * Constuctor
     *
     * @param messageMapper message mapper for this deserialization strategy
     */
    public HttpResponseDeserializationStrategy(final IMessageMapper<byte[]> messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public IObject deserialize(final FullHttpResponse response) throws DeserializationException {
        byte[] bytes = new byte[response.content().capacity()];

        for (int i = 0, size = response.content().capacity(); i < size; i++) {
            bytes[i] = response.content().getByte(i);
        }
        try {
            return messageMapper.deserialize(bytes);
        } catch (ResolutionException e) {
            throw new DeserializationException(e);
        }
    }
}
