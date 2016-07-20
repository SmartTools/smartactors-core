package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

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
    public <T> T in(final IObject wdsObject)
            throws ReadValueException, InvalidArgumentException, ClassCastException {
        if (null == wdsObject) {
            throw new InvalidArgumentException("WDSObject should not be null.");
        }

        return (T) wdsObject.getValue(fieldName);
    }

    @Override
    public <T> void out(final IObject wdsObject, final T in)
            throws ChangeValueException, InvalidArgumentException {
        if (null == wdsObject) {
            throw new InvalidArgumentException("WDSObject should not be null.");
        }
        wdsObject.setValue(fieldName, in);
    }
}
