package info.smart_tools.smartactors.iobject_extension.configuration_object;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.ioc.IOC;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link @IObject}.
 * This implementation gets value on {@code getValue} method, leads it in to the canonical form and returns result.
 */
public class ConfigurationObject implements IObject {

    private final Map<IFieldName, Object> body;
    private final Map<IFieldName, Object> canonizedBody;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        SimpleModule module = new SimpleModule("Nested IObject serialization module");
        module.addDeserializer(Object.class, new ObjectDeserializer());
        module.addSerializer(new StdSerializer<IObject>(IObject.class) {
            @Override
            public void serialize(
                    final IObject iObject,
                    final JsonGenerator jsonGenerator,
                    final SerializerProvider serializerProvider
            )
                    throws IOException {
                try {
                    jsonGenerator.writeRawValue((String) iObject.serialize());
                } catch (SerializeException e) {
                    throw new IOException("Could not serialize DSObject.");
                }
            }
        });
        OBJECT_MAPPER.registerModule(module);
    }

    {
        this.canonizedBody = new ConcurrentHashMap<>();
    }

    /**
     * Serialize incoming string and create new instance of {@link ConfigurationObject}
     * @param body incoming string data
     * @throws InvalidArgumentException if any errors occurred on object creation
     */
    public ConfigurationObject(final String body)
            throws InvalidArgumentException {
        try {
            this.body = OBJECT_MAPPER.reader(new TypeReference<Map<FieldName, Object>>() { }).readValue(body);
        } catch (Throwable e) {
            throw new InvalidArgumentException(e);
        }
    }

    /**
     * Create new instance of {@link ConfigurationObject} by given body of pairs {@link IFieldName}, {@link Object}
     * @param objectEntries map of pairs {@link IFieldName}, {@link Object}
     * @throws InvalidArgumentException if argument is null
     */
    public ConfigurationObject(final Map<IFieldName, Object> objectEntries)
            throws InvalidArgumentException {
        if (null == objectEntries) {
            throw new InvalidArgumentException("Argument should not be null.");
        }
        this.body = new HashMap<>(0);
        this.body.putAll(objectEntries);
    }

    /**
     * Create empty instance of {@link ConfigurationObject}
     */
    public ConfigurationObject() {
        this.body = new HashMap<>(0);
    }

    @Override
    public Object getValue(final IFieldName name)
            throws ReadValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }
        try {
            if (canonizedBody.containsKey(name)) {
                return canonizedBody.get(name);
            }

            Object canonicalValue = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyStorage(), "resolve key for configuration object"),
                    name,
                    body.get(name)
            );

            // ConcurrentHashMap does not permit null-values
            if (null != canonicalValue) {
                canonizedBody.put(name, canonicalValue);
            }
            return canonicalValue;
        } catch (Throwable e) {
            throw new ReadValueException("Can't read value for current field name");
        }
    }

    @Override
    public void setValue(final IFieldName name, final Object value)
            throws ChangeValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }
        body.put(name, value);
        canonizedBody.remove(name);
    }

    @Override
    public void deleteField(final IFieldName name)
            throws DeleteValueException, InvalidArgumentException {
        throw new DeleteValueException("Method not implemented.");
    }

    @Override
    public <T> T serialize()
            throws SerializeException {
        try {
            for (IFieldName fieldName : body.keySet()) {
                if (!canonizedBody.containsKey(fieldName)) {
                    getValue(fieldName);
                }
            }

            return (T) OBJECT_MAPPER.writer().writeValueAsString(canonizedBody);
        } catch (ReadValueException | InvalidArgumentException | JsonProcessingException e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public Iterator<Map.Entry<IFieldName, Object>> iterator() {
        return null;
    }

    @Override
    public int hashCode() {
        return body.hashCode() * 31 + 42;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}

/**
 * Custom deserializer.
 * Cast all nested json objects to {@link IObject}.
 */
class ObjectDeserializer extends UntypedObjectDeserializer {

    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
        try {
            if (JsonTokenId.ID_START_OBJECT == jp.getCurrentTokenId()) {
                return new ConfigurationObject(
                        (Map<IFieldName, Object>) jp.readValueAs(new TypeReference<Map<FieldName, Object>>() { })
                );
            }
        } catch (Exception e) {
            return super.deserialize(jp, ctxt);
        }
        return super.deserialize(jp, ctxt);
    }
}