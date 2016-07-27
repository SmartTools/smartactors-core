package info.smart_tools.smartactors.core.proof_of_assumption.perfomance_of_jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Json implementation of {@link IObject}
 */
public class DSObjectWithRecursiveIObject implements IObject {

    private Map<IFieldName, Object> body;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        SimpleModule module = new SimpleModule("Nested IObject serialization module");
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
                    throw new IOException("Could not serialize DSObjectWithRecursiveIObject.");
                }
            }
        });
        module.addDeserializer(Object.class, new ObjectDeserializer());
        OBJECT_MAPPER.registerModule(module);
    }

    /**
     * Create new instance of {@link DSObjectWithRecursiveIObject} by given body of pairs {@link IFieldName}, {@link Object}
     * @param objectEntries map of pairs {@link IFieldName}, {@link Object}
     * @throws InvalidArgumentException if argument is null
     */
    public DSObjectWithRecursiveIObject(final Map<IFieldName, Object> objectEntries)
            throws InvalidArgumentException {
        if (null == objectEntries) {
            throw new InvalidArgumentException("Argument should not be null.");
        }
        this.body = new HashMap<IFieldName, Object>(0);
        this.body.putAll( (HashMap<IFieldName, Object>) objectEntries);
    }

    /**
     * Serialize incoming string and create new instance of {@link DSObjectWithRecursiveIObject}
     * @param body incoming string data
     * @throws InvalidArgumentException if any errors occurred on object creation
     */
    public DSObjectWithRecursiveIObject(final String body)
            throws InvalidArgumentException {
        try {
            this.body = OBJECT_MAPPER.reader(new TypeReference<Map<FieldName, Object>>() { }).readValue(body);
        } catch (Throwable e) {
            throw new InvalidArgumentException(e);
        }
    }

    /**
     * Create empty instance of {@link DSObjectWithRecursiveIObject}
     */
    public DSObjectWithRecursiveIObject() {
        this.body = new HashMap<>(0);
    }

    @Override
    public Object getValue(final IFieldName name)
            throws ReadValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }
        return body.get(name);
    }

    @Override
    public void setValue(final IFieldName name, final Object value)
            throws ChangeValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }
        body.put(name, value);
    }

    @Override
    public void deleteField(final IFieldName name)
            throws DeleteValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }
        body.remove(name);
    }

    @Override
    public <T> T serialize()
            throws SerializeException {
        try {
            return (T) OBJECT_MAPPER.writer().writeValueAsString(body);
        } catch (Throwable e) {
            throw new SerializeException();
        }
    }

    @Override
    public Iterator<Map.Entry<IFieldName, Object>> iterator() {
        return new DSObjectIterator();
    }

    /**
     * Iterator over {@code body}
     */
    private final class DSObjectIterator implements Iterator<Map.Entry<IFieldName, Object>> {

        private Iterator<Map.Entry<IFieldName, Object>> iterator;

        private DSObjectIterator() {
            this.iterator = body.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public Map.Entry<IFieldName, Object> next() {
            return this.iterator.next();
        }
    } 
}

/**
 * Custom deserializer.
 * Cast all nested json objects to {@link IObject.
 */
class ObjectDeserializer extends UntypedObjectDeserializer {

    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
        try {
            if (JsonTokenId.ID_START_OBJECT == jp.getCurrentTokenId()) {
                return new DSObjectWithRecursiveIObject(
                        (Map<IFieldName, Object>) jp.readValueAs(new TypeReference<Map<FieldName, Object>>() { })
                );
            }
        } catch (Exception e) {
            return super.deserialize(jp, ctxt);
        }
        return super.deserialize(jp, ctxt);
    }
}
