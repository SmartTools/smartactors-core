package info.smart_tools.smartactors.core.iobject;

import java.util.NoSuchElementException;

/**
 * Iterator over set of {@code IObject}`s fields.
 * Should be used like this:
 * <pre>
 * {@code
 *     IObject object = ...;
 *     IObjectIterator iterator = object.iterator();
 *     while(iterator.next()) {
 *         // ... do something using iterator.getName() and iterator.getValue() ...
 *     }
 * }
 * </pre>
 */
public interface IObjectIterator {
    /**
     * Makes iterator to point to the next field after current one.
     * Returns {@code false} if there is no more fields.
     *
     * @return {@code true} if iterator still points to an field.
     */
    boolean next();

    /**
     * Returns name of field iterator currently points to.
     * @throws NoSuchElementException when element is absent in current iterator position
     * @return name of field.
     */
    FieldName getName() throws NoSuchElementException;

    /**
     * Returns the value of field iterator currently points to.
     * @throws NoSuchElementException when element is absent in current iterator position
     * @return value of field
     */
    Object getValue() throws NoSuchElementException;
}
