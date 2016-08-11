package info.smart_tools.smartactors.core.deserialize_strategy_post_json;

import info.smart_tools.smartactors.core.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.core.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.core.iobject.IObject;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Strategy for deserialization message from post request with application/json content-type.
 * Default strategy.
 */
public class DeserializeStrategyPostJson implements IDeserializeStrategy {
    private final IMessageMapper<byte[]> messageMapper;

    /**
     * Constructor
     *
     * @param messageMapper message mapper for deserialize
     */
    public DeserializeStrategyPostJson(final IMessageMapper<byte[]> messageMapper) {
        this.messageMapper = messageMapper;
    }

    /**
     * Method, that deserialize json content of request
     *
     * @param inputRequest Http request, that should be deserialize
     * @return {@link IObject} deserializated json
     * @throws info.smart_tools.smartactors.core.ideserialize_strategy.exceptions.DeserializationException
     */
    @Override
    public IObject deserialize(final Object inputRequest) throws DeserializationException {
        FullHttpRequest request = (FullHttpRequest) inputRequest;
        byte[] bytes = new byte[request.content().capacity()];

        for (int i = 0, size = request.content().capacity(); i < size; i++) {
            bytes[i] = request.content().getByte(i);
        }
        try {
            return messageMapper.deserialize(bytes);
        } catch (ResolutionException e) {
            throw new DeserializationException("Failed to deserialize request", e);
        }
    }
}
