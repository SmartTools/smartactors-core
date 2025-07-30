package info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_form_urlencoded;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * Strategy for deserialization message from post request with application/x-www-form-urlencoded content-type.
 * Default strategy.
 */
public class DeserializeStrategyPostFormUrlencoded implements IDeserializeStrategy<FullHttpRequest> {

    /**
     * Method, that deserialize application/x-www-form-urlencoded content of request
     * @param request Http request, that should be deserialize
     * @return {@link IObject} deserialized json
     * @throws DeserializationException if deserialization was failed
     */
    @Override
    public IObject deserialize(final FullHttpRequest request) throws DeserializationException {
        try {
            byte[] bytes = new byte[request.content().capacity()];

            for (int i = 0, size = request.content().capacity(); i < size; i++) {
                bytes[i] = request.content().getByte(i);
            }
            String string = new String(bytes);
            QueryStringDecoder decoder = new QueryStringDecoder(string, false);
            IObject message = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            decoder.parameters().forEach(
                    (k, v) -> {
                        try {
                            IFieldName fieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), k);
                            message.setValue(fieldName, v.get(0));
                        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            return message;
        } catch (ResolutionException e) {
            throw new DeserializationException("Failed to deserialize request", e);
        }
    }
}
