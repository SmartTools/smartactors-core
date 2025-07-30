package info.smart_tools.smartactors.iobject.field_name;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;

import java.util.regex.Pattern;

/**
 * A {@code FieldName} passed to {@code IObject} methods
 * as name of needed field. This class checks the validity of the name.
 */
public class FieldName implements IFieldName, Comparable<IFieldName> {

    /**
     * Pattern with valid symbols for {@code FieldName}
     */
    private static final Pattern VALID_SYMBOLS = Pattern.compile("[\\wа-яА-ЯёЁ\\-\\+=\\|№!@#\\$%\\^&\\*:/\\.,«» \\{\\}\\(\\)\\[\\]]+");

    private String name;

    /**
     * Base constructor for {@code FieldName}
     * @param name is name of field, it must not be {@code null} and
     *             must contain at least one of the symbols from {@literal 0-9a-zA-Z_-+=|!@#$%^&*:/., {}()[]}
     * @throws InvalidArgumentException if name is not valid
     */
    public FieldName(final String name)
            throws InvalidArgumentException {
        initialize(name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldName fieldName = (FieldName) o;

        return name.equals(fieldName.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Initialize class property {@code FieldName}
     * @param nameValue pretender name for {@code FieldName}
     * @throws InvalidArgumentException if any errors occurred
     */
    protected void initialize(final String nameValue)
            throws InvalidArgumentException {
        if (null == nameValue) {
            throw new InvalidArgumentException("Name parameter must not be null");
        }
        if (nameValue.isEmpty()) {
            throw new InvalidArgumentException("Name parameter must not be empty");
        }
        if (!VALID_SYMBOLS.matcher(nameValue).matches()) {
            throw new InvalidArgumentException("Name parameter contains illegal symbols");
        }
        this.name = nameValue;
    }

    @Override
    public int compareTo(final IFieldName that) {
        return name.compareTo(that.toString());
    }
}
