package info.smart_tools.smartactors.endpoint_components_generic.handler_configuration_object;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Iterator;
import java.util.Map;

/**
 * {@link IObject} implementation used to serve links to endpoint configuration in pipeline step configuration.
 *
 * <p>
 *  This implementation stores array of underlying {@link IObject}'s. All write ({@link IObject#setValue(IFieldName, Object)}
 *  and {@link IObject#deleteField(IFieldName)}) calls are delegated to the first object in array. But when read
 *  ({@link IObject#getValue(IFieldName)}) call occurs this object tries to find first non-null value in all objects
 *  sequentially. If found value is a string starting with {@code @literal "@@"} prefix then it looks for value of field
 *  with name represented by a string that follows {@code @literal "@@"} in the first value.
 * </p>
 */
public class HandlerConfigurationObject implements IObject {
    private final static String LINK_PREFIX = "@@";

    private final IObject[] objects;

    public HandlerConfigurationObject(IObject[] objects)
            throws InvalidArgumentException {
        if (null == objects || objects.length < 1) {
            throw new InvalidArgumentException("Invalid objects array.");
        }

        this.objects = objects;
    }

    @Override
    public Object getValue(final IFieldName name)
            throws ReadValueException, InvalidArgumentException {
        int stop = 0;
        IFieldName fieldName = name;

        Object val = null;

        for (int i = 0; i < objects.length; i = (i + 1) % objects.length) {
            val = objects[i].getValue(fieldName);

            if (val instanceof String && ((String) val).startsWith(LINK_PREFIX)) {
                String nextName = ((String) val).substring(LINK_PREFIX.length());
                try {
                    fieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getName()), nextName);
                } catch (ResolutionException e) {
                    throw new ReadValueException("Error resolving linked field name ('"
                            + nextName + "') for field '" + name.toString() + "'.");
                }
                val = null;
                stop = i;
            } else if (i == stop || val != null) {
                break;
            }
        }

        return val;
    }

    @Override
    public void setValue(final IFieldName name, Object value)
            throws ChangeValueException, InvalidArgumentException {
        objects[0].setValue(name, value);
    }

    @Override
    public void deleteField(final IFieldName name)
            throws DeleteValueException, InvalidArgumentException {
        objects[0].deleteField(name);
    }

    @Override
    public <T> T serialize() throws SerializeException {
        throw new SerializeException("Serialization not implemented.");
    }

    @Override
    public Iterator<Map.Entry<IFieldName, Object>> iterator() {
        throw new UnsupportedOperationException("Iterator not implemented.");
    }
}
