package info.smart_tools.smartactors.core.message_to_bytes_mapper;


import com.google.common.base.Charsets;
import info.smart_tools.smartactors.core.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Implementation of {@link IMessageMapper} which map message to byte array
 */
public class MessageToBytesMapper implements IMessageMapper<byte[]> {

    /**
     * Deserialize bytes array of json string to IObject
     * @param serializedInput Array of bytes, that should be deserialize
     * @return {@link IObject} interpretation of serializedInput
     * @throws ResolutionException
     */
    @Override
    public IObject deserialize(final byte[] serializedInput) throws ResolutionException {
        String string = IOC.resolve(Keys.getOrAdd(String.class.toString()), serializedInput);
        return IOC.resolve(Keys.getOrAdd(DSObject.class.toString()), string);
    }

    @Override
    public byte[] serialize(final IObject message) {
        return message.toString().getBytes(Charsets.UTF_8);
    }
}
