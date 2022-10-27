package info.smart_tools.smartactors.field.field;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;

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
            throw new InvalidArgumentException("Argument should not be null.");
        }
        Object value = obj.getValue(fieldName);
        if (null == value) {
            return null;
        }

        return (T) value;
    }

    @Override
    public <T> T in(final IObject obj, final Class type)
            throws ReadValueException, InvalidArgumentException {
        if (null == obj || null == type) {
            throw new InvalidArgumentException("Argument should not be null.");
        }
        Object value = obj.getValue(fieldName);
        if (null == value) {
            return null;
        }
        if (type == value.getClass() || type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        try {
            return IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), type.getCanonicalName() + "convert"),
                    value
            );
        } catch (Throwable e) {
            throw new InvalidArgumentException("Could not cast value - " + value.getClass().getCanonicalName() + " to required type - " + type.getCanonicalName() + ".");
        }
    }

    @Override
    public <T> void out(final IObject obj, final T in)
            throws ChangeValueException, InvalidArgumentException {
        if (null == obj) {
            throw new InvalidArgumentException("Argument should not be null.");
        }
        obj.setValue(fieldName, in);
    }
}
