package info.smart_tools.smartactors.core.iobject_simple_implementation;

import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple implementaion of {@link IObject}
 * <pre>
 * Main features:
 * - key-value storage
 * </pre>
 */
public class IObjectImpl implements IObject {

    private Map<IFieldName, Object> storage = new HashMap<>();

    @Override
    public Object getValue(final IFieldName name)
            throws ReadValueException {
        return storage.get(name);
    }

    @Override
    public void setValue(final IFieldName name, final Object value)
            throws ChangeValueException {
        storage.put(name, value);
    }

    @Override
    public void deleteField(final IFieldName name)
            throws DeleteValueException {
        storage.remove(name);
    }

    @Override
    public <T> T serialize()
            throws SerializeException {
        return (T) storage.toString();
    }

    @Override
    public Iterator<Map.Entry<IFieldName, Object>> iterator() {
        return null;
    }
}
