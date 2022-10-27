package info.smart_tools.smartactors.ioc.string_ioc_key;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.ikey.IKey;

/**
 * Implementation of {@link IKey}
 *
 * <pre>
 * This implementation has follow specific features:
 * - string unique identifier;
 * - overridden equals and hashCode method based on string property;
 * </pre>
 */
public class Key implements IKey {

    private String identifier;

    /**
     * Constructor with string unique identifier
     * @param identifier string unique identifier
     * @throws InvalidArgumentException if any errors occurred
     */
    public Key(final String identifier)
            throws InvalidArgumentException {
        if (null == identifier || identifier.isEmpty()) {
            throw new InvalidArgumentException("Value should not be empty or null.");
        }
        this.identifier = identifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Key key = (Key) o;

        return !(identifier != null ? !identifier.equals(key.identifier) : key.identifier != null);

    }

    @Override
    public int hashCode() {

        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
