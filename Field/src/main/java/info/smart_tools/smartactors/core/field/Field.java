package info.smart_tools.smartactors.core.field;

import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Implementation of {@link IField}
 */
public class Field implements IField {

    private IFieldName fieldName;

    /**
     * Constructor.
     * Create instance of {@link Field}
     * @param fieldName name of {@code WDSObject} field
     * @throws InvalidArgumentException if incoming arguments are incorrect
     */
    public Field(final IFieldName fieldName)
            throws InvalidArgumentException {
        if (null == fieldName) {
            throw new InvalidArgumentException("FieldName should not be null");
        }
        this.fieldName = fieldName;
    }

    @Override
    public <T> T in(final IObject obj)
            throws ReadValueException, InvalidArgumentException {
        if (null == obj) {
            throw new InvalidArgumentException("WDSObject should not be null.");
        }

        return (T) obj.getValue(fieldName);
    }

    @Override
    public <T> T in(final IObject obj, final Class type)
            throws ReadValueException, InvalidArgumentException {
        if (null == obj || null == type) {
            throw new InvalidArgumentException("WDSObject should not be null.");
        }
        Object value = obj.getValue(fieldName);
        if (type == value.getClass()) {
            return (T) value;
        }
        try {
            return IOC.resolve(Keys.getOrAdd(type.getCanonicalName() + value.getClass().getCanonicalName()), value);
        } catch (Throwable e) {
            throw new InvalidArgumentException("Could not cast value to required type.");
        }
    }

    @Override
    public <T> void out(final IObject obj, final T in)
            throws ChangeValueException, InvalidArgumentException {
        if (null == obj) {
            throw new InvalidArgumentException("WDSObject should not be null.");
        }
        obj.setValue(fieldName, in);
    }
}
